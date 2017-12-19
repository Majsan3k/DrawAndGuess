package game;

import game.database.DatabaseConnection;
import game.database.Score;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {

    private ConcurrentHashMap<String, Socket> players = new ConcurrentHashMap<>();

    private ServerSocket serverSocket;
    private PrintWriter pw;
    private String secretWord;
    private Socket painter;
    private DatabaseConnection dbCon;

    public Server(int port){
        dbCon = new DatabaseConnection();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Couldn't connect " + e.getMessage());
            System.exit(1);
        }
    }

    public void signUp(String username, String password, Socket socket){
        String signUpStatus = dbCon.createUser(username, password);
        writeToOneSocket(socket, "signup:" + signUpStatus);
    }


    /**
     * Check if the player already is logged in. If no there's no existing painter, the new player become the painter.
     *
     * @param username the players username
     * @param socket the socket that the player is connected to
     */
    public void login(String username, String password, Socket socket){

        String loginStatus = "";
        String painterStatus = "false";

        if(players.containsKey(username)) {
            loginStatus = "User already logged in";
        }else if(!dbCon.login(username, password)){
            loginStatus = "Wrong username or password";
        }else{
            loginStatus = "OK";
            if (painter == null) {
                painter = socket;
                painterStatus = "true";
            }
        }
        writeToOneSocket(socket, "login:" + loginStatus + ":" + painterStatus + ":" + "Welcome! You are the new painter! Please write your secret word. First write \"secretword\" and then your word");
        if(loginStatus.equals("OK")) {
            broadCastMessage("message:", username + " logged in:none");
            players.put(username, socket);
        }
    }

    /**
     * Decide if the player should be painter. Write to specific socket and tell if it should be a painter or not
     * @param socket
     */
    public void setPainter(Socket socket){
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
        this.secretWord = secretWord;
        writeToOneSocket(socket, "message:The secret word \"" + secretWord + "\" has been choosed, start to draw!:Server");
    }

    /**
     * Get the secret word
     * @return
     */
    public String getSecretWord(){
        return secretWord;
    }

    /**
     * Ask painter to decide a secret word
     * @param socket the painter socket
     */
    public void askSecretWord(Socket socket){
        writeToOneSocket(socket, "message:Congratulations, you are the new painter! Please write your secret word. First write \"secretword\" and then your word:Server");
    }

    public synchronized ArrayList<Score> getHighScore(){
        return dbCon.getHighscore();
    }

    public synchronized void broadCastMessage(String command, String inData){
        System.out.println(command + " " + inData);
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
        broadCastMessage("winner:", username + " is the winner and the new painter!:Server");
        setPainter(players.get(username));
        dbCon.updateScore(username);
        secretWord = null;
        broadCastMessage("highscore:", "none");
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
            painter = null;
        }
        players.remove(username);
        broadCastMessage("message:", username + " logged out:none");
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
        Server server = new Server(port);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}