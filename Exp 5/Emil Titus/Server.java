import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

public class Server {
    public static void printName(){         // To print the program name in ASCII art form
        System.out.println("\n .d8888b.                                             \nd88P  Y88b                                            \nY88b.                                                 \n \"Y888b.    .d88b.  888d888 888  888  .d88b.  888d888 \n    \"Y88b. d8P  Y8b 888P\"   888  888 d8P  Y8b 888P\"   \n      \"888 88888888 888     Y88  88P 88888888 888     \nY88b  d88P Y8b.     888      Y8bd8P  Y8b.     888     \n \"Y8888P\"   \"Y8888  888       Y88P    \"Y8888  888\n");
    }
    public static ServerSocket serverSocket;
    public static String[] registeredUsers;
    public static HashMap<String, LinkedList<String>> mailQueue;
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {             
            // Check for command line arguments
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        registeredUsers = new String[]{"client1@example.com", "client2@example.com", "client3@example.com"};
        mailQueue = new HashMap<String, LinkedList<String>>();
        for (String user : registeredUsers) {
            mailQueue.put(user, new LinkedList<String>());
        }
        printName();
        System.out.println(mailQueue.keySet());

        try {
            
            serverSocket = new ServerSocket(port);         // Create a new server socket
            System.out.println("Created server socket successfully. SMTP server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();                // Listen for incoming connections
                Handler thread = new Handler(clientSocket, registeredUsers, mailQueue);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

class Handler extends Thread {
    Socket socket;
    boolean loggedIn;
    String emailID;
    String[] registeredUsers;
    HashMap<String, LinkedList<String>> mailQueue;
    PrintWriter socketOut;
    BufferedReader socketIn;

    public Handler(Socket socket, String[] registeredUsers, HashMap<String, LinkedList<String>> mailQueue){
        this.socket = socket;
        this.loggedIn = false;
        this.registeredUsers = registeredUsers;
        this.emailID = "anonymous";
        this.mailQueue = mailQueue;
    }
    
    public static String getTime(){         
        // To get the current system time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("( EEE, d MMM yyyy hh:mm aaa )");
        return sdf.format(cal.getTime());
    }

    String tryLogin(String email){
        for (String user : registeredUsers) {
            if (user.equals(email)){
                loggedIn = true;
                emailID = email;
                System.out.println("Client logged in as " + emailID);
                return "success";
            }
        }
        return "Invalid ID: " + email;
    }

    String sendMail(String email){
        if(!loggedIn){
            return "You are not logged in.";
        }
        for (String user : registeredUsers) {
            if (user.equals(email)){
                try {
                    socketOut.println("valid");
                    String time = getTime();
                    String subject = socketIn.readLine();
                    String message = socketIn.readLine();
                    String mail = "From: " + emailID +
                                "\nSubject: " + subject +
                                "\n" + time +
                                "\n" + message;
                    mailQueue.get(user).addLast(mail);
                    return "Email sent successfully.";            
                } catch (IOException e) {
                    System.out.println("IOException encountered while communicating with " + emailID);
                    System.exit(1);
                }
            }
        }
        return "Destination ID is not valid.";
    }

    String getMail(){
        if(!loggedIn){
            return "You are not logged in.";
        }
        if(mailQueue.get(emailID).isEmpty()){
            return "Your inbox is empty.";
        }
        socketOut.println("available");
        return mailQueue.get(emailID).removeFirst();
    }

    public void run(){
        try {
            socketOut = new PrintWriter(this.socket.getOutputStream(), true);                          // Socket output stream                
            socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));     // Socket input stream
            String[] clientMessage;

            System.out.println("Established connection with client @ " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());

            while (true){
                clientMessage = socketIn.readLine().split(" ");            // Read client requests from socket input stream
                switch (clientMessage[0]){
                    case "login":
                        if (clientMessage.length == 2){
                            socketOut.println(tryLogin(clientMessage[1]));
                        }
                        else {
                            socketOut.println("Invalid Command Format: " + String.join(" ", clientMessage));
                        }
                        break;
                    case "logout":
                        // Close all stream objects and sockets
                        socketOut.close();
                        socketIn.close();
                        socket.close();
                        throw new LoggedOut();
                    case "to":
                        if (clientMessage.length == 2){
                            socketOut.println(sendMail(clientMessage[1]));
                        }
                        else {
                            socketOut.println("Invalid Command Format: " + String.join(" ", clientMessage));
                        }
                        break;
                    case "receive":
                        socketOut.println(getMail());
                        break;
                    default:
                        socketOut.println("Invalid Command Format: " + String.join(" ", clientMessage));
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (LoggedOut e) {
            if (loggedIn){
                System.out.println("Client (" + emailID + ") logged out.");
            }
            else {
                System.out.println("Client @ " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " logged out.");
            }
        }
    }
}

class LoggedOut extends Exception {
    public LoggedOut(){
        super("User logged out.");
    }
}