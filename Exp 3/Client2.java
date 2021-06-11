// Client class that
// sends data and receives also

import java.io.*;
import java.net.*;

class Client2 {

    public static void main(String args[])
        throws Exception
    {

        // Create client socket
        Socket s = new Socket("localhost", 5001);

        System.out.println("Client socket created.");

        // to send data to the server
        DataOutputStream dos
            = new DataOutputStream(
                s.getOutputStream());

        // to read data coming from the server
        BufferedReader br
            = new BufferedReader(
                new InputStreamReader(
                    s.getInputStream()));

        // to read data from the keyboard
        BufferedReader kb
            = new BufferedReader(
                new InputStreamReader(System.in));
        String strSend, strReceive;

        // repeat as long as exit
        // is not typed at client
        while (true) {

  	    strSend = kb.readLine();
            // send to the server
            dos.writeBytes(strSend + "\n");

            if(strSend.equals("Exit"))
            {
              System.out.println("Client Exiting");
              break;
            }

            // receive from the server
            strReceive = br.readLine();

            System.out.println("From Server: " + strReceive);
        }

  	System.out.println("Client Exit");

        // close connection.
        dos.close();
        br.close();
        kb.close();
        s.close();
    }
}
