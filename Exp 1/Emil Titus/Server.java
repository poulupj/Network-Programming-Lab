import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server {
    public static void printTime(){         // To get the current system time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]  ");
        System.out.print(sdf.format(cal.getTime()));
    }
    public static void printName(){         // To print the program name in ASCII art form
        System.out.println("\n .d8888b.                                             \nd88P  Y88b                                            \nY88b.                                                 \n \"Y888b.    .d88b.  888d888 888  888  .d88b.  888d888 \n    \"Y88b. d8P  Y8b 888P\"   888  888 d8P  Y8b 888P\"   \n      \"888 88888888 888     Y88  88P 88888888 888     \nY88b  d88P Y8b.     888      Y8bd8P  Y8b.     888     \n \"Y8888P\"   \"Y8888  888       Y88P    \"Y8888  888\n");
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {             // Check for command line arguments
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }
        
        int port = Integer.parseInt(args[0]);
        printName();

        try {
            ServerSocket serverSocket = new ServerSocket(port);         // Create a new server socket
            printTime();
            System.out.println("Created server socket successfully. Listening on port " + port);
            Socket clientSocket = serverSocket.accept();                // Listen for incoming connections
            printTime();
            System.out.println("Established connection with client @ " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
            
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter socketOut = new PrintWriter(clientSocket.getOutputStream(), true);                          // Socket output stream                
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));     // Socket input stream
            String userInput;
            String clientMessage;
                        
            while(true){
                clientMessage = socketIn.readLine();            // Read client requests from socket input stream
                System.out.print("\n");
                printTime();
                System.out.println("Client Request: " + clientMessage);
                if(clientMessage.equals("Exit")){
                    break;
                }
                System.out.print(" >> ");
                userInput = in.readLine();
                socketOut.println(userInput);                   // Send server response through the socket output stream
                printTime();
                System.out.println("Server Response: " + userInput);
            }

            System.out.println("\nClient is waiting for exit confirmation from server...");
            
            do{
                System.out.print(" >> ");
                userInput = in.readLine();
                socketOut.println(userInput);
                printTime();
                System.out.println("Server Response: " + userInput);
            } while(!userInput.equals("Exit"));                 // Wait till server acknowledges the exit request
            
            System.out.println("\nServer acknowledges termination request. Server exiting.");

            // Close all stream objects and sockets
            in.close();     
            socketOut.close();
            socketIn.close();
            clientSocket.close();
            serverSocket.close();

            printTime();
            System.out.println("Connection terminated.\n");

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}