// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
	final static int ServerPort = 1234;

	public static void main(String args[]) throws UnknownHostException, IOException
	{
		Scanner scn = new Scanner(System.in);
		
		// getting localhost ip
		InetAddress ip = InetAddress.getByName("localhost");
		
		// establish the connection
		Socket s = new Socket(ip, ServerPort);
		
		// obtaining input and out streams
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());

		// sendMessage thread
		Thread sendMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {
				while (true) {

					
					
					try {
                        // read the message to deliver.
                        
                        String msg = scn.nextLine();

                     
                        dos.writeUTF(msg);
						if(msg.equals("logout")){
							break;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		// readMessage thread
		Thread readMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {

				while (true) {
					try {
						// read the message sent to this client
						String msg = dis.readUTF();
                        
						
						if(msg.equals("logout")){
							System.out.println("logging out....");
							break;
						}
						System.out.println(msg);
						
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		});

        while(true){
            try {
                    // read the message to deliver.
                    System.out.println("Enter Sender ID");
                    String msg = scn.nextLine();						
                    dos.writeUTF("auth#"+msg);

                    String msg1 = dis.readUTF();
                    
                     if(msg1.equals("valid")){
                        System.out.println("server : valid ID");
                        break;
                    }
                    System.out.println("server : invalid ID");
                } catch (IOException e) {
                    e.printStackTrace();
                }
               
        }
		sendMessage.start();
		readMessage.start();
        

	}
}
