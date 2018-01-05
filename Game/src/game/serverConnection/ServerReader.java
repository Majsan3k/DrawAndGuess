package game.serverConnection;

import game.database.Score;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerReader implements Runnable{

    private BufferedReader serverIn;
    private ObjectInputStream objIn;
    private Socket socket;
    private ServerHandler serverHandler;

    public ServerReader(Socket socket, ServerHandler serverHandler){
        this.serverHandler = serverHandler;
        this.socket = socket;

        try {
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<Score> getHighScore(){
        ArrayList<Score> scores = null;
        try {
            objIn = new ObjectInputStream(socket.getInputStream());
            scores = (ArrayList<Score>) objIn.readObject();
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }
        return scores;
    }

    public synchronized HashSet<Point> getDrawing(){
        HashSet<Point> drawing = null;
        try{
            objIn = new ObjectInputStream(socket.getInputStream());
            drawing = (HashSet<Point>) objIn.readObject();
        }catch (Exception e){
            System.out.println(e.getClass() + ": " + e.getMessage());
        }
        return drawing;
    }

    @Override
    public void run() {
        String message;
        try {
            while((message = serverIn.readLine()) != null){
                String[] command = message.split(":");
                switch (command[0]){
                    case "signup" :
                        serverHandler.signUp(command[1]);
                        break;
                    case "login" :
                        serverHandler.login(command[1], command[2], command[3], command[4], command[5]);
                        break;
                    case "message" :
                        serverHandler.showMessage(command[2], command[1]);
                        break;
                    case "winner" :
                        serverHandler.showWinnerMessage(command[2], command[1]);
                        break;
                    case "highscore" :
                        serverHandler.updateScore();
                        break;
                    case "draw" :
                        String[] xy = command[1].split(", ");
                        Point receivedPoint = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        serverHandler.addPoint(receivedPoint);
                        break;
                    case "secretword" :
                        serverHandler.showMessage(command[2], command[1]);
                        serverHandler.setSecretWord();
                        break;
                    case "painter" :
                        serverHandler.setPainter(command[1].equals("true"));
                }
            }
        }catch(SocketException s){
            try{
                serverIn.close();
                objIn.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
            serverHandler.showMessage("", "You lost connection");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
