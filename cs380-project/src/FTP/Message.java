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
    //private Queue<String> msgQueue;
    private Socket sock;
    private BufferedReader bf;
    private PrintStream ps;
    /*public Message(Socket sock){
        this.sock = sock;
    }*/
   
    public Message(Socket sock){
        try {
            this.sock = sock;
            bf = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            ps = new PrintStream(sock.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        // Send a string to the client 
    public void sendMessage(String msg)
    {
        ps.println(msg); 
        ps.flush();   
    }
    
    // Read a string sent by the client and print it to the console
    public String readMessage()
    {
        String msg = null;
        try {
            msg = bf.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
        
    }
    
    public boolean hasMessage(){
        try {       
            boolean hasMsg = false;
            //System.out.println(BR.ready());
            bf.mark(2);
            int ch = bf.read();
            if(ch != -1) hasMsg = true;
            bf.reset();
            if(hasMsg) return true;
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
