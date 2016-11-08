/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.security.SecureRandom;
/**
 *
 * @author andre
 */
public class UserAuthentication {
    public boolean login(String username, String password){
        //TODO: Get the user info from a file 
        String user = "";
        String savedUsername;
        String savedSalt;
        String savedSaltedPassword;
        boolean foundUser = false;
        //TEMP! Going to be has next line for file reader
        boolean hasNext = false;
        while(hasNext){
            //Next line in file
            user = "";
            //The way we are storing passwords is username:salt:salted password
            String[] savedUserInfo = user.split(":");
            savedUsername = savedUserInfo[0];
            savedSalt = savedUserInfo[1];
            savedSaltedPassword = savedUserInfo[2];
            String saltedPassword = createSaltedPassword(password, savedSalt);
            if(username.equals(savedUsername) && savedSaltedPassword.equals(saltedPassword)){
                foundUser = true;
                break;
            }
        }        
        return foundUser;
    }
    public String createUser(String username, String password){
        String user = "";
        String salt = createSalt();
        String saltedPassword = createSaltedPassword(password, salt);
        user += username + ":" + salt + ":" + saltedPassword;
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
