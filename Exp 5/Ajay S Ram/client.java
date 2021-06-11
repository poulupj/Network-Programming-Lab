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

            if(sendMessage.equals("send"))
            {
              Console console = System.console();      
              String recepient = console.readLine(" Recepient : ");
              String subject = console.readLine(" Subject : ");
              String message = console.readLine(" Message : ");

              sendMessage = "send`"+loggedUser+"`"+recepient+"`"+subject+'`'+message;

            }
            if(sendMessage.equals("help"))
            {
              System.out.println("\n\thelp\t: Get help commands\n\tsend\t: Send new mail\n\tview\t: View inbox\n\tread\t: Read a specific mail\n\texit\t: Exit the program\n");
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

            if(sendMessage.equals("view"))
            {
              sendMessage = "view:"+loggedUser;
              
            }
            if(sendMessage.equals("read"))
            {
              Console console = System.console();
              String mailNo = console.readLine("Enter the mail index : ");
              sendMessage = "read:"+loggedUser+":"+mailNo;
              
            }

            // Send mesage
            pwrite.println(sendMessage);        
            // Flush buffer
            pwrite.flush();                     
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
                  // 02-05-2021T16:38:0058_bob@test.com+hiINBOX
                  String[] mailsList =  receiveMessage.split(";");
                  for(int i = 0 ;i < mailsList.length;i++)
                  {
                    String dateOfMail = mailsList[i].split("T")[0];
                    String mailSender = mailsList[i].split("_")[1].split("\\+")[0];
                    String mailSubject = mailsList[i].split("_")[1].split("\\+")[1].replace("SENT","\t\t[UNREAD]");
                    System.out.println("\n-----------------------------------------------------------------------------------------\n\n["+i+"] ("+dateOfMail+")   { "+mailSender+" : "+mailSubject+"  }");
                  }
                  System.out.println("\n-----------------------------------------------------------------------------------------\n");
                }

                else
                {
                 
                 if(receiveMessage.contains("READ") || receiveMessage.contains("SENT"))
                 {
                  
                  String from = receiveMessage.split("`")[0];
                  String mailSub = receiveMessage.split("`")[1];
                  String mailContent = receiveMessage.split("`")[2];
                  String mailStatus = receiveMessage.split("`")[2].replace("SENT","\t\t[UNREAD]");
                  System.out.println("\n#########################################################################\n\nFROM : "+from+"\n\nSubject : "+mailSub+"\n\nMessage : \n\n\t"+mailContent+"\n\n#########################################################################\n\n"); 

                 }
                 else
                 {
                   System.out.println(receiveMessage);
                 }

                  if(receiveMessage.equals("exit"))
                  {
                    System.out.println("\nServer disconnected");
                    System.exit(0);
                  }
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
