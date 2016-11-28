/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.math.BigInteger;
import java.security.MessageDigest;
import SHA1.SHA1;

/**
 *
 * @author andre
 */
public class CustomHash {
    
	SHA1 sha1;
	
	
    public String passHash(String toHash){
        //TODO: REMOVE THIS!!!!!
        //Seriously, we need a custom hash, this is just for password testing
        byte[] byteHash = toHash.getBytes();
		String hash = "";
        try{
            hash = sha1.encode(byteHash);
        }catch(Exception e){return hash;}
        return hash;
    }
}
