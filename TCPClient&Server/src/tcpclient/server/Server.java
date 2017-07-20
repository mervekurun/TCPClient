/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author HP PAVİLİON 15
 */
public class Server {
    static ServerSocket serverSocket;
    private static ServerThread RunThread;
    public static int port = 1500;
    public static boolean keepGoing;
    public static int uniqueId;
    static ArrayList<Client> ConnectedClients;
    
    public Server(int port){
        Server.port=port;
        Server.ConnectedClients=new ArrayList<Client>();
    }
    public static void Set_Server(int port){
        try{
            Server.serverSocket=new ServerSocket(port);
            Server.port=port;
            Server.ConnectedClients=new ArrayList<Client>();
            Server.RunThread=new ServerThread();
            RunThread.start();
        }
        catch(IOException ex){
             
        }
    }
     public static void stop(){
         if(Server.serverSocket.isClosed()){
             return;
         }
         keepGoing = false;
         try{
            for(int i=ConnectedClients.size(); --i>=0;){
                Client ct=ConnectedClients.get(i);
                ct.close();
                ConnectedClients.remove(i);
            }
            Server.RunThread.interrupt();
            Server.serverSocket.close();
            Server.serverSocket=null;
         }
         catch(Exception e){
             
        }
     }
     public static void display(String msg){
         System.out.println(msg);
     }
     public static synchronized void broadcast(String message){
         for(int i=ConnectedClients.size();--i>=0;){
             Client ct= ConnectedClients.get(i);
             if(!ct.writeMsg(message)){
                 ConnectedClients.remove(i);
                 display("Disconned Client"+ ct.username+"removed from list");
             }
         }
     }
     public static synchronized void broadcast(Object message){
         for(int i=ConnectedClients.size();--i>=0;){
             Client ct= ConnectedClients.get(i);
             if(!ct.writeMsg(message)){
                 ConnectedClients.remove(i);
                 display("Disconned Client"+ ct.username+"removed from list");
             }
         }
     }
     public static synchronized void remove(int id){
         for(int i=0; i<ConnectedClients.size();++i){
             Client ct= ConnectedClients.get(i);
             if(ct.id==id){
                 ct.close();
                 ConnectedClients.remove(i);
                 return;
                             
             }
         }
     }
     static class ServerThread extends Thread{
    public void run (){
        try{
        while(!Server.serverSocket.isClosed()){
            display("Server waiting for Clients on port " + port + ".");
            Socket socket = Server.serverSocket.accept();
            Client newClient = new Client(socket);
            Server.ConnectedClients.add(newClient);
            newClient.start();
            newClient.writeMsg("baglandi");
        }
        
        Server.serverSocket.close();
        try{
        for (int i = 0; i < ConnectedClients.size(); i++) {
            Client tc = ConnectedClients.get(i);
            tc.close();
        }
        }catch(Exception e){
            display("Exception closing the server and clients" + e);
        }
        }
        catch(IOException e){
            String msg = new Date().toString() + "Exception on new ServerSocket : " + e +"\n";
            display(msg);
        }
    }
}
}
