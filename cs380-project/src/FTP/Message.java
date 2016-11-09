/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

/**
 *
 * @author andre
 */
public class Message {
    private Queue<String> msgQueue;
    private Socket sock;
    
    public Message(Socket sock){
        this.sock = sock;
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
}
