/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 * @author andre
 */
public class CustomHash {
    //Implementation of the
    public String hash(String toHash){
        String hash = "" + toHash;
        //
        return hash;
    }
    
    public String passHash(String toHash){
        //TODO: REMOVE THIS!!!!!
        //Seriously, we need a custom hash, this is just for password testing
        String hash = toHash;
        try{
            MessageDigest mdEnc = MessageDigest.getInstance("MD5"); 
            mdEnc.update(toHash.getBytes(), 0, toHash.length());
            hash = new BigInteger(1, mdEnc.digest()).toString(16); // Hash value            
        }catch(Exception e){return hash;}
        return hash;
    }
}
