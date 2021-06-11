import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Client {
    public static void printTime(){         
        // To get the current system time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]  ");
        System.out.print(sdf.format(cal.getTime()));
    }
    public static void printName(){         
        // To print the program name in ASCII art form
        System.out.println("\n .d8888b.  888 d8b                   888    \nd88P  Y88b 888 Y8P                   888    \n888    888 888                       888    \n888        888 888  .d88b.  88888b.  888888 \n888        888 888 d8P  Y8b 888 \"88b 888    \n888    888 888 888 88888888 888  888 888    \nY88b  d88P 888 888 Y8b.     888  888 Y88b.  \n \"Y8888P\"  888 888  \"Y8888  888  888  \"Y888\n");
    }
    public static void main(String[] args) throws Exception {
        // Check for command line arguments
        if (args.length != 2) {
           System.err.println("Usage: java Client <host> <port>");
           System.exit(1);
        }

        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        printName();

        try {
            DatagramSocket socket = new DatagramSocket();
            printTime();
            System.out.println("Client socket created. Binded to local port " + socket.getLocalPort());

            // Create buffers to store the data from datagrams
            byte[] inBuffer = new byte[1024];
            byte[] outBuffer;
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            String userInput;
            String serverResponse;

            while(true){
                System.out.print("\n >> ");
                userInput = in.readLine();
                outBuffer = userInput.getBytes();
                // Send a datagram to the server
                DatagramPacket outPacket = new DatagramPacket(outBuffer, 0, outBuffer.length, host, port);
                socket.send(outPacket);
                printTime();
                System.out.println("Client Request: " + userInput);
                if(userInput.equals("Exit")){
                    break;
                }
                // Wait for an incoming datagram
                inPacket.setLength(inBuffer.length);
                socket.receive(inPacket);
                serverResponse = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());
                printTime();
                System.out.println("Server Response: " + serverResponse);
            }
            
            System.out.println("\nWaiting for exit confirmation from server...\n");

            // Wait till server confirms exit
            do{         
                inPacket.setLength(inBuffer.length);
                socket.receive(inPacket);
                serverResponse = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());
                printTime();
                System.out.println("Server Response: " + serverResponse);
            } while(!serverResponse.equals("Exit"));            

            System.out.println("\nServer confirmation received. Client exiting.\n");

            // Close stream object and socket
            in.close();
            socket.close();

        } catch (SocketException e) {
            System.out.println(e.getMessage() + "\nThe socket could not be opened, or the socket could not bind to the specified port.");
        }
    }
}