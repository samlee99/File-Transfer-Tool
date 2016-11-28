/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FTP;
import client.Client;
import java.io.Console;
import java.io.*;
import java.util.Scanner;
import server.Server;
/**
 *
 * @author andrew
 */
public class FTP {
    public static void main(String[] args) throws InterruptedException, IOException
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
    private void client(Console console) throws InterruptedException, IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter host: ");
        String host = sc.nextLine();
        System.out.println("Enter port: ");
        String portInput = sc.nextLine();
        int port = Integer.parseInt(portInput);
        Client cln = new Client();
        boolean clientStarted = cln.start(host, port);
        int numOfAttempt = 100;
        while(!clientStarted && numOfAttempt >= 0){
            System.out.println("Failed to find server... Attempt #" + (100-numOfAttempt) + "\nTry again...");
            System.out.println("Enter host: ");
            host = sc.nextLine();
            System.out.println("Enter port: ");
            portInput = sc.nextLine();
            port = Integer.parseInt(portInput);     
            numOfAttempt--;
            clientStarted = cln.start(host, port);
        }
        cln.loginMenu(console);
    }
    private void server() throws IOException{
        Scanner sc = new Scanner(System.in);       
        Server srv = new Server();
        System.out.println("Enter a port to listen to:");
        String portInput = sc.nextLine();
        int port = Integer.parseInt(portInput);       
        srv.start(port);
        srv.authenticate();
        srv.menu();
    }
}
