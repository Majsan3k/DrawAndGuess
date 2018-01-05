package game.mainFrame;

import game.chatt.ChattPanel;
import game.database.Score;
import game.gamePanel.*;
import game.highscore.HighScorePanel;
import game.logIn.LogInPanel;
import game.serverConnection.*;
import game.signUp.SignUpPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class GameFrame extends JFrame{
    private JLabel title;

    private Paper paper;
    private LogInPanel logInPanel;
    private ChattPanel chattPanel;
    private SignUpPanel signUpPanel;
    private HighScorePanel highScorePanel;
    private ServerHandler serverHandler;


    public GameFrame(Socket socket) throws IOException {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        title = new JLabel("Welcome", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        title.setPreferredSize(new Dimension(0, 60));
        add(title, BorderLayout.PAGE_START);

        paper = new Paper(this);
        chattPanel = new ChattPanel(this);
        logInPanel = new LogInPanel(this);
        signUpPanel = new SignUpPanel(this);
        highScorePanel = new HighScorePanel();
        serverHandler = new ServerHandler(this, socket);


        loginMode();

        //TODO: Ta bort, bara för testning
        loginRequest("Maja", "aa");

        pack();
        setVisible(true);
    }

    public void setHeader(String newTitle){
        title.setText(newTitle);
    }

    public void loginMode(){
        setHeader("Welcome");
        if(signUpPanel != null){
            remove(signUpPanel);
        }
        add(logInPanel, BorderLayout.CENTER);
        pack();
        repaint();
    }

    public void signUp(){
        remove(logInPanel);
        add(signUpPanel);
        pack();
        repaint();
    }

    /* Write to Server through ServerHandler */
    public void writeToServer(String message){
        serverHandler.writeToServer(message);
    }

    public void loginRequest(String username, String password){
        serverHandler.writeToServer("login:" + username + ":" + password);
    }

    public void signUpRequest(String username, String password){
        serverHandler.writeToServer("signUp:" + username + ":" + password);
    }

    /**
     * Update the whole GameFrame. Removes the loginPanel and add highScorePanel,
     * GamePanel and chattPanel.
     * Ask server for the current drawing.
     * @param username
     */
    public synchronized void login(String username){
        setTitle("Logged in as: " + username);
        updateScore();
        remove(logInPanel);
        add(highScorePanel, BorderLayout.WEST);
        add(new GamePanel(paper), BorderLayout.CENTER);
        add(chattPanel, BorderLayout.EAST);
        serverHandler.writeToServer("getdrawing" );
        drawFirstLogIn();
        pack();
        repaint();
    }

    public synchronized void drawFirstLogIn(){
        HashSet<Point> drawing = serverHandler.getDrawing();
        paper.setDrawing(drawing);
        paper.repaint();
    }

    /* Handlers for handling commands from ServerHandler */
    public void signUpHandler(String signupStatus){
        if(signupStatus.equalsIgnoreCase("OK")){
            signUpPanel.clearFields();
            loginMode();
        }else{
            signUpPanel.setErrorMessage(signupStatus);
        }
    }

    public void loginHandler(String loginStatus, String painterStatus, String username, String message, String serverName){
        if(loginStatus.equalsIgnoreCase("OK")){
            login(username);
        }else{
            logInPanel.setErrorMessage(loginStatus);
        }
        if (painterStatus.equalsIgnoreCase("true")) {
            paper.setPainter(true);
            chattPanel.setSecretWordMessage(true);
            chattPanel.displayMessage(serverName.toUpperCase() + ": " + message);
        }

    }

    public void showMessageHandler(String username, String message){
        if(message.trim().contains("logged out")){
            paper.clearPaper();
        }
        chattPanel.displayMessage(username.toUpperCase() + ": " + message);
    }

    public void showWinnerMessageHandler(String name, String message){
        paper.clearPaper();
        showMessageHandler(name, message);
    }

    public synchronized void updateScore(){
        serverHandler.writeToServer("getHighScore");
        ArrayList<Score> scores = serverHandler.getHighScore();
        Collections.sort(scores);
        highScorePanel.fillHighscoreField(scores);
    }

    public void addPointHandler(Point point){
        paper.addPoint(point);
    }

    public void setSecretWordHandler(){
        chattPanel.setSecretWordMessage(true);
    }

    public void setPainterHandler(boolean painter){
        paper.setPainter(painter);
    }
}