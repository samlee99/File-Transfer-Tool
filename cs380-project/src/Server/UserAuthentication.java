/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
            userfile = new BufferedReader(new FileReader("\\users.txt")); 
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
    public String createUser(String username, String password){
        String user = "";
        String salt = createSalt();
        String saltedPassword = createSaltedPassword(password, salt);
        user += username + ":" + salt + ":" + saltedPassword;
        try {
            try (BufferedWriter userfile = new BufferedWriter(new FileWriter("\\users.txt"))) {
                userfile.write(user + "\n");
            }
        }catch (IOException ex) {}
        return user;
    }
    private String createSalt(){
        String salt = "";
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.setSeed(random.generateSeed(16));
        random.nextBytes(bytes);
        //TODO: Use Base64 encoding to convert bytes into a string
        //salt = new String(bytes, ""); 
        return salt;
    }
    private String createSaltedPassword(String password, String salt){
        String saltPassword = salt+password;
        CustomHash hash = new CustomHash();
        String saltedHash = hash.hash(saltPassword);
        return saltedHash;
    }

}
