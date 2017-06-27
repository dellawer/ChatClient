package ru.geekbrains.j2lesson6;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by amifanick on 06.06.2017.
 */
public class MyServer {
    private ArrayList<ClientHandler> clients;

    public MyServer(){
        ServerSocket server = null;
        Socket s = null;
        clients = new ArrayList<>();
        SQLHandler.connect();

        try {
            server = new ServerSocket(8189);
            while (true) {
                System.out.println("Waiting for clients...");
                s = server.accept();

                System.out.println("Client connected");
                ClientHandler ch = new ClientHandler(s,this);

                new Thread(ch).start();


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
                SQLHandler.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void personalMsg(String nick, String msg){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime());
        for (ClientHandler ch :
                clients) {
            if(ch.getName().equals(nick)){
                ch.sendMsg(time+" "+msg);
                break;
            }

        }
    }

    public synchronized boolean isNickBusy(String nick){
        for (ClientHandler ch :
                clients) {
            if (ch.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime());
        for (ClientHandler ch :
                clients) {
            ch.sendMsg(time+" "+msg);
        }
    }

    public synchronized void removeClient(ClientHandler ch){
        clients.remove(ch);
        broadcastMsg("Client "+ch.getName()+" disconnected from chatroom");
    }

    public synchronized void addClient(ClientHandler ch){
        if(!clients.contains(ch)) {
            clients.add(ch);
            broadcastMsg("Client " + ch.getName() + " connected to chatroom");
        }
    }
}