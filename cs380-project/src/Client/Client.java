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
import Base64.Base64;
import xorCipher.xorCipher;
import SHA1.SHA1;
/**
 *
 * @author Alexx
 */
public class Client 
{
    private Socket sock;
    private Scanner sc = new Scanner(System.in);
    Message message;
    Base64 b64;
    xorCipher xor;
	SHA1 sha1;
	byte[] key;
	
    // Connect the client to the server
    public boolean start(String host, int port) throws IOException
    {
        b64 = new Base64();
        xor = new xorCipher();
		
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
            if(loggedOn) menu();
            System.out.println("1) Create new user");
            System.err.println("2) Login");
            System.out.println("3) Exit");
            choice = sc.nextInt();
            sc.nextLine();
        }
    }
    public void createUser(Console console){
        createUsername();
        //boolean hasMsg = message.hasMessage();
        //while(!hasMsg){ hasMsg = message.hasMessage(); }
        System.out.println(getMessage());
        boolean goodpass = false;
        String password = "";
        while(!goodpass){
            char[] hiddenPassword = console.readPassword();
            password = new String(hiddenPassword);
            if(password.equals("")) System.out.println("Password cannot be empty!");
            else goodpass = true;
        }
        message.sendMessage(password);    
        //while(!hasMsg){ hasMsg = message.hasMessage(); }
        String msg = getMessage();
        if(msg.equals("Created user!")){
            System.out.println(msg);
        }else System.out.println(msg);
    }
    
    public void createUsername(){
        boolean goodname = false;
        while(!goodname){
            //boolean hasMsg = message.hasMessage();
            //while(!hasMsg){ hasMsg = message.hasMessage(); }
            System.out.println(getMessage()); 
            String username = sc.nextLine();    
            message.sendMessage(username);    
            //hasMsg = message.hasMessage();
            //while(!hasMsg){ hasMsg = message.hasMessage(); }
            String msg = getMessage();
            if(msg.equals("Username is available!")){
                System.out.println(msg);
                goodname = true;
            }else{
                System.out.println(msg);
            }
            
        }
    }
    
    public boolean login(Console console){
            boolean login = false;
            //while(!login){
                //boolean hasMsg = message.hasMessage();
                //while(!hasMsg){ hasMsg = message.hasMessage(); }
                System.out.println(getMessage());
                String username = sc.nextLine();
                message.sendMessage(username);

                //hasMsg = message.hasMessage();
                //while(!hasMsg){ hasMsg = message.hasMessage(); }
                System.out.println(getMessage());
                char[] hiddenPassword = console.readPassword();
                String password = new String(hiddenPassword);
                message.sendMessage(password);
                
                //hasMsg = message.hasMessage();
                //while(!hasMsg){ hasMsg = message.hasMessage(); }
                String msg = getMessage();
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
            //}
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
                int encode = -1;
                System.out.println("Would you like to encode? (0 for no, 1 for yes)");
                encode = sc.nextInt();
                sc.nextLine();
                boolean encoded;
                if(encode == 1) encoded = true;
                else encoded = false;
                upload(encoded);
                menu();
                break;
            case 2:
                exit();
            default:
                System.out.println("Invalid choice.");
                menu();
        }
       
    }
        
    public void upload(boolean encode)
    {
        JFileChooser fc = new JFileChooser();
        try {
            if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) 
                throw new FileNotFoundException();
            
            File file = fc.getSelectedFile();
			// Tell server a file is being uploaded
			message.sendMessage("uploading");
			
			String filename = file.getName();			
			System.out.println("Filename: " + filename);
			// Send the filename that is being uploaded
			message.sendMessage(file.getName());
            sendFile(file, encode);
            
        } catch (FileNotFoundException e) {
            System.out.println("Invalid file.");
        }
    }
    
    // Send the file at the given directory
    public void sendFile(File file, boolean encode)
    {
        try {             
            byte[] buffer = new byte[1024];
            BufferedInputStream in = new BufferedInputStream (new FileInputStream(file));
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            int bytesRead;
            int bytesSent = 0;
            int attempts = 0;
            long fileSize = file.length();            
            String received = "received";
                    
            System.out.println("Uploading file of size: " + fileSize + " bytes...\n");            
            // Read up to 1024 bytes (1kb)
            while ((bytesRead = in.read(buffer)) > 0 && !received.equals("quit")) 
            {    
                in.mark(1024);
                while (true)
                {
                    if (received.equals("received")) 
                    {
                        // notify the server of the bytes are being sent
                        if (fileSize <= 1024)
                            received = "last";
                        else 
                            received = "sending";
                        message.sendMessage(received);

                        // wait until server is ready for bytes to be sent
                        isReady();                    
                        

                        //*******************INSERT ENCODING***********************
						String checksum = sha1.encode(buffer);
						
						//need to send this checksum to client for verification
						byte[] byteChecksum = checksum.getBytes(); 
						
						//Encrypt the chunk by XORing with the key
						xor.xorCipher(buffer, key);
						//Encrypt the checksum by XORing with the key
						xor.xorCipher(byteChecksum, key);
						
						message.sendMessage(String.valueOf(encode));
                        if(encode){
							String stringB64Hash = b64.encode(buffer);
							out.writeUTF(stringB64Hash);
                        }
						
						
                        // send the chunk to the server
                        out.write(buffer, 0, bytesRead);    
						
						//send the hash to the server somehow
						

                        // receive notification from server that bytes have been read
                        received = hasReceived(received);
                        
                        if (received.equals("quit"))
                            break;
                        else if (received.equals("failed"))
                        {
                            System.out.println("resending bytes\n");
                            in.reset();
                            received = "received";
                            continue;
                        }
                        

                        // total bytes sent
                        fileSize -= bytesRead;
                        bytesSent += bytesRead;
                        System.out.println("---------> " + bytesSent + " bytes received\n");

                        //garbage collector should clean out the old buffer I think...
                        //Clean out the buffer and start fresh
                        buffer = new byte[1024];
                        break;
                    } 
                }
            }
            out.flush();
            in.close();
            in = null;
            if (received.equals("quit"))
            {
                System.out.println("Receiver could not download the file and "
                        + "then quitted.\nGuess I'll quit too."); 
                exit();
            }
            else 
                System.out.println("File uploaded and received successfully!");
        } catch (NullPointerException ex){
            menu();
        }           
          catch (IOException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Print and get answer from server if they are ready to receeive bytes    
    public void isReady() throws InterruptedException
    {        
        System.out.println("ready?");
        String ready = "";
        while (!ready.equals("ready"))
        {
            Thread.sleep(10); //10ms
            ready = getMessage();
            //ready = message.readMessage();
        }
    }
    
    
    // Print and get answer from server if they received bytes    
    public String hasReceived(String received) throws InterruptedException
    {        
        System.out.println("received?");  
        while (!received.equals("received") && !received.equals("failed") 
                && !received.equals("quit"))
        {   
            Thread.sleep(10); //10ms
            received = getMessage();
            //received = message.readMessage();
        }        
        
        if (received.equals("failed") || received.equals("quit"))
            System.out.println("failed");       
        
        return received;
    }
    
        //  Encodes/decodes the input with the key using XOR.
    public byte[] xorCipher(byte[] input, byte[] key){
        byte[] result = new byte[input.length];
        for(int i = 0; i < result.length; i++){
            result[i] = (byte)(((int) input[i]) ^ ((int) key[i % key.length]));
        }
        return result;
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
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}