import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.*;
import java.util.*;

class Account
{
  static String[] userNames = {"abc@gmail.com", "xyz@gmail.com", "pqr@gmail.com"};
  static String[] passWords = {"abc", "xyz", "pqr"};

  static String[][] inbox = new String[userNames.length][100];
  static int[] inboxPointer = new int[userNames.length];

  public static void initialize()
  {
    for(int i = 0; i < userNames.length; i++)
    {
      inboxPointer[i] = -1;
    }
  }

  public static boolean validate(String userName, String passWord)
  {
    for(int i = 0; i < userNames.length; i++)
    {
      if(userName.equals(userNames[i]) && passWord.equals(passWords[i]))
      {
        return true;
      }
    }

    return false;
  }

  public static void send(String from, String to, String subject, String content)
  {
    int receiver = -1;

    for(int i = 0; i < userNames.length; i++)
    {
      if(to.equals(userNames[i]))
      {
        receiver = i;
        break;
      }
    }

    if(receiver < 0)
    {
      System.out.println("Invalid to address");
      return;
    }
    else
    {
      inboxPointer[receiver]++;
      inbox[receiver][inboxPointer[receiver]] = from + "|" + subject + "|" + content;
      System.out.println("Email sent from " + from + " to " + to);

      return;
    }
  }


  public static String inbox(String userName)
  {
    int user = -1;

    for(int i = 0; i < userNames.length; i++)
    {
      if(userName.equals(userNames[i]))
      {
        user = i;
        break;
      }
    }

    if(inboxPointer[user] < 0)
    {
      return "Inbox Empty~";
    }
    else
    {
      String messages = "";

      for(int i = 0; i <= inboxPointer[user]; i++ )
      {
        messages = messages + inbox[user][i] + "~";
      }

      inboxPointer[user] = -1;

      return messages;
    }
  }
}


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

      DatagramPacket DpReceive = null;

      boolean loggedIn = false;

      String userName = "";
      String passWord = "";

      while(true)
      {
        DpReceive = new DatagramPacket(receive, receive.length);

        ds.receive(DpReceive);

        String receivedString = data(receive).toString();
        String toSend = "";
        String command = "";
        int first, second, third;

        first = receivedString.indexOf('|');
        second = receivedString.indexOf('|', first + 1);
        third = receivedString.indexOf('|', second + 1);
        command = receivedString.substring(0, first);

        if(!loggedIn)
        {
          if(command.equals("logIn"))
          {
            userName = receivedString.substring(first + 1, second);
            passWord = receivedString.substring(second + 1, third);

            loggedIn = Account.validate(userName, passWord);

            if(loggedIn)
            {
              System.out.println(userName + " logged in.");
              toSend = "true|||";
            }
            else
            {
              System.out.println("Invalid log in attempt.");
              toSend = "false|||";
            }

            buf = toSend.getBytes();
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, clientSocket);
            dsSend.send(DpSend);
          }
          else if(command.equals("logOut"))
          {
            loggedIn = false;
            break;
          }
        }

        else if(loggedIn)
        {
          if(command.equals("send"))
          {
            String to = receivedString.substring(first + 1, second);
            String subject = receivedString.substring(second + 1, third);
            String content = receivedString.substring(third + 1, receivedString.length());

            Account.send(userName, to, subject, content);
          }
          else if(command.equals("inbox"))
          {
            toSend = Account.inbox(userName);

            buf = toSend.getBytes();
            DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, clientSocket);
            dsSend.send(DpSend);
          }
          else if(command.equals("logOut"))
          {
            loggedIn = false;
            System.out.println(userName + " logged out.");
            break;
          }
        }

        receive = new byte[65535];
      }
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
    Account.initialize();

    System.out.println("Email Server open.");

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
