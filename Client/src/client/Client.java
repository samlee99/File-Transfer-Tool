/*
 * Client class will act as the sender for the project.
 */
package client;

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
    
    public static void main(String[] args)
    {
        Client cln = new Client();
        cln.start("localhost", 8888);
        cln.login();
        cln.menu();
    }   
    
    // Connect the client to the server
    public void start(String host, int port) 
    {
        try {          
            sock = new Socket(host, port);
            System.out.println("Connected to server.");
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public void login()
    {        
        System.out.println(readMessage());
        String username = sc.nextLine();
        sendMessage(username);
        
        System.out.println(readMessage());
        String password = sc.nextLine();
        sendMessage(password);
        
        String msg = readMessage();
        if (!msg.equals("Logged in successfully!")) 
        {
            System.out.println("Log in failed.");
            login();
        }
        else
            System.out.println(msg);
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
    
    // Send a string to the server 
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
            
            while( (bytesRead = in.read(buffer)) > 0)
                out.write(buffer, 0, bytesRead);
            
            out.flush();            
            System.out.println("File uploaded.");
            
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