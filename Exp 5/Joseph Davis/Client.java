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

    DatagramSocket ds = new DatagramSocket();

    InetAddress ip = InetAddress.getLocalHost();
    byte buf[] = null;

    DatagramSocket dsReceive = new DatagramSocket(clientSocket);
    byte[] receive = new byte[65535];

    DatagramPacket DpReceive = null;

    boolean loggedIn = false;
    int choice;

    String userName = "";
    String passWord = "";
    String toSend = "";
    String receivedString = "";

    while(true)
    {
      System.out.print("\033[H\033[2J");
      choice = 0;

      if(!loggedIn)
      {
        System.out.print("Email Application:\n\n1.Login\n2.Exit\nChoice: ");
        choice = sc.nextInt();

        if(choice == 1)
        {
          System.out.print("\033[H\033[2J");
          System.out.println("Email Application:\n");
          System.out.print("Username: ");
          sc.nextLine();
          userName = sc.nextLine();
          System.out.print("Password: ");
          passWord = sc.nextLine();
          toSend = "logIn" + '|' + userName + "|" + passWord + "|";

          buf = toSend.getBytes();
          DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, serverSocket);
          ds.send(DpSend);


          DpReceive = new DatagramPacket(receive, receive.length);
          dsReceive.receive(DpReceive);

          receivedString = data(receive).toString();

          if(receivedString.equals("true|||"))
          {
            loggedIn = true;
          }
          else if(receivedString.equals("false|||"))
          {
            loggedIn = false;
          }
        }
        else if(choice == 2)
        {
          loggedIn = false;
          toSend = "logOut|||";
        }
      }

      else if(loggedIn)
      {
        System.out.println("Email Application:");
        System.out.println("Welcome " + userName);

        System.out.print("\n1.Send\n2.Inbox\n3.Log out\nEnter choice: ");
        choice = sc.nextInt();

        if(choice == 1)
        {
          String to = "";
          String subject = "";
          String content = "";

          System.out.print("\033[H\033[2J");
          System.out.println("Email Application:");

          System.out.println("Compose:\n");
          sc.nextLine();
          System.out.print("To: ");
          to = sc.nextLine();
          System.out.print("Subject: ");
          subject = sc.nextLine();
          System.out.print("Content: ");
          content = sc.nextLine();

          toSend = "send" + "|" + to + "|" + subject + "|" + content;

          buf = toSend.getBytes();
          DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, serverSocket);
          ds.send(DpSend);
        }
        else if(choice == 2)
        {
          toSend = "inbox|||";

          buf = toSend.getBytes();
          DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, serverSocket);
          ds.send(DpSend);

          DpReceive = new DatagramPacket(receive, receive.length);
          dsReceive.receive(DpReceive);

          System.out.print("\033[H\033[2J");
          System.out.println("Email Application:");

          System.out.println("Inbox:\n");

          receivedString = data(receive).toString();

          if(receivedString.equals("Inbox Empty~"))
          {
            System.out.println("Inbox Empty\n");
          }
          else{

            int first = receivedString.indexOf("|");
            int second = receivedString.indexOf("|", first + 1);
            int third = receivedString.indexOf("~", second + 1);
            int start = 0;

            while(first != -1)
            {
              System.out.println("From: " + receivedString.substring(start, first));
              System.out.println("Subject: " + receivedString.substring(first + 1, second));
              System.out.println("Content: " + receivedString.substring(second + 1, third) + "\n");

              start = third + 1;
              first = receivedString.indexOf("|", third);
              second = receivedString.indexOf("|", first + 1);
              third = receivedString.indexOf("~", second + 1);
            }
          }

          System.out.println("Press enter to continue.");
          System.in.read();
        }
        else if(choice == 3)
        {
          loggedIn = false;
          toSend = "logOut|||";
        }
      }



      if(toSend.equals("logOut|||"))
      {
        buf = toSend.getBytes();
        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, serverSocket);
        ds.send(DpSend);

        break;
      }

      receive = new byte[65535];
    }

    System.out.println("Email Client exited");
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
