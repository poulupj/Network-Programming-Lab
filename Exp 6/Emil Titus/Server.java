import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

public class Server {
    public static void printName(){         // To print the program name in ASCII art form
        System.out.println("\n .d8888b.                                             \nd88P  Y88b                                            \nY88b.                                                 \n \"Y888b.    .d88b.  888d888 888  888  .d88b.  888d888 \n    \"Y88b. d8P  Y8b 888P\"   888  888 d8P  Y8b 888P\"   \n      \"888 88888888 888     Y88  88P 88888888 888     \nY88b  d88P Y8b.     888      Y8bd8P  Y8b.     888     \n \"Y8888P\"   \"Y8888  888       Y88P    \"Y8888  888\n");
    }
    public static void main(String[] args) throws Exception {
        int port = 5000;
        String[] tokens = new String[]{"userpass", "adminpass"};
        printName();
        System.out.println("Tokens:" + Arrays.toString(tokens));
        try {
            
            ServerSocket serverSocket = new ServerSocket(port);         // Create a new server socket
            System.out.println("Created server socket successfully. File Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();                // Listen for incoming connections
                Handler thread = new Handler(clientSocket, tokens);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    } 
}

class Handler extends Thread {
    Socket socket;
    boolean loggedIn;
    String[] tokens;

    public Handler(Socket socket, String[] tokens){
        this.socket = socket;
        this.loggedIn = false;
        this.tokens = tokens;
    }

    String getPIDTID(){         
        // To get the Process ID and the current thread's Thread ID
        long TID = Thread.currentThread().getId();
        long PID = ProcessHandle.current().pid();
        return "Process ID: " + PID + " |  Thread ID: " + TID;
    }

    String tryLogin(String password){
        for (String token : tokens) {
            if (token.equals(password)){
                loggedIn = true;
                System.out.println("Client successfully authenticated with: " + Thread.currentThread().getName());
                return "202";   // SUCCESS 202: ACCEPTED
            }
        }
        return "403";           // ERROR 403: FORBIDDEN
    }

    public void run() {
        try {
            PrintWriter socketOut = new PrintWriter(this.socket.getOutputStream(), true);                          // Socket output stream                
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));     // Socket input stream
            DataOutputStream fileSocketOut = new DataOutputStream(this.socket.getOutputStream());
            DataInputStream fileSocketIn = new DataInputStream(this.socket.getInputStream());
            String clientMessage, fileName, password;
            long fileSize;
            byte[] buffer;
            File file;

            System.out.println("Established connection with client @ " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());

            while (true){
                clientMessage = socketIn.readLine().toLowerCase();            // Read client requests from socket input stream
                switch (clientMessage) {
                    case "auth":
                        password = socketIn.readLine();
                        socketOut.println(tryLogin(password));
                        break;
                    case "get":
                        socketOut.println(getPIDTID());
                        if (!loggedIn){
                            socketOut.println("403");   // ERROR 403: FORBIDDEN
                            break;
                        }
                        socketOut.println("202");      // SUCCESS 202: ACCEPTED
                        fileName = socketIn.readLine();
                        file = new File("server-storage/" + fileName);
                        if (!file.isFile() || !file.canRead()){
                            socketOut.println("404");   // ERROR 404: NOT FOUND
                            file.delete();
                            break;
                        }
                        fileSize = file.length();
                        socketOut.println("202");      // SUCCESS 202: ACCEPTED
                        socketOut.println(fileSize);
                        buffer = Files.readAllBytes(file.toPath());
                        fileSocketOut.write(buffer, 0, buffer.length);
                        fileSocketOut.flush();
                        socketOut.println("200");      // SUCCESS 200: OK
                        break;
                    case "put":
                        socketOut.println(getPIDTID());
                        if (!loggedIn){
                            socketOut.println("403");   // ERROR 403: FORBIDDEN
                            break;
                        }
                        socketOut.println("202");      // SUCCESS 202: ACCEPTED
                        fileName = socketIn.readLine();
                        file = new File("server-storage/" + fileName);
                        if (file.exists()){
                            socketOut.println("400");   // ERROR 403: BAD REQUEST (File already exists)
                            break;
                        }
                        socketOut.println("202");      // SUCCESS 202: ACCEPTED
                        fileSize = Long.parseLong(socketIn.readLine());
                        buffer = new byte[(int)fileSize];
                        fileSocketIn.read(buffer, 0, buffer.length);
                        Files.write(file.toPath(), buffer);
                        socketOut.println("200");      // SUCCESS 200: OK
                        break;
                    case "exit":
                        socketOut.close();
                        socketIn.close();
                        socket.close();
                        throw new Terminated();                
                    default:
                        socketOut.println("501");   // ERROR 501: NOT IMPLEMENTED (Invalid command)
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Terminated e) {
            System.out.println("Client exited. " + Thread.currentThread().getName() + " terminated.");
        }
    }
}

class Terminated extends Exception {
    public Terminated(){
        super("Connection terminated.");
    }
}