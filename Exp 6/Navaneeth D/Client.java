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

            int bytesRead;
            int current = 0;

			System.out.println("Type 'log me in' to get more details on authorization process. ");
			System.out.println();
			// the following loop performs the exchange of
			// information between client and client handler
			while (true)
			{
				System.out.println();
				System.out.print("To server: ");
				String tosend = scn.nextLine();
				dos.writeUTF(tosend);
				
				// If client sends exit,close this connection
				// and then break from the while loop
				if(tosend.equalsIgnoreCase("Exit"))
				{
					System.out.println("Closing this connection : " + s);
					s.close();
					System.out.println("Connection closed");
					break;
				}

				String received = dis.readUTF();
                if(received.equals("File not found")){
                    System.out.print("From server: ");
				    System.out.println(received);
                }else if(received.contains("File found")){
					StringTokenizer st = new StringTokenizer(received, " ");
                    String filename = st.nextToken();
                    System.out.print("From server: ");
                    System.out.println(received );
					String pid = dis.readUTF();
					System.out.println();
					System.out.println("File transfered");
					System.out.println("META INFO: ");
					System.out.println("The process was handled by thread with PID" + pid + " on the server side");

					int index = filename.lastIndexOf('.');
					if(index > 0) {
						String extension = filename.substring(index + 1);
						String mime = "unknown";
						if(extension.equals("txt")){
							mime = "text";
						}else if(extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")){
							mime = "image";
						}
						
						System.out.println("File extension: " + extension);
						System.out.println("MIME type: "+mime);
					}
					
                    // printing date or time as requested by client
                    InputStream in = s.getInputStream();

                    // Writing the file to disk
                    // Instantiating a new output stream object
                    OutputStream out = new FileOutputStream("./Target/"+filename);
                    byte[] buffer = new byte[1024];

                    while ((bytesRead = in.read(buffer)) > 0) {
                        out.write(buffer, 0, bytesRead);
                        if(bytesRead<986){
                            break;
                        }

                    }

                    // Closing the FileOutputStream handle
                    out.close();
					System.out.println("-----Transfer complete-----");
					System.out.println();
					System.out.println();
                }else {
					System.out.println(received);
				}
                
			}
			
			// closing resources
			scn.close();
			dis.close();
			dos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
