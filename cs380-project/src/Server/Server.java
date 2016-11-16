/*
 * Server class will act as the receiver for the project.
 */
package server;

import FTP.Message;
import Server.UserAuthentication;
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
        try {
            System.out.println("\nWaiting to receive file...");
            String uploading = "";
            while (!uploading.equals("uploading")) 
            {
                Thread.sleep(100); // 100ms            
                uploading = message.readMessage();
            }
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Enter file name and directory");
        readFile();
        menu();
    }
       
    // Authenticate login information
    public void authenticate()
    {
        UserAuthentication ua = new UserAuthentication();
        //boolean clientExit = false;
        boolean loggedOn = false;
        while(!loggedOn){
            boolean clientRdy = false;
            while(!clientRdy){ clientRdy = message.hasMessage(); }    
            String cmsg = message.readMessage();     
            switch(cmsg){
                case "create":
                    createUser(ua);
                    break;
                case "login":
                    loggedOn = login(ua);
                    break;
                case "exit":
                    //TODO: Client should force server to exit
                    exit();
                    break;
                default:
                    
            }
        }
    }
    
    public boolean login(UserAuthentication ua){
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
                boolean userLogin = ua.login(username, password);
                if (userLogin)
                {
                    login = true;
                }else{
                    message.sendMessage("Invalid login, please try again...");
                    System.out.println();               
                }
                /*if (username.equals(getUser()) && password.equals(getPass()))
                {
                    login = true;
                }else{
                    message.sendMessage("Invalid login, please try again...");
                    System.out.println();               
                }*/
            }
            message.sendMessage("Logged in successfully!");
            System.out.println("Client loggged in.");     
            return login;
    }
    
    public void createUser(UserAuthentication ua){
        message.sendMessage("c_username");
        boolean hasMsg = message.hasMessage();
        while(!hasMsg){ hasMsg = message.hasMessage(); }
        String username = message.readMessage();
        message.sendMessage("c_password");
        hasMsg = message.hasMessage();
        while(!hasMsg){ hasMsg = message.hasMessage(); }       
        String password = message.readMessage();
        boolean created = ua.createUser(username, password);
        if(created) message.sendMessage("Created user!");
        else message.sendMessage("Could not create user!");
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
            int attempts = 0;
            int fileSize = 0;            
            String sending = "";
            boolean lastChunk = false;
            boolean integrity = true;   // should be changed to false once decode code is inserted
            
            while(lastChunk == false && attempts < 4)
            {
                while (true)
                {                    
                    sending = message.readMessage();                         
                    if (sending.equals("sending") || sending.equals("last")) 
                    {
                        if (sending.equals("last"))
                            lastChunk = true;

                        // tell client the server is ready to receive chunk                 
                        isReady();
                        // wait for client to send chunk 
                        Thread.sleep(100); //100ms - might need to change this depending on encoding time
                        // read bytes sent by client          
                        bytesRead = in.read(buffer);

                        //*******************INSERT DECODING***********************
                        // change integrity to false if decoding doesn't match     
                        //notify client chunk have been received or failed to be received                            
                        if (integrity == false)
                        {                              
                            attempts++;
                            if (attempts < 4)
                            {
                                hasReceived(integrity, attempts);
                                System.out.println("making attempt# " + attempts 
                                        + " to redownload chunk.");  
                                continue;   // reset the loop
                            }
                            else 
                            {
                                hasReceived(integrity, attempts);
                                break;
                            }
                        }                        
                        hasReceived(integrity, attempts);                        
                        
                        // write chunk to file
                        out.write(buffer, 0, bytesRead);           
                        
                        // total chunk received
                        fileSize += bytesRead;                    
                        System.out.println("---------> " + fileSize + " bytes received");                    

                        // reset buffer
                        buffer = new byte[1024];
                        break;
                    }
                }
                if (attempts > 3)
                    break;
                else
                    attempts = 0;
            } 
            out.flush();     
            out.close();
            out = null;
            if (attempts > 3)
            {
                System.out.println("I've tried 3 times already to download this chunk."
                        + " There won't be a 4th. \nI quit.");
                exit();
            }
            else 
                System.out.println("\nDownloaded file of size " + fileSize + " bytes");
        } catch (NullPointerException ex){
            menu();
        }           
          catch (IOException | InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Print and send message to client that server is ready to receive bytes
    public void isReady()
    {        
        String ready = "\nready";
        message.sendMessage(ready);
        System.out.println(ready);
    }
    
    // Print and send message to client that server has received bytes
    public void hasReceived(boolean integrity, int attempts) 
    {
        String received = "";
        if (attempts > 3) 
        {             
            System.out.println("failed");
            message.sendMessage("quit"); 
        }
        else if (integrity == false)
        {            
            received = "failed";
            System.out.println(received);
            message.sendMessage("failed");  
        }
        else 
        {        
            received = "received";
            System.out.println(received);
            message.sendMessage("received");  
        }
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
