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
	// static Vector<ClientHandler> arId = new Vector<>();
	
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
			ClientHandler mtch = new ClientHandler(s,"client_" + i, dis, dos);

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
	private String nameId;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	// constructor
	public ClientHandler(Socket s, String nameId,
							DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.nameId = nameId;
		this.s = s;
		this.isloggedin=false;
	}

	@Override
	public void run() {

		String received;

        try{
            // provide the user with Auth ID
            dos.writeUTF("From Server: Your Auth id is "+this.nameId+". Please log in by sending auth#<id> to server");
        }catch (IOException e) {	
            e.printStackTrace();
        }
                
		while (true)
		{
			try
			{
                int search_flag = 0;
                // receive the string
				received = dis.readUTF();
				
				System.out.print("From Client: ");
				System.out.println(received);
				
				if(received.equals("logout")){
                    dos.writeUTF("logout");
					this.isloggedin=false;
					this.s.close();
					break;
				}else if(received.contains("auth#")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String auth = st.nextToken();
                    String id = st.nextToken();
                    if(id.equals(nameId)){
                        this.isloggedin=true;
                        dos.writeUTF("From Server: You have been successfully logged in as "+this.nameId+". Feel free to message other active Clients");
						dos.writeUTF("#readBufferedMessages#");
					}else{
                        dos.writeUTF("From Server: Credentials Wrong. Please try again");
                    }
                }else if(this.isloggedin && received.contains("#")){
                    // break the string into message and recipient part
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String MsgToSend = st.nextToken();
                    String recipient = st.nextToken();

                    // search for the recipient in the connected devices list.
                    // ar is the vector storing client of active users
                    for (ClientHandler mc : Server.ar)
                    {
                        // if the recipient is found, write on its
                        // output stream
                        if (mc.nameId.equals(recipient))
                        {
                            search_flag = 1;
                            if( mc.isloggedin==true){
                                mc.dos.writeUTF(this.nameId+" : "+MsgToSend);
                                break;
                            }else{
                                dos.writeUTF("Recipient not Logged in");
								mc.dos.writeUTF("#addToBuffer# "+this.nameId+" : "+MsgToSend);
                            }
                            
                        }
                    }
                    if(search_flag == 0){
                        dos.writeUTF("Recipient does not exist");
                    }
                }else{
                    dos.writeUTF("Wrong Input type or You are not logged in");
                }
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		try
		{
			// closing resources
			this.dis.close();
			this.dos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
