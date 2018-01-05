package game;

import game.database.DatabaseConnection;
import game.database.Score;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {

    private ConcurrentHashMap<String, Socket> players = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private PrintWriter pw;
    private ObjectOutputStream objOut;
    private String secretWord;
    private Socket painter;
    private String painterName;
    private DatabaseConnection dbCon;
    private HashSet<Point> drawing = new HashSet<>();

    public Server(int port){
        dbCon = new DatabaseConnection();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Couldn't connect " + e.getMessage());
            System.exit(1);
        }
    }

    public synchronized void sendHighscore(Socket socket){
        ArrayList<Score> scores = getHighScore();
        try {
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.writeObject(scores);
            objOut.flush();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public synchronized void sendDrawing(Socket socket){
        try {
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.writeObject(getDrawing());
            objOut.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void signUp(String username, String password, Socket socket){
        String signUpStatus = dbCon.createUser(username, password);
        writeToOneSocket(socket, "signup:" + signUpStatus);
    }

    public HashSet<Point> getDrawing(){
        return drawing;
    }

    public void addPoint(Point p){
        drawing.add(p);
    }

    /**
     * Check if the player already is logged in. If no there's no existing painter, the new player become the painter.
     *
     * @param username the players username
     * @param socket the socket that the player is connected to
     */
    public synchronized void login(String username, String password, Socket socket){

        String loginStatus = "";
        String painterStatus = "false";

        if(players.containsKey(username)) {
            loginStatus = "User already logged in";
        }else if(!dbCon.login(username, password)){
            loginStatus = "Wrong username or password";
        }else{
            loginStatus = "OK";
            if (painter == null) {
                painterName = username;
                painter = socket;
                painterStatus = "true";
            }
        }
        writeToOneSocket(socket, "login:" + loginStatus + ":" + painterStatus + ":" + username + ":" + "Welcome! You are the new painter! Please write your secret word or sentence.:Server");
        if(loginStatus.equals("OK")) {
            broadCastMessage("message:", username + " logged in.:server");
            players.put(username, socket);
        }
    }

    /**
     * Decide if the player should be painter. Write to specific socket and tell if it should be a painter or not
     * @param socket
     */
    public void setPainter(Socket socket, String username){
        painterName = username;
        if(painter != null){
            writeToOneSocket(painter, "painter:false");
        }
        painter = socket;
        writeToOneSocket(painter, "painter:true");
        askSecretWord(socket);
    }


    /* Secret word handlers */

    /**
     * Set new secret word and accept the secret word to the painter
     * @param secretWord new secret word
     * @param socket the painter
     */
    public void setSecretWord(String secretWord, Socket socket){
        if(socket == painter) {
            this.secretWord = secretWord;
            writeToOneSocket(socket, "message:The secret word \"" + secretWord + "\" has been chosen, start to draw!:Server");
        }else{
            writeToOneSocket(socket, "message:You can't decide the secret word since you're not the drawer!:Server");
        }
    }

    /**
     * Get the secret word
     * @return
     */
    public boolean checkSecretWord(String guess, Socket socket){
        return guess.equalsIgnoreCase(secretWord) && painter != socket;
    }

    /**
     * Ask painter to decide a secret word
     * @param socket the painter socket
     */
    public void askSecretWord(Socket socket){
        writeToOneSocket(socket, "secretword:Congratulations, you are the new painter! Please write your secret word or sentence.:SERVER");
    }

    public synchronized ArrayList<Score> getHighScore(){
        return dbCon.getHighscore();
    }

    public synchronized void broadCastMessage(String command, String inData){
        for (Map.Entry<String, Socket> s : players.entrySet()) {
            try {
                pw = new PrintWriter(s.getValue().getOutputStream(), true);
                pw.println(command + inData);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public synchronized void winnerMessage(String username){
        drawing.clear();
        broadCastMessage("winner:", username + " is the winner and the new painter!:Server");
        dbCon.updateScore(painterName);
        dbCon.updateScore(username);
        setPainter(players.get(username), username);
        secretWord = null;
        broadCastMessage("highscore:", "SERVER");
    }

    private synchronized void writeToOneSocket(Socket socket, String message){
        try {
            pw = new PrintWriter(socket.getOutputStream(), true);
            pw.println(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized void removeClient(String username){

        if(painter == players.get(username)){
            drawing.clear();
            players.remove(username);
            painter = null;
            if(players.size() > 0) {
                String newPainter = (String)players.keySet().toArray()[0];
                broadCastMessage("message:", username + " logged out. The new painter is " + newPainter + ".:SERVER");
                setPainter(players.get(newPainter), newPainter);
            }
        }else {
            players.remove(username);
            broadCastMessage("message:", username + " logged out.:SERVER");
        }
    }

    @Override
    public void run() {
        try{
            while (true){
                Socket socket = serverSocket.accept();
                Thread playerThread = new Thread(new PlayerHandler(this, socket));
                playerThread.start();
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String args[]){
        int port = 2000;
        if(args.length != 0){
            port = Integer.parseInt(args[0]);
        }
        if(args.length > 1){
            System.exit(1);
        }
        Thread serverThread = new Thread(new Server(port));
        serverThread.start();
    }
}