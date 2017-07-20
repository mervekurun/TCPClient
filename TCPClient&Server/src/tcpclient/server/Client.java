/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 *
 * @author HP PAVİLİON 15
 */
public class Client {
    private Socket socket;
        private ObjectInputStream sInput;
        private ObjectOutputStream sOutput;
        private ClientThread ListenThread ;
        //DefaultListModel list;
        //private String server;
        public int id ;
        //private int port;
        public String username;
        public Date ConDate;

            Client(Socket socket) {
         
            this.id = ++Server.uniqueId;
            this.socket = socket;
            
            try{
            this.sOutput = new ObjectOutputStream(socket.getOutputStream());
            this.sInput = new ObjectInputStream(socket.getInputStream());
            this.username = (String) sInput.readObject();
            this.ConDate = new Date();
            this.ListenThread = new ClientThread(this);
            }catch(IOException e){
                Server.display("Exception creating new Input/outout Streams " + e);
                return;
            }catch(ClassNotFoundException e){
                
            }
        
            }
           
           public void start(){
            this.ListenThread = new ClientThread(this);
            this.ListenThread.start();
           }
           
           public void close(){
           try{
               if (this.ListenThread != null) {
                   this.ListenThread.interrupt();
               }
               if (this.sOutput != null) {
                   this.sOutput.close();
               }
               if (this.sInput != null) {
                   this.sInput.close();
               }
               if (this.socket != null) {
                   this.socket.close();
               }
           }catch(Exception e){
               
           }
           }
           
           public boolean writeMsg(String msg){
               if (!this.socket.isConnected()) {
                   close();
                   return false;
               }
               
         try {
             this.sOutput.writeObject(msg);
         } catch (IOException ex) {
            Server.display("Error sending message to " + username);
            Server.display(ex.toString());
         }
         return true;
           }
           
        public boolean writeMsg(Object msg){
               if (!this.socket.isConnected()) {
                   close();
                   return false;
               }
               
         try {
             this.sOutput.writeObject(msg);
         } catch (IOException ex) {
            Server.display("Error sending message to " + username);
            Server.display(ex.toString());
         }
         return true;
      }
           
             
          class ClientThread extends Thread {
              Client TheClient;
              
              ClientThread(Client TheClient){
                  this.TheClient = TheClient;
              }
              public void run(){
                  while(TheClient.socket.isConnected()){
                      try{
                      String message = (String) this.TheClient.sInput.readObject();
                          System.out.println(message);
                      }catch(IOException err){
                       Server.display(this.TheClient.username + "Exception reading Streams : " + err);
                      }catch(ClassNotFoundException err){
                          Server.display(this.TheClient.username + "Exception reading Streams " + err);
                      }
                  }
                  Server.remove(this.TheClient.id);
              }
          }
}
