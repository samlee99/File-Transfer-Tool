/*
 * Server class will act as the receiver for the project.
 */
package server;

import java.net.*;
import java.io.*;
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
    
    public static void main(String[] args)
    {
        Server srv = new Server();
        srv.start(8888);
        srv.authenticate();
        srv.menu();
    }
        
    // Start the server and search for a client
    public void start(int port) 
    {
        try {
            server = new ServerSocket(8888);
            System.out.println("Waiting for client...");
            sock = server.accept();
            System.out.println("Connected to client.");
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
            sendMessage("Username: ");
            String username = readMessage();
            
            sendMessage("Password");
            String password = readMessage();
            
            if (!username.equals(getUser()))
            {
                sendMessage("Invalid username");
                System.out.println();
                authenticate();
            }
            if (!password.equals(getPass()))
            {
                sendMessage("Invalid password");
                System.out.println();
                authenticate();
            }            
            sendMessage("Logged in successfully!");
            System.out.println("Client loggged in.");
    }
    
    // Send a string to the client 
    public void sendMessage(String msg)
    {
        PrintStream out;
        try {  
            out = new PrintStream(sock.getOutputStream());         
            out.println(msg); 
            out.flush();   
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Read a string sent by the client and print it to the console
    public String readMessage()
    {
        BufferedReader BR;
        try {
            BR = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String msg = BR.readLine();
            return msg;
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return null;
    }
    
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
            exit();
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
            
            while ( (bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);   
                break;
            }
                        
            System.out.println("File downloaded.");
            out.flush();     
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
