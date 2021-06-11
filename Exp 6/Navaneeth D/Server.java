// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{
      
    // counter for clients
    static int i = 1;

	public static void main(String[] args) throws IOException
	{
		// server is listening on port 5056
		ServerSocket ss = new ServerSocket(5056);
		
		// running infinite loop for getting
		// client request
		while (true)
		{
			Socket s = null;
			
			try
			{
				// socket object to receive incoming client requests
				s = ss.accept();
				
				System.out.println("A new client is connected : " + s);
				
				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				
				System.out.println("Assigning new thread for this client");
                System.out.println();

				// create a new thread object
				Thread t = new ClientHandler(s,"client_" + i, dis, dos);

				// Invoking the start() method
				t.start();

                i+=1;
				
			}
			catch (Exception e){
				s.close();
				e.printStackTrace();
			}
		}
	}
}

// ClientHandler class
class ClientHandler extends Thread
{
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
    final String name;
	boolean isloggedin;

	// Constructor
	public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos)
	{
		this.s = s;
		this.dis = dis;
		this.dos = dos;
        this.name  = name;
		this.isloggedin=false;
	}

	@Override
	public void run()
	{
		String received;
		String toreturn;
		
		while (true)
		{
			try {

			
				received = dis.readUTF();
                System.out.println("");
                System.out.print("From Client: ");
                System.out.print(received);
                System.out.println("");
				
				if(received.equalsIgnoreCase("Exit"))
				{
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}
			
                if(received.contains("auth#")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String auth = st.nextToken();
                    String id = st.nextToken();
                    if(id.equals(this.name)){
                        this.isloggedin=true;
                        dos.writeUTF("From Server: You have been successfully logged in as "+this.name+". Feel free to fetch files");
					}else{
                        dos.writeUTF("From Server: Credentials Wrong. Please try again");
                    }
                }else if(received.equals("log me in")){
					try{
						// provide the user with Auth ID
						dos.writeUTF("From Server: Your Auth id is "+this.name+". Please log in by sending auth#<id> to server");
					}catch (IOException e) {	
						e.printStackTrace();
					}
				}else if (received.contains("GET") && this.isloggedin){
                    // sendfile
                    StringTokenizer st = new StringTokenizer(received, " ");
                    String get = st.nextToken();
                    String filename = st.nextToken();
                    File directory = new File("./Source/");
                    MyFilenameFilter filter = new MyFilenameFilter(filename);
                    String[] flist = directory.list(filter);
                    if (flist.length == 0) {
                        dos.writeUTF("File not found");
                    }else{
                        dos.writeUTF(filename+ " File found");
						int len = this.name.length();
                        dos.writeUTF(this.name.charAt(len -1) + " ");
                        File myFile = new File("./Source/"+filename);
                        byte[] mybytearray = new byte[(int) myFile.length()];
                        
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        bis.read(mybytearray, 0, mybytearray.length);
                        OutputStream os = this.s.getOutputStream();
            
                        os.write(mybytearray, 0, mybytearray.length);
                        
                        os.flush();
                    }
                    
                    
                }else{
                    dos.writeUTF("Invalid input or please login and try again.");
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

class MyFilenameFilter implements FilenameFilter {
    
    String initials;
    
    // constructor to initialize object
    public MyFilenameFilter(String initials)
    {
        this.initials = initials;
    }
    
    // overriding the accept method of FilenameFilter
    // interface
    public boolean accept(File dir, String name)
    {
        return name.startsWith(initials);
    }
}