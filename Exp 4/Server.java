import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.*;
import java.util.*;

class ClientServicer extends Thread
{
  final int serverSocket;
  final int clientSocket;

  ClientServicer(int serverSocket, int clientSocket)
  {
    this.serverSocket = serverSocket;
    this.clientSocket = clientSocket;
  }

  public void run()
  {
    try{
      DatagramSocket ds = new DatagramSocket(serverSocket);
      byte[] receive = new byte[65535];

      DatagramSocket dsSend = new DatagramSocket();
      InetAddress ip = InetAddress.getLocalHost();
      byte buf[] = null;

      System.out.println("Server socket opened for socket: " + serverSocket);
      DatagramPacket DpReceive = null;

      while(true)
      {
        DpReceive = new DatagramPacket(receive, receive.length);

        ds.receive(DpReceive);

        String receivedString = data(receive).toString();
        String sendString = "ack";

        System.out.println("Client " + clientSocket + ": " + receivedString);

        if(receivedString.equals("Exit"))
        {
          break;
        }

        if(receivedString.substring(0,4).equals("date"))
        {
          SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/YYYY z");
          Date date = new Date();
          formatDate.setTimeZone(TimeZone.getTimeZone(receivedString.substring(4)));

          sendString = formatDate.format(date).toString();
          buf = sendString.getBytes();

          DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, clientSocket);

          dsSend.send(DpSend);
        }

        if(receivedString.substring(0,4).equals("time"))
        {
          SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss z");
          Date date = new Date();
          formatDate.setTimeZone(TimeZone.getTimeZone(receivedString.substring(4)));

          sendString = formatDate.format(date).toString();
          buf = sendString.getBytes();

          DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, clientSocket);

          dsSend.send(DpSend);
        }

        receive = new byte[65535];

      }

      System.out.println("Socket " + serverSocket + " closed.");

    }
    catch(Exception e){
      // Exception Handling
    }
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


public class Server{
  public static void main(String[] args)
  {
    int n = 3;
    int[] serverSocketList = {5000, 5001, 5002};
    int[] clientSocketList = {8080, 8081, 8082};

    for(int i = 0; i < n; i++)
    {
      ClientServicer object = new ClientServicer(serverSocketList[i], clientSocketList[i]);
      object.start();
    }
  }
}
