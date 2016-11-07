/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FTP;
import client.Client;
import server.Server;
/**
 *
 * @author andrew
 */
public class FTP {
    public static void main(String[] args)
    {
        FTP ftp = new FTP();
        if(args.length > 0){
            if(args[0].equals("server")){
                ftp.server();
                return;
            }
            else if(args[0].equals("client")){ 
                ftp.client();
                return;
            }
        }
        help();
    }  
    private static void help(){
        System.out.println("Usage: \"client\" or \"server\"");
    }
    private void client(){
        Client cln = new Client();
        cln.start("localhost", 8888);
        cln.login();
        cln.menu();
    }
    private void server(){
        Server srv = new Server();
        srv.start(8888);
        srv.authenticate();
        srv.menu();
    }
}
