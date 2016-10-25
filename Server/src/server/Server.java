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
    private ServerSocket server;
    private Socket sock;
    private PrintStream msgOut;
    PrintStream out;
    
    public static void main(String[] args)
    {
        Server srv = new Server();
        srv.start(8888);
        srv.readMessage();
        //srv.readFile("test.txt");
        srv.exit();
    }
    
    // Start the server and search for a client
    public void start(int port) 
    {
        try {
            server = new ServerSocket(8888);
            sock = server.accept();
            System.out.println("Connected to client.");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Send a string to the client 
    public void sendMessage(String msg)
    {
        PrintStream out;
        try {  
            out = new PrintStream(sock.getOutputStream());         
            out.println(msg); 
            out.close();            
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Read a string sent by the client and print it to the console.
    // If the message was received, notify the sender the message was received.
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
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
            System.out.println(in.read() + "d");
            while( (bytesRead = in.read(file)) > 0)
            {
                out.write(file, 0, bytesRead);
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Exit the program
    public void exit()
    {
        try {
            sock.close();
            server.close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
}
