package game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PlayerHandler implements Runnable {

    private Server server;
    private Socket socket;
    private BufferedReader inDataPlayer;
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

    /**
     * Listens for messages from Client
     */
    @Override
    public void run() {
        String inData;
        try{
            while((inData = inDataPlayer.readLine()) != null) {
                String[] command = inData.split(":");

                switch(command[0]){
                    case "signUp" :
                        server.signUp(command[1], command[2], socket);
                        break;
                    case "login" :
                        this.username = command[1];
                        String password = command[2];
                        server.login(username, password, socket);
                        break;
                    case "message" :
                        server.broadcastMessage("message:", command[1] + ":" + username);
                        if(server.checkSecretWord(command[1], socket)){
                            server.winnerMessage(username);
                        }
                        break;
                    case "getHighScore" :
                        server.sendHighscore(socket);
                        break;
                    case "draw" :
                        String[] xy = command[1].split(", ");
                        server.addPoint(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
                        server.broadcastMessage("draw:", command[1]);
                        break;
                    case "getdrawing" :
                        server.sendDrawing(socket);
                        break;
                    case "secretword" :
                        server.setSecretWord(command[1], socket);
                        break;
                    default :
                        break;
                }
            }
            inDataPlayer.close();
            socket.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        server.removeClient(username);
    }
}
