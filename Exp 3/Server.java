import java.io.*;
import java.net.*;

class ClientServicer extends Thread{
  final int socket;
  final int id;

  public ClientServicer(int socket, int id)
  {
      this.socket = socket;
      this.id = id;
  }

  public void run()
  {
    try{
      // Create server Socket
      ServerSocket ss = new ServerSocket(socket);

      System.out.println("Server socket created for Client: " + id);

      // connect it to client socket
      Socket s = ss.accept();
      System.out.println("Connection established from Client: " + id);
      Server.numberOfUsers++;

      // to send data to the client
      PrintStream ps
          = new PrintStream(s.getOutputStream());

      // to read data coming from the client
      BufferedReader br
          = new BufferedReader(
              new InputStreamReader(
                  s.getInputStream()));

      // server executes continuously
      while (true) {

          String strReceive, strSend = "Acknowledged";

          // repeat as long as the client
          // does not send an "Exit"

          // read from client
          while ((strReceive = br.readLine()) != null) {
              System.out.println("From Client" + id + ": " + strReceive);

              if(strReceive.equals("Exit"))
              {
                System.out.println("Client" + id + " Exiting.\nSocket " + id + " closed.");
                break;
              }
              else if(strReceive.equals("hi") || strReceive.equals("hello") || strReceive.equals("hello server"))
              {
                strSend = "Hello Client" + id;
              }
              else if(strReceive.equals("number of users"))
              {
                strSend = Server.numberOfUsers + " users are active.";
              }
              // send to client
              ps.println(strSend);
          }

          Server.numberOfUsers--;

          // close connection
          ps.close();
          br.close();
          s.close();
          ss.close();
    }
  }
    catch(Exception e){
      // Exception Handling
    }
  }
}

public class Server{
  public static int numberOfUsers = 0;

  public static void main(String[] args)
  {
    int n = 3;
    int[] socketList = {5000, 5001, 5002};

    for(int  i = 0; i < n; i++)
    {
      ClientServicer object = new ClientServicer(socketList[i], i + 1);
      object.start();
    }
  }
}
