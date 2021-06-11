import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
        // Check for command line arguments
        if (args.length != 1) { 
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        printName();

        try {
            DatagramSocket socket = new DatagramSocket(port);
            printTime();
            System.out.println("Server socket created. Binded to local port " + socket.getLocalPort());

            // Create buffers to store the data from datagrams
            byte[] inBuffer = new byte[1024];
            byte[] outBuffer;
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            String userInput;
            String clientRequest;

            while(true){
                // Wait for an incoming datagram
                inPacket.setLength(inBuffer.length);
                socket.receive(inPacket);
                clientRequest = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());
                System.out.print("\n");
                printTime();
                System.out.println("Client Request: " + clientRequest);
                if(clientRequest.equals("Exit")){
                    break;
                }
                System.out.print(" >> ");
                userInput = in.readLine();
                outBuffer = userInput.getBytes();
                // Send a datagram to the client
                DatagramPacket outPacket = new DatagramPacket(outBuffer, 0, outBuffer.length, inPacket.getSocketAddress());
                socket.send(outPacket);
                printTime();
                System.out.println("Server Response: " + userInput);
            }

            System.out.println("\nClient is waiting for exit confirmation from server...");
            
            // Wait till server acknowledges the exit request
            do{
                System.out.print("\n >> ");
                userInput = in.readLine();
                outBuffer = userInput.getBytes();
                DatagramPacket outPacket = new DatagramPacket(outBuffer, 0, outBuffer.length, inPacket.getSocketAddress());
                socket.send(outPacket);
                printTime();
                System.out.println("Server Response: " + userInput);
            } while(!userInput.equals("Exit"));                 
            
            System.out.println("\nServer acknowledges exit request. Server exiting.\n");

            // Close stream object and socket
            in.close();     
            socket.close();

        } catch (SocketException e) {
            System.out.println(e.getMessage() + "\nThe socket could not be opened, or the socket could not bind to the specified port.");        }
    }
}
