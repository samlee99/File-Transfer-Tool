/*
 * Client class will act as the sender for the project.
 */
package client;

import FTP.Message;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author Alexx
 */
public class Client 
{
    private Socket sock;
    private Scanner sc = new Scanner(System.in);
    Message message;
    /*public static void main(String[] args)
    {
        Client cln = new Client();
        cln.start("localhost", 8888);
        cln.login();
        cln.menu();
    }   */
    
    // Connect the client to the server
    public boolean start(String host, int port) 
    {
        boolean started = true;
        try {          
            sock = new Socket(host, port);
            System.out.println("Connected to server.");
            
        } catch (IOException ex) {
            started = false;
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Open up the message queue
        if(started)
            message = new Message(sock);
        return started;
    }
            
    // Log client into server
    public void loginMenu(Console console)
    {        
        System.out.println("1) Create new user");
        System.err.println("2) Login");
        System.out.println("3) Exit");
        boolean loggedOn = false;
        int choice = sc.nextInt();
        sc.nextLine();
        while(choice != 3 && loggedOn == false){
            switch(choice){
                case 1:
                    message.sendMessage("create");
                    createUser(console);
                    break;
                case 2:
                    message.sendMessage("login");
                    loggedOn = login(console);
                    break;
                case 3:
                    message.sendMessage("exit");
                    exit();
                default:
            }      
            if(loggedOn) break;
            System.out.println("1) Create new user");
            System.err.println("2) Login");
            System.out.println("3) Exit");
            choice = sc.nextInt();
            sc.nextLine();
        }
    }
    public void createUser(Console console){
        boolean hasMsg = message.hasMessage();
        while(!hasMsg){ hasMsg = message.hasMessage(); }
        System.out.println(message.readMessage()); 
        String username = sc.nextLine();
        message.sendMessage(username);     
        hasMsg = message.hasMessage();
        while(!hasMsg){ hasMsg = message.hasMessage(); }
        System.out.println(message.readMessage());
        char[] hiddenPassword = console.readPassword();
        String password = new String(hiddenPassword);
        message.sendMessage(password);    
        while(!hasMsg){ hasMsg = message.hasMessage(); }
        String msg = message.readMessage();
        if(msg.equals("Created user!")){
            System.out.println(msg);
        }else System.out.println(msg);
    }
    public boolean login(Console console){
            boolean login = false;
            while(!login){
                boolean hasMsg = message.hasMessage();
                while(!hasMsg){ hasMsg = message.hasMessage(); }
                System.out.println(message.readMessage());
                String username = sc.nextLine();
                message.sendMessage(username);

                hasMsg = message.hasMessage();
                while(!hasMsg){ hasMsg = message.hasMessage(); }
                System.out.println(message.readMessage());
                char[] hiddenPassword = console.readPassword();
                String password = new String(hiddenPassword);
                message.sendMessage(password);
                
                hasMsg = message.hasMessage();
                while(!hasMsg){ hasMsg = message.hasMessage(); }
                String msg = message.readMessage();
                if (!msg.equals("Logged in successfully!")) 
                {
                    System.out.println("Log in failed.");
                    //TODO: Change this to a loop instead of recurrsion 
                }
                else{
                    System.out.println(msg);   
                    login = true;
                    return login;
                }
            }
            return false;
    }
    // Start menu for choosing to upload a file or quit the program
    public void menu()
    {
        System.out.println("\n1. Upload file");
        System.out.println("2. Exit");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) 
        {
            case 1:                             
                upload();
                menu();
                break;
            case 2:
                exit();
            default:
                System.out.println("Invalid choice.");
                menu();
        }
       
    }
        
    public void upload()
    {
        JFileChooser fc = new JFileChooser();
        try {
            if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) 
                throw new FileNotFoundException();
            
            File file = fc.getSelectedFile();
            sendFile(file);
            
        } catch (FileNotFoundException e) {
            System.out.println("Invalid file.");
        }
    }
    
    // Send the file at the given directory
    public void sendFile(File file)
    {
        try {             
            byte[] buffer = new byte[1024];
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            int bytesRead;
            int bytesSent = 0;
            long fileSize = file.length();            
            String hasBytes = "received";
            System.out.println("Uploading file of size: " + fileSize + " bytes...\n");
            
            // Read up to 1024 bytes (1kb)
            while ((bytesRead = in.read(buffer)) > 0) 
            {    
                if (hasBytes.equals("received")) 
                {
                    // notify the server of the bytes are being sent
                    if (fileSize <= 1024)
                        hasBytes = "last";
                    else                        
                        hasBytes = "sending";
                    message.sendMessage(hasBytes);

                    // wait until server is ready for bytes to be sent
                    hasBytes = isReady(hasBytes);
                    
                    //*******************INSERT ENCODING***********************
                    
                    // send the bytes 
                    out.write(buffer, 0, bytesRead);

                    // receive notification from server that bytes have been read
                    hasBytes = hasReceived(hasBytes);

                    // total bytes sent
                    fileSize -= bytesRead;
                    bytesSent += bytesRead;
                    System.out.println("---------> " + bytesSent + " bytes sent\n");

                    //garbage collector should clean out the old buffer I think...
                    //Clean out the buffer and start fresh
                    buffer = new byte[1024];
                } 
            }
            out.flush();
            in.close();
            in = null;
            System.out.println("File uploaded!");
        } catch (NullPointerException ex){
            menu();
        }           
          catch (IOException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Print and get answer from server if they are ready to receeive bytes    
    public String isReady(String hasBytes) throws InterruptedException
    {        
        System.out.println("Waiting on server...");
        while (!hasBytes.equals("ready"))
        {
            Thread.sleep(10);
            boolean hasMsg = message.hasMessage();
            while(!hasMsg){ hasMsg = message.hasMessage(); }
            hasBytes = message.readMessage();
        }
        return hasBytes;
    }
    
    
    // Print and get answer from server if they received bytes    
    public String hasReceived(String hasBytes) throws InterruptedException
    {
        System.out.println("received?");  
        while (!hasBytes.equals("received"))
        {   
            Thread.sleep(10); // 1s sleep 
            boolean hasMsg = message.hasMessage();
            while(!hasMsg){ hasMsg = message.hasMessage(); }
            hasBytes = message.readMessage();
        }
        return hasBytes;
    }
        
    // Exit the program
    public void exit()
    {
        try {
            System.out.println("\nExiting...");
            System.out.println("Bye bye.");
            sock.close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}