// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{

	// Vector to store active clients
	static Vector<ClientHandler> ar = new Vector<>();
	
	// counter for clients
	static int i = 0;

	public static void main(String[] args) throws IOException
	{
		// server is listening on port 1234
		ServerSocket ss = new ServerSocket(1234);
		
		Socket s;
		
		// running infinite loop for getting
		// client request
		while (true)
		{
			// Accept the incoming request
			s = ss.accept();

			System.out.println("New client request received : " + s);
			
			// obtain input and output streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			System.out.println("Creating a new handler for this client...");

			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

			// Create a new Thread with this object.
			Thread t = new Thread(mtch);
			
			System.out.println("Adding this client to active client list");

			// add this client to active clients list
			ar.add(mtch);

			// start the thread.
			t.start();

			// increment i for new client.
			// i is used for naming only, and can be replaced
			// by any naming scheme
			i++;

		}
	}
}

// ClientHandler class
class ClientHandler implements Runnable
{
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	List<String> list=new ArrayList<String>();
	// constructor
	public ClientHandler(Socket s, String name,
							DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
		this.isloggedin=false;
        this.list=list;
	}

	@Override
	public void run() {

		String received;
		while (true)
		{
			try
			{
				// receive the string
				received = dis.readUTF();
				
				System.out.println(received);
				
				if(received.equals("logout")){
					this.isloggedin=false;
					dos.writeUTF("logout");
					break;
				}
				
				// break the string into message and recipient part
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.nextToken();
				String recipient = st.nextToken();
                if(MsgToSend.equals("auth")){
                    if(recipient.equals(name)){
                        this.isloggedin=true;
                        dos.writeUTF("valid");
                        Iterator iterator = list.iterator();
                        while(iterator.hasNext()) {
                            // System.out.println(iterator.next());
                            String temp1=iterator.next().toString();
                            StringTokenizer temp = new StringTokenizer(temp1, "#");
                            String qmsg=temp.nextToken();
                            String qsend=temp.nextToken();
                            dos.writeUTF(qsend+" : "+qmsg);
                        }
                        list.clear();
                        
                    }
                    else{
                        dos.writeUTF("invalid");
                    }
                    continue;
                }
				// search for the recipient in the connected devices list.
				// ar is the vector storing client of active users
                boolean status=true;
				for (ClientHandler mc : Server.ar)
				{
					// if the recipient is found, write on its
					// output stream
					if (mc.name.equals(recipient) && mc.isloggedin==true)
					{
						mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        status=false;
						break;
                        
					}else if (mc.name.equals(recipient) && mc.isloggedin==false)
					{
						dos.writeUTF("server : machine not logged in");
                        status=false;
                        mc.list.add(MsgToSend+"#"+this.name);
						break;
					}

				}
                if(status){dos.writeUTF("server : invalid ID, message not sent");}
                
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		try
		{
			// closing resources
			this.dis.close();
			this.dos.close();
            this.s.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
