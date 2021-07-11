import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {
    public static void main(String[] args) throws Exception {
        InetAddress HOST = InetAddress.getByName("localhost");
        int PORT = 5000;
        try {
            DatagramSocket socket = new DatagramSocket();       // create UDP socket
            System.out.println("Client socket created. Binded to local port " + socket.getLocalPort());
            byte[] outBuffer = "speedtest".getBytes();          // dummy data to initiate connection
            DatagramPacket outPacket = new DatagramPacket(outBuffer, 0, outBuffer.length, HOST, PORT);
            byte[] inBuffer = new byte[60000];
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            socket.send(outPacket);                             // initiate connection
            long start = System.nanoTime();                     // start loggin time
            socket.receive(inPacket);                           // start data transfer
            
            long stop = System.nanoTime();                      // stop logging time
            long duration = (stop - start) / 1000;              // calculate stats
            double bytesTransferred = (inPacket.getLength() - inPacket.getOffset()) / 1024; 

            System.out.println("Time taken (microseconds): " + duration);
            System.out.println("Data transfered (KiB)    : " + bytesTransferred);

            double dataRate = (bytesTransferred / duration) * 1000000;
            System.out.println("Transfer Rate (KiB/s)    : " + String.format("%.2f", dataRate));
            socket.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage() + "\nThe socket could not be opened, or the socket could not bind to the specified port.");
        }
    }
}