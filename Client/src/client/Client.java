/*
 * Client class will act as the sender for the project.
 */
package client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexx
 */
public class Client 
{
    private Socket sock;
    
    public static void main(String[] args)
    {
        Client cln = new Client();
        cln.start("localhost", 8888);
        cln.sendMessage("Hello Server, tis I, Client!");
        cln.readMessage();
        //cln.sendFile("test.txt");
        cln.exit();
    }   
    
    // Connect the client to the server
    public void start(String host, int port) 
    {
        try {          
            sock = new Socket(host, port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Send a string to the server 
    public void sendMessage(String msg)
    {
        PrintStream out;
        try {
            out = new PrintStream(sock.getOutputStream());            
            out.println(msg);  
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
    
    // Read a string sent by the server and print it to the console.    
    // If the message was received successfully, notify the sender
    public void readMessage()
    {
        BufferedReader BR;
        try {
            BR = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String msg = BR.readLine();
            System.out.println(msg);
        
            if (msg != null) 
                sendMessage("Message received.");
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
          
    }
    
    // *************Incomplete*********************
    public void sendFile(String path)
    {
        try {                        
            byte[] file = new byte[1024];
            InputStream in = new FileInputStream(new File(path));
            OutputStream out = sock.getOutputStream();
                        
            
            System.out.println(in.read() + "v");
            int bytesRead;
            while( (bytesRead = in.read(file)) > 0)
                out.write(file, 0, bytesRead);
            //out.close();            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // *************Incomplete*********************
    public void readFile(String path)
    {
        try {
            InputStream in = sock.getInputStream();
            OutputStream out = new FileOutputStream(path);
            byte[] file = new byte[1024];
            int bytesRead;
            
            while( (bytesRead = in.read(file)) >= 0)
                out.write(file, 0, bytesRead);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Exit the program
    public void exit()
    {
        try {
            sock.close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}