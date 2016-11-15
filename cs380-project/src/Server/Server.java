/*
 * Server class will act as the receiver for the project.
 */
package server;

import FTP.Message;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Alexx
 */
public class Server 
{        
    private Socket sock;
    private ServerSocket server;
    private Scanner sc = new Scanner(System.in);
    Message message;
    /*public static void main(String[] args)
    {
        Server srv = new Server();
        srv.start(8888);
        srv.authenticate();
        srv.menu();
    }*/
        
    // Start the server and search for a client
    public void start(int port) 
    {
        try {
            server = new ServerSocket(8888);
            System.out.println("Waiting for client...");
            sock = server.accept();
            System.out.println("Connected to client.");
            message = new Message(sock);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Start menu for choosing to download a file or quit the program
    public void menu()
    {             
        System.out.println("\n1. Download file");
        System.out.println("2. Exit");                      
        int choice = sc.nextInt();
        sc.nextLine();
        
        switch(choice)
        {
            case 1:
                readFile();
                break;
                
            case 2:
                exit();
                break;
                
            default:
                System.out.println("Invalid option. Try again.");
                break;
        }
        menu();
    }
    
    // Stored  username is returned;
    public String getUser()
    {
        String user = "test";
        return user;
    }
    
    // Stored Salted/Hashed password is returned
    public String getPass()
    {
        String pass = "380";
        return pass;
    }
    
    // Authenticate login information
    public void authenticate()
    {
        boolean login = false;
        while(!login){
            message.sendMessage("Username: ");
            boolean hasMsg = message.hasMessage();
            while(!hasMsg){ hasMsg = message.hasMessage(); }
            String username = message.readMessage();
            System.out.println(username + " is trying to login");
            message.sendMessage("Password: ");
            hasMsg = message.hasMessage();
            while(!hasMsg){ hasMsg = message.hasMessage(); }
            String password = message.readMessage();
            System.out.println("With the password " + password);

            if (username.equals(getUser()) && password.equals(getPass()))
            {
                login = true;
            }else{
                message.sendMessage("Invalid login, please try again...");
                System.out.println();               
            }
        }
        message.sendMessage("Logged in successfully!");
        System.out.println("Client loggged in.");        
    }
    
    // Enter name and directory for the file to save
    public File saveFile()
    {
        JFileChooser fc = new JFileChooser();
        try {
            if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) 
                throw new FileNotFoundException();

            File file = fc.getSelectedFile();
            return file;

        } catch (FileNotFoundException e) {
            System.out.println("Invalid file.");
        }
        return null;
    }
    
    // Read a file from the client and save it at the given file path
    public void readFile()
    {
        try { 
            DataInputStream in = new DataInputStream(sock.getInputStream());         
            byte[] buffer = new byte[1024];            
            int bytesRead;
            
            File file = saveFile();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));        
            int fileSize = 0;            
            String hasBytes = "";
            boolean lastChunk = false;
            
            while(lastChunk == false)
            {
                boolean hasMsg = message.hasMessage();
                while(!hasMsg){ hasMsg = message.hasMessage(); }
                hasBytes = message.readMessage();                         
                if (hasBytes.equals("sending") || hasBytes.equals("last")) 
                {
                    if (hasBytes.equals("last"))
                        lastChunk = true;
                    
                    // tell client the server is ready to receive bytes                 
                    isReady();
                    
                    // wait for client to send bytes
                    Thread.sleep(10);
                    
                    // read bytes sent by client          
                    bytesRead = in.read(buffer);
                  
                    //*******************INSERT DECODING***********************
                    
                    out.write(buffer, 0, bytesRead);
                    
                    //notify client bytes have been received
                    hasReceived();                 
                    
                    // total bytes received
                    fileSize += bytesRead;                    
                    System.out.println("---------> " + fileSize + " bytes received");                    
                    
                    // reset buffer
                    buffer = new byte[1024];
                }
            }                        
            System.out.println("\nDownloaded file of size " + fileSize + " bytes");
            out.flush();     
            out.close();
            out = null;
        } catch (NullPointerException ex){
            menu();
        }           
          catch (IOException | InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Print and send message to client that server is ready to receive bytes
    public void isReady() {
        String hasBytes = "\nready";
        message.sendMessage(hasBytes);
        System.out.println(hasBytes);
    }
    
    // Print and send message to client that server has received bytes
    public void hasReceived() {
        String hasBytes = "received";
        System.out.println(hasBytes);
        message.sendMessage("received");             
    }
    
    // Exit the program
    public void exit()
    {
        try {
            System.out.println("\nExiting...");
            System.out.println("Bye bye.");
            sock.close();
            server.close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
}
