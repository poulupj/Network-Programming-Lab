import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.SocketException;

public class Client
{
  public static void main(String args[]) throws IOException
  {
    Scanner sc = new Scanner(System.in);

    int serverSocket = Integer.parseInt(args[0]);
    int clientSocket = Integer.parseInt(args[1]);
    String timeZone = args[2];

    DatagramSocket ds = new DatagramSocket();

    InetAddress ip = InetAddress.getLocalHost();
    byte buf[] = null;

    DatagramSocket dsReceive = new DatagramSocket(clientSocket);
    byte[] receive = new byte[65535];

    System.out.println("Client Created.\n");

    DatagramPacket DpReceive = null;

    while(true)
    {
      String inp = sc.nextLine();

      if(inp.equals("Exit") == false){
        inp = inp + timeZone;
      }

      buf = inp.getBytes();

      DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, serverSocket);

      ds.send(DpSend);

      if(inp.equals("Exit"))
      {
        break;
      }

      DpReceive = new DatagramPacket(receive, receive.length);

      dsReceive.receive(DpReceive);

      System.out.println("Server: " + data(receive));

      receive = new byte[65535];
    }

    System.out.println("Client exited");
  }

  public static StringBuilder data(byte[] a)
  {
    if (a == null)
    return null;
    StringBuilder ret = new StringBuilder();
    int i = 0;
    while (a[i] != 0)
    {
      ret.append((char) a[i]);
      i++;
    }
    return ret;
  }
}
