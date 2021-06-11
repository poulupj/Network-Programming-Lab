// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;

public class Client
{
	final static int ServerPort = 1234;

	public static void main(String args[]) throws UnknownHostException, IOException
	{
		Scanner scn = new Scanner(System.in);
		Queue<String> q = new LinkedList<>();
		
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
            // Thread stop
            boolean exit=false;

			@Override
			public void run() {
				while (!exit) {
					// System.out.print("Type your message: ");
					// read the message to deliver.
					String msg = scn.nextLine();
					try {
                        if(msg.equals("logout")){
                            exit=true;
                        }
						// write on the output stream
						dos.writeUTF(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
            public void stop(){
                exit=true;
            }
		});
		
		// readMessage thread
		Thread readMessage = new Thread(new Runnable()
		{

            // Thread stop
            boolean exit=false;

			@Override
			public void run() {

				while (!exit) {
					try {
						// read the message sent to this client
						String msg = dis.readUTF();
						if(!q.isEmpty() && msg.equals("#readBufferedMessages#")){
							while(!q.isEmpty()){
								String removedele = q.remove();
        						System.out.println("You have unread messages from "+ removedele.replace("#addToBuffer#",""));
							}
						}else if(msg.contains("#addToBuffer#")){
							q.add(msg);
						}else if(msg.equals("logout")){
						    System.out.println("Client logging out....");
                            exit=true;
                        }else if(!msg.equals("#readBufferedMessages#")){
							System.out.println(msg);
						}
                        
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
            public void stop(){
                exit=true;
            }
		});

		sendMessage.start();
		readMessage.start();

	}
}
