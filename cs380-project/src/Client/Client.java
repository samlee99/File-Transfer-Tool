/*
 * Client class will act as the sender for the project.
 */
package client;

import FTP.Message;
import java.net.*;
import java.io.*;
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
            
    public void login()
    {        
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
            String password = sc.nextLine();
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
            }
        }
        /*System.out.println(message.readMessage(sock));
        String username = sc.nextLine();
        message.sendMessage(sock,username);
        
        System.out.println(message.readMessage(sock));
        String password = sc.nextLine();
        message.sendMessage(sock,password);
        
        String msg = message.readMessage(sock);
        if (!msg.equals("Logged in successfully!")) 
        {
            System.out.println("Log in failed.");
            //TODO: Change this to a loop instead of recurrsion 
            login();
        }
        else
            System.out.println(msg);*/
    }
    
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
    
/*    // Send a string to the server 
    public void sendMessage(String msg)
    {
        PrintStream out;
        try {
            out = new PrintStream(sock.getOutputStream());            
            out.println(msg);  
            out.flush();
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
    
    // Read a string sent by the server and print it to the console.    
    // Return the message that was received.
    public String readMessage()
    {
        BufferedReader BR;
        try {
            BR = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String msg = BR.readLine();
            return msg;
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return null;
    }*/
    
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
            exit();
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
            int filesize = 0;
                // Read up to 1024 bytes (1kb)
                while((bytesRead = in.read(buffer)) > 0){
                    out.write(buffer, 0, bytesRead);
                    filesize += bytesRead;
                    System.out.print(filesize + ",");
                    //garbage collector should clean out the old buffer I think...
                    //Clean out the buffer and start fresh
                    buffer = new byte[1024];
                }
            out.flush();
            in.close();
            in = null;
            System.out.println("Uploaded the file!");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
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