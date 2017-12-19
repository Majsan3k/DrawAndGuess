package game;

import game.database.Score;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class PlayerHandler implements Runnable {

    private Server server;
    private Socket socket;
    private BufferedReader inDataPlayer;
    private ObjectOutputStream objOut;
    private String username = "";

    public PlayerHandler(Server server, Socket socket){
        this.server = server;
        this.socket = socket;

        try {
            inDataPlayer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public synchronized void getHighscore(){
        ArrayList<Score> scores = server.getHighScore();
        try {
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.writeObject(scores);
            objOut.flush();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        String inData;
        try{
            while((inData = inDataPlayer.readLine()) != null) {
                String[] command = inData.split(":");

                if(command[0].equalsIgnoreCase("signUp")){
                    server.signUp(command[1], command[2], socket);
                }

                if (command[0].equals("login")) {
                    this.username = command[1];
                    String password = command[2];
                    server.login(username, password, socket);
                }

                if(command[0].equalsIgnoreCase("getHighScore")){
                    getHighscore();
                }

                if (command[0].equals("message")) {
                    if(command[1].contains("secretword")){
                        String[] secretWord = command[1].split(" ");
                        server.setSecretWord(secretWord[1], socket);
                    }else {
                        server.broadCastMessage("message:", command[1] + ":" + username);
                        if(command[1].equalsIgnoreCase(server.getSecretWord())){
                            server.winnerMessage(username);
                        }
                    }
                }
                if(command[0].equals("draw")){
                    server.broadCastMessage("draw:", command[1]);
                }
            }
            inDataPlayer.close();
            socket.close();
            objOut.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        server.removeClient(username);
    }
}
