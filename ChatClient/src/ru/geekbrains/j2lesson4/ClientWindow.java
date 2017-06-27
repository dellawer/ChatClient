package ru.geekbrains.j2lesson4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by amifanick on 30.05.2017.
 */
public class ClientWindow extends JFrame{

    private JTextArea jta;
    private JTextField jtf;
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private JTextFieldWithHint jtfLogin;
    private JPasswordField jtfPass;
    private Thread threadRead;
    private JPanel authPanel;
    private JPanel bottom;

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        if(isAuthorized){
            authPanel.setVisible(false);
            bottom.setVisible(true);
        }else{
            authPanel.setVisible(true);
            bottom.setVisible(false);

        }
    }

    private boolean isAuthorized;


    public ClientWindow(){
        setTitle("Client");
        setBounds(600,300,400,400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());



        jta = new JTextArea();
        jta.setEditable(false);
        jta.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(jta);

        add(jsp,BorderLayout.CENTER);

        bottom = new JPanel(new BorderLayout());
        jtf = new JTextField();
        JButton sendMsgBt = new JButton("Send");
        bottom.add(jtf,BorderLayout.CENTER);
        bottom.add(sendMsgBt,BorderLayout.EAST);
        add(bottom,BorderLayout.SOUTH);

        sendMsgBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
        bottom.setVisible(false);


        addActListoJTF();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                try {
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        authPanel = new JPanel(new GridLayout());
        jtfLogin = new JTextFieldWithHint("Login");
        jtfPass = new JPasswordField();
        JButton jbAuth = new JButton("Войти");
        authPanel.add(jtfLogin);
        authPanel.add(jtfPass);
        authPanel.add(jbAuth);
        add(authPanel,BorderLayout.NORTH);
        jbAuth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                try {
                    out.writeUTF("/auth "+jtfLogin.getText()+" "
                            +new String(jtfPass.getPassword()));
                    out.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        threadRead = null;
        setAuthorized(false);

        setVisible(true);
    }

    public void connect(){

        if(s==null || (s!=null && s.isClosed())) {
            try {
                s = new Socket("localhost", 8189);
                in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());


            } catch (IOException e) {
                e.printStackTrace();
            }
            if (threadRead == null) {
                threadRead = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                String str = in.readUTF();
                                if (str != null) {
                                    System.err.println(str);
                                }
                                if (!isAuthorized) {
                                    if (str != null) {
                                        if (str.equals("a.u.t.h.")) {
                                            setAuthorized(true);
                                            continue;

                                        }
                                        jta.append(str + "\n");
                                    }
                                }

                                if (isAuthorized && str != null) {
                                    jta.append(str + "\n");
                                }
                                try {
                                    Thread.currentThread().sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (IOException ex) {
                            threadRead = null;
                            try {
                                s.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            setAuthorized(false);
                            jta.append("Connection lost...\n");
                            System.out.println("Server disconnected");
                        }
                    }
                });
                threadRead.start();
            }
        }
    }

    private void addActListoJTF(){
        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });


    }

    public void sendMsg(){
        try {
            if(!jtf.getText().trim().isEmpty()) {
                out.writeUTF(jtf.getText());
                out.flush();
            }
        } catch (IOException e) {
            threadRead = null;
            try {
                s.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            jta.append("Connection lost...\n");
            setAuthorized(false);
            System.out.println("Server disconnected");
        }

        jtf.setText("");
        jtf.grabFocus();
    }


}
