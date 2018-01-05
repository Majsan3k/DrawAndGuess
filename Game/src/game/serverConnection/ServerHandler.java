package game.serverConnection;

import game.database.Score;
import game.mainFrame.GameFrame;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerHandler {

    private GameFrame gameFrame;
    private ServerPrint serverPrint;
    private ServerReader serverReader;

    public ServerHandler(GameFrame gf, Socket socket) throws IOException{
        this.gameFrame = gf;
        this.serverPrint = new ServerPrint(socket);

        serverReader = new ServerReader(socket, this);
        Thread thread = new Thread(serverReader);
        thread.start();
    }

    /* Communication with ServerPrinter */
    public void writeToServer(String message){
        serverPrint.writeToServer(message);
    }

    /* Communication between GameFrame and ServerReader */
    public synchronized ArrayList<Score> getHighScore(){
        return serverReader.getHighScore();
    }

    public synchronized HashSet<Point> getDrawing(){
        return serverReader.getDrawing();
    }

    public void signUp(String signupStatus){
        gameFrame.signUpHandler(signupStatus);
    }

    public void login(String logInStatus, String painterStatus, String username, String message, String serverName){
        gameFrame.loginHandler(logInStatus, painterStatus, username, message, serverName);
    }

    public void showMessage(String username, String message){
        gameFrame.showMessageHandler(username, message);
    }

    public void showWinnerMessage(String name, String message){
        gameFrame.showWinnerMessageHandler(name, message);
    }

    public void updateScore(){
        gameFrame.updateScore();
    }

    public void addPoint(Point point){
        gameFrame.addPointHandler(point);
    }

    public void setSecretWord(){
        gameFrame.setSecretWordHandler();
    }

    public void setPainter(boolean painter){
        gameFrame.setPainterHandler(painter);
    }







}
