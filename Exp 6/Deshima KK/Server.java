// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

import java.net.*;

// Server class
public class Server
{
	
	
	// static Vector<ClientHandler> ar = new Vector<>();
	public static void main(String[] args) throws IOException
	{
		
		// server is listening on port 5056
		ServerSocket ss = new ServerSocket(5056);
		List<String> list=new ArrayList<String>();
		// running infinite loop for getting
		// client request
        int i=0;
		while (true)
		{
			Socket s = null;
			
			try
			{
				// socket object to receive incoming client requests
				s = ss.accept();
				i=i+1;
				String name="client"+i;

				System.out.println("Client "+i+" is connected : " + s);
				
				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				OutputStream os = s.getOutputStream();
				System.out.println("Assigning new thread for this client");

				// create a new thread object
				Thread t = new ClientHandler(s, dis, dos,i,os);

				// Invoking the start() method
				while(true){
					String auth=dis.readUTF();
					if(auth.equals(name)){
						System.out.println(name+" validated");
						dos.writeUTF("valid");
						break;
					}
					dos.writeUTF("invalid");
				}
				t.start();
				
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
	int i;
	final OutputStream os;
	private static List<String> files;
	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos,int i,OutputStream os)
	{
		this.s = s;
		this.dis = dis;
		this.dos = dos;
        this.i=i;
		this.os=os;
	}

	@Override
	public void run()
	{

		List<String> files=new ArrayList<String>();
		files.add("aa.txt");
		files.add("a.png");
		
		String fileName;
        Scanner sc = new Scanner(System.in);
		String toreturn;
		while (true)
		{
			try {

				
				
				// receive the answer from client
				fileName = dis.readUTF();
				
				if(fileName.equals("Exit"))
				{
					System.out.println("Client "+i+" "+ this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}
				
				
				
				System.out.println("Client "+i+" requests: "+fileName);

				// file search
				if(files.contains(fileName))
				{	
					
					dos.writeUTF("found");
					dos.writeUTF("process_id : "+i+", file_name : "+fileName);
					File myFile = new File("./Server/"+fileName);
					byte[] mybytearray = new byte[(int) myFile.length()];
					
					FileInputStream fis = new FileInputStream(myFile);
					BufferedInputStream bis = new BufferedInputStream(fis);
					bis.read(mybytearray, 0, mybytearray.length);
					
					// OutputStream os = sock.getOutputStream();
					
					os.write(mybytearray, 0, mybytearray.length);
					
					
					// toreturn=sc.nextLine();
					// dos.writeUTF("found");
					
					// dos.flush();
					os.flush();
					System.out.println("Process "+i+" completed. File tranfered succcessfully.");
					}
					else{
						dos.writeUTF("not found");
						System.out.println("The file is not found. Process "+i+" not completed.");
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
            sc.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
