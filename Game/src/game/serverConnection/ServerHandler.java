package game.serverConnection;

import game.database.Score;
import game.mainFrame.GameFrame;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ServerHandler {

    private GameFrame gameFrame;
    private ServerPrinter serverPrinter;
    private ServerReader serverReader;

    /**
     * Constructor. Create a ServerPrinter and a serverWriter and start a serverReader thread
     * @param gf GameFrame that the Serverhandler is connected to
     * @param socket socket to be used for server communication
     * @throws IOException shows for problem with creating a ServerPrinter
     */
    public ServerHandler(GameFrame gf, Socket socket) throws IOException{
        this.gameFrame = gf;
        this.serverPrinter = new ServerPrinter(socket);

        serverReader = new ServerReader(socket, this);
        Thread thread = new Thread(serverReader);
        thread.start();
    }

    /* Communication with ServerPrinter */
    public void writeToServer(String message){
        serverPrinter.writeToServer(message);
    }

    /* Communication between GameFrame and ServerReader */

    public synchronized ArrayList<Score> getHighScore(){
        return serverReader.getHighScore();
    }

    public synchronized void getDrawing(){
        writeToServer("getdrawing");
        HashSet<Point> drawing = serverReader.getDrawing();
        gameFrame.drawCurrentDrawing(drawing);
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

    public synchronized void showWinnerMessage(String name, String message){
        gameFrame.showWinnerMessageHandler(name, message);
    }

    public synchronized void updateScore(){
        writeToServer("getHighScore");
        ArrayList<Score> scores = getHighScore();
        Collections.sort(scores);
        gameFrame.updateScoreHandler(scores);
    }

    public synchronized void addPoint(Point point){
        gameFrame.addPointHandler(point);
    }

    public void setSecretWord(boolean secretWord){
        gameFrame.setSecretWordHandler(secretWord);
    }

    public void setPainter(boolean painter){
        gameFrame.setPainterHandler(painter);
    }
}
