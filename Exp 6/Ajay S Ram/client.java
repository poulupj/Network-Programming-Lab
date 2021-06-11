import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
public class client
{
  


  public static boolean isLoggedIn = false;
  public static String loggedUser = "";

  public static void main(String[] args) throws Exception
  {
      if (args.length != 2)
      {
          System.out.println("FORMAT : java client [SERVER IP][SERVER PORT]");
      }
      else
      {
      
        //System.out.println(BLACK);
        try
        {
          // Create socket
          Socket sock = new Socket(args[0],Integer.parseInt(args[1]));
          System.out.print("\n\n##################################################################\n##                                                              ##\n##                       AUTHENTICATE                           ##\n##                                                              ##\n##################################################################\n\n");
          // Input buffer
          BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
          
          // Output stream
          OutputStream ostream = sock.getOutputStream(); 
          
          // Write output
          PrintWriter pwrite = new PrintWriter(ostream, true);

          //Input stream
          InputStream istream = sock.getInputStream();
          
          // Buffer for incoming message
          BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
          String receiveMessage, sendMessage;   
          if(!isLoggedIn)
          {
            Console console = System.console();      
            String username = console.readLine("\tEmail : ");
            char[] pass = console.readPassword("\tPassword: ");
            String password = String.valueOf(pass);
      
            String sendMsg = "login:"+username+":"+password;
            pwrite.println(sendMsg);        
            pwrite.flush();

            if(receiveRead.readLine().contains("Success"))
            {
              isLoggedIn = true;
              loggedUser = username;

              
            }
          }

          System.out.println("\n\n");
          while(isLoggedIn)
          {
            System.out.print(">> ");
            // Mesaage to send
            sendMessage = keyRead.readLine();  

            
            if(sendMessage.equals("help"))
            {
              System.out.println("\n\thelp\t: Get help commands\n\tls\t: List files in directory\n\tget\t: Download file to current working directory\n\tput\t: Upload to remote server\n\texit\t: Exit the program\n");
            }


            if(sendMessage.equals("login"))
            {
              Console console = System.console();      
              String username = console.readLine("Email : ");
              char[] pass = console.readPassword("Password: ");
              String password = String.valueOf(pass);

              sendMessage = "login:"+username+":"+password;

              if(receiveRead.readLine().equals("Success"))
              {
                isLoggedIn = true;
                loggedUser = username;
                
              }

            }

            if(sendMessage.contains("put"))
            {
              String[] filename = sendMessage.split(" ");
              if(filename.length == 2)
              {

              try 
              {
                FileInputStream fileStream = new FileInputStream(System.getProperty("user.dir")+"/"+filename[1]);
								byte[] data = fileStream.readAllBytes();
								String encodedData = Base64.getEncoder().encodeToString(data);
								fileStream.close();
								sendMessage = "put:"+filename[1]+":"+encodedData;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
              }
              else
              {
                System.out.println("\nPlease specify a filename\n\n\tExample  : put filename\n");
                sendMessage = "";
              }
            }
         
            // Send mesage
            pwrite.println(sendMessage);        
            // Flush buffer
            pwrite.flush(); 

            if(sendMessage.contains("get"))
            {   
              FileOutputStream fileOutputStream = new FileOutputStream(sendMessage.split(" ")[1]);
              byte[] data = Base64.getDecoder().decode(receiveRead.readLine());
              fileOutputStream.write(data);
              fileOutputStream.close();
            }

            if(sendMessage.contains("Error"))
            {
              System.out.println("File not found");
            }

            if(sendMessage.equals("exit"))
            {
              System.out.println("\nClient exit");
              System.exit(0);
            }
            
             
             

          


            // Incoming message
            
            if((receiveMessage = receiveRead.readLine()) != null) 
            {
                if(receiveMessage.contains(";"))
                {
                    System.out.println("\n");
                    String[] filesInDir = receiveMessage.split(";");
                    for(int i = filesInDir.length - 1; i >= 0;i--)
                    {
                        System.out.print(filesInDir[i]+"\t");
                    } 
                    System.out.println("\n");
                }

                if(receiveMessage.equals("exit"))
                {
                  System.out.println("\nServer disconnected");
                  System.exit(0);
                }
            } 
            else{

              
              System.out.println("\nServer has closed connection, please try reconnecting again\n");
              System.exit(0);
            }
                    
          }   
        }
        catch(java.net.ConnectException e)
        {
          System.out.println("Are your sure that server [" + args[0] +"] is listening on port [" + args[1] + "] ? \nPlease try again\n");
        }
        catch(java.net.SocketException e)
        {
          System.out.println("Unfortunately the connection was reset.Please try again\n");
        }            
        catch(Exception e)
        {
         System.out.println(e.getMessage());
        }     
    }       
  }             
}                        
