package ru.geekbrains.j2lesson6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by amifanick on 02.06.2017.
 */
public class ClientHandler implements Runnable {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;
    private MyServer owner;

    public ClientHandler(Socket s, MyServer server){
        this.s = s;
        this.owner = server;

        name="";
        try {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void run(){
        try {

            while (true) {
                String str = in.readUTF();
                if (name.isEmpty() && str != null) {
                    String[] x = str.split(" ");
                    if (x.length == 3) {
                        if (x[0].equals("/auth")) {
                            String login = x[1];
                            String pass = x[2];
                            String n = SQLHandler.getNickByLoginAndPassword(login, pass);
                            if (n != null) {
                                if(!owner.isNickBusy(n)) {
                                    name = n;
                                    sendMsg("a.u.t.h.");
                                    owner.addClient(this);

                                    break;
                                }else {
                                    sendMsg("This nick is already in use");
                                }
                            }else {
                                sendMsg("Wrong login or password");
                            }
                        }
                    }
                }
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (true) {
                String str = in.readUTF();
                if (!name.isEmpty() && str != null) {

                    if(str.startsWith("/")){

                        if(str.equals("/end")){
                            break;
                        }
                        if(str.startsWith("/pm")){
                            String[] w = str.split(" ");
                            String nick = w[1];
                            String msg = str.substring(w[0].length()+w[1].length()+2);
                            if(!getName().equals(nick)){
                                owner.personalMsg(nick, "from "+getName()+" : "+msg);
                                owner.personalMsg(getName(),"to "+nick+" : "+msg);
                            }
                        }
                        if(str.startsWith("/changeNick")){
                            String[] w = str.split(" ");
                            String nick = w[1];
                            SQLHandler.changeNickName(getName(),nick);

                            owner.broadcastMsg(getName()+" now is "+nick);
                            setName(nick);

                        }

                    } else{
                        System.err.println(name + ": " + str);
                        owner.broadcastMsg(name + ": " + str);
                    }


                }
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex){
            System.out.println("IO Exception");
        }
        finally {
            owner.removeClient(this);
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("IO Exception");
        }
    }

    public void sendMsg(String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
