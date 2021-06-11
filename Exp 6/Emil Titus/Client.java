import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client {
    public static void printName(){         // To print the program name in ASCII art form
        System.out.println("\n .d8888b.  888 d8b                   888    \nd88P  Y88b 888 Y8P                   888    \n888    888 888                       888    \n888        888 888  .d88b.  88888b.  888888 \n888        888 888 d8P  Y8b 888 \"88b 888    \n888    888 888 888 88888888 888  888 888    \nY88b  d88P 888 888 Y8b.     888  888 Y88b.  \n \"Y8888P\"  888 888  \"Y8888  888  888  \"Y888\n");
    }
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        printName();

        try {
            Socket socket = new Socket(host, port);     // Try to connect to the server socket at the specified address
            System.out.println(String.format("Connected to File Server @ %s:%d. Using local port %d.", host, port, socket.getLocalPort()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));       // Socket input stream 
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);                            // Socket output stream 
            DataInputStream fileSocketIn = new DataInputStream(socket.getInputStream());
            DataOutputStream fileSocketOut = new DataOutputStream(socket.getOutputStream());
            String userInput, password, fileName;
            long fileSize;
            byte[] buffer;
            while (true) {
                System.out.print("\n >> ");
                userInput = in.readLine();
                switch (userInput) {
                    case "auth":
                        socketOut.println("auth");
                        System.out.println("Enter authentication token: ");
                        password = in.readLine();
                        socketOut.println(password);
                        if(socketIn.readLine().equals("202")) {
                            System.out.println("Successfully logged in to the file server.");
                        }
                        else {
                            System.out.println("Error 403: Invalid Token");
                        }
                        break;
                    case "get":
                        socketOut.println("get");
                        System.out.println(socketIn.readLine());
                        if(socketIn.readLine().equals("403")) {
                            System.out.println("Error 403: Access Denied.");
                            break;
                        }
                        System.out.print("Enter File Name: ");
                        fileName = in.readLine();
                        socketOut.println(fileName);
                        if(socketIn.readLine().equals("404")) {
                            System.out.println("Error 404: File not found.");
                            break;
                        }
                        fileSize = Long.parseLong(socketIn.readLine());
                        buffer = new byte[(int)fileSize];
                        fileSocketIn.read(buffer, 0, buffer.length);
                        Files.write(Paths.get("client-storage/" + fileName), buffer);
                        if(socketIn.readLine().equals("200")){
                            System.out.println("Status Code: 200 (OK). File Transfer Successful.");
                        }
                        break;
                    case "put":
                        System.out.print("Enter File Name: ");
                        fileName = in.readLine();
                        File file = new File("client-storage/" + fileName);
                        if (!file.isFile() || !file.canRead()){
                            System.out.println("File not found in local storage.");
                            file.delete();
                            break;
                        }
                        socketOut.println("put");
                        System.out.println(socketIn.readLine());
                        if(socketIn.readLine().equals("403")) {
                            System.out.println("Error 403: Access Denied.");
                            break;
                        }
                        socketOut.println(fileName);
                        if(socketIn.readLine().equals("400")) {
                            System.out.println("Error 400: File already exists.");
                            break;
                        }
                        
                        fileSize = file.length();
                        socketOut.println(fileSize);
                        buffer = Files.readAllBytes(file.toPath());
                        fileSocketOut.write(buffer, 0, buffer.length);
                        fileSocketOut.flush();
                        if(socketIn.readLine().equals("200")){
                            System.out.println("Status Code: 200 (OK). File Transfer Successful.");
                        }
                        break;
                    case "exit":
                        socketOut.println("exit");;
                        System.out.println("Connection with file server terminated. Exiting...\n");
                        System.exit(0);
                    default:
                        System.out.println("Available Commands: auth, get, put, exit");
                        break;
                }
            }
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Could not find the specified host.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error in obtaining I/O.");
            System.exit(1);
        }
    }
}
