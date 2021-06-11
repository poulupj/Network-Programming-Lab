// Java implementation for a client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.*;

// Client class
public class Client
{
	public static void main(String[] args) throws IOException
	{
		int bytesRead;
    	int current = 0;
		try
		{
			Scanner scn = new Scanner(System.in);
			
			// getting localhost ip
			InetAddress ip = InetAddress.getByName("localhost");
	
			// establish the connection with server port 5056
			Socket s = new Socket(ip, 5056);
	
			// obtaining input and out streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String tosend="";
			String auth;
			String rep;
			while(true){
				System.out.println("Enter ID : ");
				auth=scn.nextLine();
				dos.writeUTF(auth);
				rep=dis.readUTF();
				if(rep.equals("valid")){
					System.out.println(auth+" Validated");
					break;
				}
				System.out.println("invalid id");
			}
			// the following loop performs the exchange of
			// information between client and client handler
			while (true)
			{
				System.out.print("Enter filename : ");
				tosend = scn.nextLine();
				dos.writeUTF(tosend);
				dos.flush();
				// If client sends exit,close this connection
				// and then break from the while loop
				InputStream in = s.getInputStream();
        
				// Writing the file to disk
				// Instantiating a new output stream object
				
				if(tosend.equals("Exit"))
				{
					System.out.println("Closing this connection : " + s);
					s.close();
					System.out.println("Connection closed");
					break;
				}
				String received = dis.readUTF();
				// System.out.println("From Server: "+received);
				if(received.equals("found"))
				{	
					received = dis.readUTF();
					System.out.println(received);
					OutputStream output = new FileOutputStream("./Client/"+tosend);
					byte[] buffer = new byte[1024];
					
					 while ((bytesRead = in.read(buffer)) > 0) {
                        output.write(buffer, 0, bytesRead);
                        if(bytesRead<986){
                            break;
                        }

                    }
					
					output.close();
					System.out.println("Transfer Complete");
					}else{
				System.out.println("The file is not found.");
			}
				
				// printing date or time as requested by client
				
			}
			
			// closing resources
			scn.close();
			dis.close();
			dos.close();
			// output.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
