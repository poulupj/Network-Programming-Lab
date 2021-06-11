import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;  


// Server class
class server
{ 


    public static ArrayList<String> loggedInUsers = new ArrayList<String>();
	public static Hashtable<String,String> creds = new Hashtable<String,String>();


	public static void main(String[] args)
	{
        
		// ServerSocket server = null;
		if(args.length !=1)
		{
			System.out.println("FORMAT : java server [VALID PORT]");
		}
		else
	{
		try {
			
			int serverPort = Integer.parseInt(args[0]);
			// server is listening on port 1234
			ServerSocket server = new ServerSocket(serverPort);
			System.out.println("Server socket created\n"+"Server listening on port "+serverPort);
			server.setReuseAddress(true);
			
			File myObj = new File("creds.txt");
        	Scanner myReader = new Scanner(myObj);

        while (myReader.hasNextLine()) 
        {
        	String data = myReader.nextLine();
        	String user = data.split(":")[0];
        	String pass = data.split(":")[1];
        	creds.put(user,pass);
			File theDir = new File(System.getProperty("user.dir")+"/"+user);
			if (!theDir.exists())
			{
    			theDir.mkdirs();
			}

			
        }
        myReader.close();
			// running infinite loop for getting
			// client request
			while (true) {

				// socket object to receive incoming client
				// requests
				Socket client = server.accept();

				// Displaying that new client is connected
				// to server

				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SS");  
    			Date date = new Date();  

                String newClient = client.getInetAddress().getHostAddress();
				System.out.println("["+formatter.format(date)+"]\tNew IP connected : "+newClient);

                 

				// create a new thread object
				ClientHandler clientSock = new ClientHandler(client);

				// This thread will handle the client
				// separately
				new Thread(clientSock).start();
                
			}
		}
			catch(BindException e)
        	{
          		System.out.println("Please try another PORT");
        	}
        	catch(java.lang.NullPointerException e)
        	{
          		System.out.println("Please restart server to regain connection\n");
        	}
        	catch(IOException e)
        	{
          		System.out.println(e.getMessage()+"\n");
        	}
	}
	}
	

	// ClientHandler class
	private static class ClientHandler implements Runnable 
	{
		private final Socket clientSocket;

        public static volatile boolean dead = false;
		// Constructor
		public ClientHandler(Socket socket)
		{
			this.clientSocket = socket;
		}

		        
		public void run()
		{
			PrintWriter out = null;
			BufferedReader in = null;
			try 
			{
					
				// get the outputstream of client
				out = new PrintWriter(clientSocket.getOutputStream(), true);

				// get the inputstream of client
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                          
				String line;
				while ((line = in.readLine()) != null ) 
                {
                    
					String clientIP = clientSocket.getRemoteSocketAddress().toString().split("/")[1].split(":")[0]; 
					
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SS");  
    				Date date2 = new Date();  
                    
					String op = "";
					if(line.contains("send"))
					{
						op = "send";
					}
					if(line.contains("login"))
					{
						op = "login";
					}
					if(line.contains("view"))
					{
						op = "view";
					}
					if(line.contains("read"))
					{
						op = "read";
					}

						switch(op)
                    	{
                        	
							case "login":
								String sendMsg = "";
								String loginUser = line.split(":")[1];
								String loginPass = line.split(":")[2];
									int counter = 0;
								if(creds.containsKey(loginUser) && loginPass.equals(creds.get(loginUser)))
								{
									loggedInUsers.add(loginUser);

									sendMsg = "Success";
									Date dateNow = new Date();
									System.out.println("["+formatter.format(dateNow)+"]\t[ LOGIN ] : "+loginUser+"\n");
								}
								
								out.println(sendMsg);
								out.flush();
								break;

							case "send":
								// System.out.println(line);
								String sender = line.split("`")[1];
								String receiver = line.split("`")[2];
								String subject = line.split("`")[3];
								String message = line.split("`")[4];

								Date date3 = new Date();

								System.out.println("["+formatter.format(date3)+"]\t[ NEW MAIL ] : "+sender);

								SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ssss");
								Date date = new Date();

							
								File newMailObject = new File(System.getProperty("user.dir")+"/"+receiver+"/"+formatter2.format(date)+"_"+sender);
								FileWriter mailDateWriter = new FileWriter(newMailObject);
								mailDateWriter.write(sender+"`"+subject+"`"+message+"`SENT");
								mailDateWriter.close();
								out.println("Message sent !");
								out.flush();
								break;
							
							case "view":
								String viewer = line.split(":")[1];

								File directory = new File(System.getProperty("user.dir")+"/"+viewer);
								String[] mails = directory.list();

								String allMails = "";

								for(int i = 0 ; i < mails.length ; i++)
								{

									File readObj = new File(System.getProperty("user.dir")+"/"+viewer+"/"+mails[i]);
      								Scanner myReader = new Scanner(readObj);
      								String mailContent = myReader.nextLine();
									String sub = mailContent.split("`")[1];
									allMails+=mails[i]+"+"+sub+mailContent.split("`")[3]+";";
      								myReader.close();
								}

								Date date4 = new Date();
								
								System.out.println("["+formatter.format(date4)+"]\t[ VIEW MAIL ] : "+viewer);

								out.println(allMails);
								out.flush();
								break;
							
							case "read":
								System.out.println(line.split(":")[2]);
								Integer mailIndex = Integer.parseInt(line.split(":")[2]);
								String viewer2 = line.split(":")[1];
								File directory2 = new File(System.getProperty("user.dir")+"/"+viewer2);
								String[] mails2 = directory2.list();
								File readObj2 = new File(System.getProperty("user.dir")+"/"+viewer2+"/"+mails2[(mailIndex)]);
      							Scanner myReader2 = new Scanner(readObj2);
      							String mailContent2 = myReader2.nextLine();
            					String newContent = mailContent2.replace("SENT","`READ");
								FileWriter mailDateWriter2 = new FileWriter(readObj2);
								mailDateWriter2.write(newContent);
								mailDateWriter2.close();
								out.println(mailContent2);
								out.flush();

								Date date5 = new Date();
								
								System.out.println("["+formatter.format(date5)+"]\t[ READ MAIL ] : "+viewer2);

								break;
							
							default :
								out.println("");
								break;
                		}

				}
			}
			 
			catch(IOException e)
			{
				
			} 
			
		}
	}
}
