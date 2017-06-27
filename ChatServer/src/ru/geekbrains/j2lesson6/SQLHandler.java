package ru.geekbrains.j2lesson6;

import java.sql.*;

public class SQLHandler {

    private static Connection c;
    private static PreparedStatement ps;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:chatDB.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPassword(String login, String password) {
        ResultSet rs;
        String str = null;
        try {
            ps = c.prepareStatement("SELECT nick FROM Users WHERE login = ? AND pass = ?");
            ps.setString(1, login);
            ps.setString(2, password);
            rs = ps.executeQuery();
            while (rs.next()) {
                str = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static void changeNickName(String oldNick,String newNick){
        try{
            ps = c.prepareStatement("UPDATE Users SET nick = ? WHERE nick = ?");
            ps.setString(1,newNick);
            ps.setString(2,oldNick);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
//
//    public static void addEntry(String login, String pass, String nick) {
//        try {
//            ps = c.prepareStatement("INSERT INTO Main (Login, Password, Nickname) VALUES (?, ?, ?);");
//            ps.setString(1, login);
//            ps.setString(2, pass);
//            ps.setString(3, nick);
//            ps.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


}