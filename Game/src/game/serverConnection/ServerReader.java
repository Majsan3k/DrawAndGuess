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
            gameFrame.loginMode();
        }else{
            signUpPanel.setErrorMessage(signupStatus);
        }
    }

    private void login(String logInStatus, String painterStatus, String message){
        if(logInStatus.equalsIgnoreCase("OK")){
            gameFrame.login();
        }else{
            logInPanel.setErrorMessage(logInStatus);
        }
        if (painterStatus.equalsIgnoreCase("true")) {
            paper.setPainter(true);
            chattWindow.displayMessage(message);
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

    private void showMessage(String username, String message){
        if(!username.equalsIgnoreCase("none")) {
            chattWindow.displayMessage(username + ": " + message);
        }else{
            chattWindow.displayMessage(message);
        }
    }

    private void showWinnerMessage(String message){
        paper.clearPaper();
        chattWindow.displayMessage(message);
    }

    @Override
    public void run() {
        String message;
        try {
            while((message = serverIn.readLine()) != null){
                System.out.println(message);

                String[] command = message.split(":");
                if(command.length > 1) {
                    String cmd = command[0];
                    String val = command[1];

                    if(cmd.equalsIgnoreCase("signup")){
                        signUp(val);
                    }

                    /* Log in values */
                    if (cmd.equalsIgnoreCase("login")) {
                        login(val, command[2], command[3]);
                    }

                    /* Message */
                    if (cmd.equalsIgnoreCase("message")) {
                        showMessage(command[2], val);
                    }

                    /* Winner message */
                    if(cmd.equalsIgnoreCase("winner")){
                        showWinnerMessage(val);
                    }

                    /* Highscore has been updated */
                    if(cmd.equalsIgnoreCase("highscore")){
                        gameFrame.updateScore();
                    }

                    /* Draw information */
                    if(cmd.equalsIgnoreCase("draw")){
                        String[] xy = val.split(", ");
                        Point receivedPoint = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        paper.addPoint(receivedPoint);
                    }

                    /* Server asks for a secret word */
                    if(cmd.equals("secretWord")){
                        chattWindow.displayMessage(val);
                    }

                    /* Server tells if painter or not */
                    if(cmd.equals("painter")){
                        if(command[1].equals("true")){
                            paper.setPainter(true);
                        }else{
                            paper.setPainter(false);
                        }
                    }
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
