package game.serverConnection;

import game.chatt.ChattPanel;
import game.database.Score;
import game.gamePanel.Paper;
import game.logIn.LogInPanel;
import game.mainFrame.GameFrame;
import game.signUp.SignUpPanel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerReader implements Runnable{

    private ChattPanel chattWindow;
    private SignUpPanel signUpPanel;
    private LogInPanel logInPanel;
    private Paper paper;
    private BufferedReader serverIn;
    private ObjectInputStream objIn;
    private GameFrame gameFrame;
    private Socket socket;

    public ServerReader(Socket socket, ChattPanel chattWindow, LogInPanel logInPanel, Paper paper, GameFrame gameFrame, SignUpPanel signUpPanel){
        this.chattWindow = chattWindow;
        this.logInPanel = logInPanel;
        this.paper = paper;
        this.gameFrame = gameFrame;
        this.signUpPanel = signUpPanel;
        this.socket = socket;

        try {
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void signUp(String signupStatus){
        if(signupStatus.equalsIgnoreCase("OK")){
            signUpPanel.clearFields();
            gameFrame.loginMode();
        }else{
            signUpPanel.setErrorMessage(signupStatus);
        }
    }

    private void login(String logInStatus, String painterStatus, String username, String message, String serverName){
        if(logInStatus.equalsIgnoreCase("OK")){
            gameFrame.login(username);
        }else{
            logInPanel.setErrorMessage(logInStatus);
        }
        if (painterStatus.equalsIgnoreCase("true")) {
            paper.setPainter(true);
            chattWindow.setSecretWordMessage(true);
            chattWindow.displayMessage(serverName.toUpperCase() + ": " + message);
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

    private void showMessage(String username, String message){
        if(message.trim().contains("logged out")){
            paper.clearPaper();
        }
        chattWindow.displayMessage(username.toUpperCase() + ": " + message);
    }

    private void showWinnerMessage(String name, String message){
        paper.clearPaper();
        showMessage(name, message);
    }

    @Override
    public void run() {
        String message;
        try {
            while((message = serverIn.readLine()) != null){
                System.out.println(message);

                String[] command = message.split(":");

                switch (command[0]){
                    case "signup" :
                        signUp(command[1]);
                        break;
                    case "login" :
                        login(command[1], command[2], command[3], command[4], command[5]);
                        break;
                    case "message" :
                        showMessage(command[2], command[1]);
                        break;
                    case "winner" :
                        showWinnerMessage(command[2], command[1]);
                        break;
                    case "highscore" :
                        gameFrame.updateScore();
                        break;
                    case "draw" :
                        String[] xy = command[1].split(", ");
                        Point receivedPoint = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        paper.addPoint(receivedPoint);
                        break;
                    case "secretword" :
                        showMessage(command[2], command[1]);
                        chattWindow.setSecretWordMessage(true);
                        break;
                    case "painter" :
                        paper.setPainter(command[1].equals("true"));
                }
            }
        }catch(SocketException s){
            try{
                serverIn.close();
                objIn.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
            chattWindow.displayMessage("You lost connection");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
