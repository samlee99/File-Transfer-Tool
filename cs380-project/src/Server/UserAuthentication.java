/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import Base64.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
/**
 *
 * @author andrew
 */
public class UserAuthentication {
    public boolean login(String username, String password){
        BufferedReader userfile;
        boolean foundUser = false;
        try{
            userfile = new BufferedReader(new FileReader("users.txt")); 
            String user;
            String savedSalt;
            String savedSaltedPassword;
            //Read line by line
            while((user = userfile.readLine()) != null){
                //The way we are storing passwords is username:salt:salted password
                String[] savedUserInfo = user.split(":");
                if(username.equals(savedUserInfo[0])){
                    savedSalt = savedUserInfo[1];
                    savedSaltedPassword = savedUserInfo[2];
                    String saltedPassword = createSaltedPassword(password, savedSalt);
                    if(savedSaltedPassword.equals(saltedPassword)){
                        foundUser = true;
                        break;
                    }
                }
            }        
            userfile.close();
        }catch(Exception e){}
        return foundUser;
    }
    public boolean createUser(String username, String password){
        System.out.println("Creating user: " + username + "-" + password);
        String user = "";
        String salt = createSalt();
        String saltedPassword = createSaltedPassword(password, salt);
        System.out.println(saltedPassword);
        user = username + ":" + salt + ":" + saltedPassword;
        try {
            System.out.println("Writing to file...");
            try (PrintWriter out = new PrintWriter(new FileOutputStream(new File("users.txt"),true))) {
                out.println(user);
            }
            return true;
        }catch (IOException ex) { System.out.println("IOException!");}
        return false;
    }
    private String createSalt(){
        String salt = "";
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.setSeed(random.generateSeed(16));
        random.nextBytes(bytes);
        Base64 b64 = new Base64();
        salt = b64.encode(bytes);
        salt = salt.replace("=", "");
        //TODO: Use Base64 encoding to convert bytes into a string
        //salt = new String(bytes, ""); 
        return salt;
    }
    private String createSaltedPassword(String password, String salt){
        String saltPassword = salt+password;
        CustomHash hash = new CustomHash();
        String saltedHash = saltPassword;
        return saltedHash;
    }

}
