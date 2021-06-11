import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Client {
    public static void printTime(){         // To get the current system time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]  ");
        System.out.print(sdf.format(cal.getTime()));
    }
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
            printTime();
            System.out.println(String.format("Connected to server @ %s:%d. Using local port %d.", host, port, socket.getLocalPort()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));       // Socket input stream 
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);                            // Socket output stream 
            String userInput;
            String serverResponse;

            while(true){
                System.out.print("\n >> ");
                userInput = in.readLine();
                socketOut.println(userInput);                   // Send user input as client requests to the server
                printTime();
                System.out.println("Client Request: " + userInput);
                if(userInput.equals("Exit")){
                    break;
                }
                serverResponse = socketIn.readLine();
                printTime();
                System.out.println("Server Response: " + serverResponse);
            }

            System.out.println("\nWaiting for exit confirmation from server...\n");

            do{         
                serverResponse = socketIn.readLine();
                printTime();
                System.out.println("Server Response: " + serverResponse);
            } while(!serverResponse.equals("Exit"));            // Wait till server confirms exit

            System.out.println("\nServer confirmation received. Client exiting.");
    
            // Close all stream objects and sockets
            in.close();
            socketIn.close();
            socketOut.close();
            socket.close();

            printTime();
            System.out.println("Connection terminated.\n");

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Could not find the specified host.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error in obtaining I/O.");
            System.exit(1);
        }   
        // The socket streams and the socket itself can also be closed implicitly as it is declared in a try-catch block.
    }
}