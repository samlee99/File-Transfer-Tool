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
import Base64.Base64;

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
	byte[] key;
	Base64 b64;

    // Start the server and search for a client
    public void start(int port) throws IOException
    {
		//Not sure where to put this....
		//Reads the key into a byte array
		FileInputStream fileInputStream = null;
		//Just change the directory to where the key is in
		File file = new File("key.txt");
		key = new byte[(int)file.length()];
		try{
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(key);
			fileInputStream.close();
		}catch(FileNotFoundException fileNotFoundException){
			fileNotFoundException.printStackTrace();
		}
		
        try {
            server = new ServerSocket(port);
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
            //boolean clientRdy = false;
            //while(!clientRdy){ clientRdy = message.hasMessage(); }    
            String cmsg = getMessage();
            switch(cmsg){
                case "create":
                    createUser(ua);
                    break;
                case "login":
                    loggedOn = login(ua);
                    if(!loggedOn) System.out.println("User failed to login");
                    break;
                case "exit":
                    //TODO: Client should force server to exit
                    exit();
                    break;
                default:                    
            }
        }
        if(loggedOn){
            message.sendMessage("Logged in successfully!");
            System.out.println("Client loggged in.");     
        }
    }
    
    public boolean login(UserAuthentication ua){
            boolean login = false;
           // while(!login){
                message.sendMessage("Username: ");
                //boolean hasMsg = message.hasMessage();
                ///while(!hasMsg){ hasMsg = message.hasMessage(); }
                String username = getMessage();
                System.out.println(username + " is trying to login");
                message.sendMessage("Password: ");
                //hasMsg = message.hasMessage();
                //while(!hasMsg){ hasMsg = message.hasMessage(); }
                String password = getMessage();
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
            //}
            return login;
    }
    
    public void createUser(UserAuthentication ua){
        //String username = message.readMessage();
        String username = createUsername(ua);
        message.sendMessage("Password");
        //boolean hasMsg = message.hasMessage();
        //while(!hasMsg){ hasMsg = message.hasMessage(); }       
        String password = getMessage();
        boolean created = ua.createUser(username, password);
        if(created) message.sendMessage("Created user!");
        else message.sendMessage("Could not create user!");
    }
     public String createUsername(UserAuthentication ua){
        String username = "";
        boolean goodname = false;
        while(!goodname){
          message.sendMessage("Username");
          //boolean hasMsg = message.hasMessage();
          //while(!hasMsg){ hasMsg = message.hasMessage(); }  
          username = getMessage();
          goodname = ua.usernameAvailable(username);
          if(goodname == true){
              message.sendMessage("Username is available!");
              break;
          }
          message.sendMessage("Username is not available!");
        }
        return username;
    }   
	
    // Get the filename of the file to be saved and return the file
    public File saveFile()
    {
        try {			
	    String filename = "";
            while (filename.equals("") || filename.equals("uploading")) 
            {
                Thread.sleep(100); // 100ms            
                //filename = message.readMessage();
                filename = getMessage();
            }
                filename = "downloads\\" + filename;
		File file = new File(filename);
                file.getParentFile().mkdir();
                return file;
            }
        catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
			boolean encode = false;
            
            while(lastChunk == false && attempts < 4)
            {
                while (true)
                {     
                    sending = getMessage();
                    //sending = message.readMessage();                         
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
						encode = Boolean.parseBoolean(getMessage());
						if(encode){
							String myString = in.readUTF();
							buffer = b64.decode(myString);
						}
						
						//Decode the chunk with the key
						xorCipher(buffer, key);
						
						//Decode the hash/checksum with the key
						//TODO: implement xorCipher(hash,key)
						
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

        //  Encodes/decodes the input with the key using XOR.
    public void xorCipher(byte[] input, byte[] key){
        for(int i = 0; i < input.length; i++){
            input[i] = (byte)(((int) input[i]) ^ ((int) key[i % key.length]));
        }
    }

    public String getMessage(){
        boolean hasMsg = message.hasMessage();
        while(!hasMsg){ hasMsg = message.hasMessage(); }       
        String msg = message.readMessage();
        if(msg.equals("disconnected")){
            exit();
        }
        return msg;
        //disconnected
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
           // Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
}
