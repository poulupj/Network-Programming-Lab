import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void printName(){         // To print the program name in ASCII art form
        System.out.println("\n .d8888b.  888 d8b                   888    \nd88P  Y88b 888 Y8P                   888    \n888    888 888                       888    \n888        888 888  .d88b.  88888b.  888888 \n888        888 888 d8P  Y8b 888 \"88b 888    \n888    888 888 888 88888888 888  888 888    \nY88b  d88P 888 888 Y8b.     888  888 Y88b.  \n \"Y8888P\"  888 888  \"Y8888  888  888  \"Y888\n");
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {             // Check for command line arguments
            System.err.println(
                "Usage: java Client <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        printName();

        try {
            Socket socket = new Socket(host, port);     // Try to connect to the server socket at the specified address
            System.out.println(String.format("Connected to SMTP server @ %s:%d. Using local port %d.", host, port, socket.getLocalPort()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));       // Socket input stream 
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);                            // Socket output stream 
            String userInput, serverResponse, email, destination, subject, message;

            while (true) {
                System.out.print("\n >> ");
                userInput = in.readLine();
                switch (userInput) {
                    case "login":
                        System.out.print("Email ID: ");
                        email = in.readLine();
                        socketOut.println("login " + email);
                        serverResponse = socketIn.readLine();
                        if(serverResponse.equals("success")){
                            System.out.println("Logged in successfully.");
                        }
                        else {
                            System.out.println(serverResponse);
                        }
                        break;
                    case "exit":
                        socketOut.println("logout");
                        System.out.println("Logged out and connection terminated.\n");
                        in.close();
                        socketIn.close();
                        socketOut.close();
                        socket.close();
                        System.exit(0);
                    case "send":
                        System.out.print("To: ");
                        destination = in.readLine();
                        socketOut.println("to " + destination);
                        serverResponse = socketIn.readLine();
                        if(serverResponse.equals("valid")){
                            System.out.print("Subject: ");
                            subject = in.readLine();
                            socketOut.println(subject);
                            System.out.print("Message: ");
                            message = in.readLine();
                            socketOut.println(message);
                            System.out.println(socketIn.readLine());
                        }
                        else {
                            System.out.println(serverResponse);
                        }
                        break;
                    case "receive":
                        socketOut.println("receive");
                        serverResponse = socketIn.readLine();
                        if(serverResponse.equals("available")){
                            System.out.println(socketIn.readLine());
                            System.out.println(socketIn.readLine());
                            System.out.println(socketIn.readLine());
                            System.out.println(socketIn.readLine());
                        }
                        else {
                            System.out.println(serverResponse);
                        }
                        break;  
                    default:
                        System.out.println("Available Commands: login, send, receive, exit");
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
