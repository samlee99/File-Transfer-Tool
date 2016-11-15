/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FTP;
import client.Client;
import java.io.Console;
import server.Server;
/**
 *
 * @author andrew
 */
public class FTP {
    public static void main(String[] args) throws InterruptedException
    {
        Console console = System.console();
        if(console == null){
            System.out.println("Console instance not found...");
            System.exit(0);
        }
        FTP ftp = new FTP();
        if(args.length > 0){
            if(args[0].equals("server")){
                ftp.server();
                return;
            }
            else if(args[0].equals("client")){ 
                ftp.client(console);
                return;
            }
        }
        help();
    }  
    private static void help(){
        System.out.println("Usage: \"client\" or \"server\"");
    }
    private void client(Console console) throws InterruptedException{
        Client cln = new Client();
        boolean clientStarted = cln.start("localhost", 8888);
        int numOfAttempt = 100;
        while(!clientStarted && numOfAttempt >= 0){
            System.out.println("Failed to find server... Attempt #" + (100-numOfAttempt));
            Thread.sleep(1000);
            numOfAttempt--;
            clientStarted = cln.start("localhost", 8888);
        }
        cln.loginMenu(console);
        cln.menu();
    }
    private void server(){
        Server srv = new Server();
        srv.start(8888);
        srv.authenticate();
        srv.menu();
    }
}
