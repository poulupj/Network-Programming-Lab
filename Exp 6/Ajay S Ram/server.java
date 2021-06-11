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
					
					if(line.contains("login"))
					{
						op = "login";
					}
					if(line.contains("ls"))
					{
						op = "ls";
					}
					if(line.contains("get"))
					{
						op = "get";
					}
					if(line.contains("put"))
					{
						op = "put";
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

							case "ls":
                                String filesList = "";
                                try{

                                File directoryPath = new File(System.getProperty("user.dir")+"/public");
                                String contents[] = directoryPath.list();
                                
                                for(int i=0; i<contents.length; i++) 
                                {
                                    filesList+=contents[i]+";";
                                }

                                filesList = filesList.substring(0, filesList.length() - 1);
                                }
                                catch(java.lang.StringIndexOutOfBoundsException e)
                                {
                                    filesList="Empty directory";
                                }
                                out.println(filesList);
                                out.flush();
                                break;

                            case "get":
                                String[] filename = line.split(" ");
								String sendThistoClient = "";
								try{

                                FileInputStream fileStream = new FileInputStream(System.getProperty("user.dir")+"/public/"+filename[1]);
								byte[] data = fileStream.readAllBytes();
								String encodedData = Base64.getEncoder().encodeToString(data);
								fileStream.close();
								sendThistoClient = encodedData;
								
                				} catch (FileNotFoundException e) {
                				    sendThistoClient = "Error";
                				} catch (IOException e) {
                				    e.printStackTrace();
                				}
								
								out.println(sendThistoClient);
								out.flush();
								out.println("");
								out.flush();
                                break;

							case "put":
								String putFile = line.split(":")[1];
								String fileData = line.split(":")[2];

              					FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir")+"/public/"+putFile);
              					byte[] data = Base64.getDecoder().decode(fileData);
              					fileOutputStream.write(data);
              					fileOutputStream.close();
								out.println("");
								out.flush();
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
