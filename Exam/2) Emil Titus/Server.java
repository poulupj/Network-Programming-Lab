import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;

public class Server {
    public static void main(String[] args) throws Exception {
        final int PORT = 5000;
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            System.out.println("Server socket created. Binded to local port " + socket.getLocalPort());

            File payload = new File("test.jpg");
            byte[] outbuffer = Files.readAllBytes(payload.toPath());

            byte[] inBuffer = new byte[512];
            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(inPacket);                           // listen for any incoming connection

            DatagramPacket packet = new DatagramPacket(outbuffer, 0, outbuffer.length, inPacket.getSocketAddress());
            socket.send(packet);                                // send the payload as a reply

            socket.close();

        } catch (SocketException e) {
            System.out.println(e.getMessage()
                    + "\nThe socket could not be opened, or the socket could not bind to the specified port.");
        }
    }
}
