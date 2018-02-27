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
import java.util.HashSet;

public class GameFrame extends JFrame{
    private JLabel title;
    private LogInPanel logInPanel;
    private GamePanel gamePanel;
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

        chattPanel = new ChattPanel(this);
        gamePanel = new GamePanel(this);
        logInPanel = new LogInPanel(this);
        signUpPanel = new SignUpPanel(this);
        highScorePanel = new HighScorePanel();
        serverHandler = new ServerHandler(this, socket);

        loginMode();

        loginRequest("Maja", "aaaaaa");
        pack();
        setVisible(true);
    }

    public void setHeader(String newTitle){
        title.setText(newTitle);
    }

    /**
     * Start page. User can choose between login or signup.
     */
    public void loginMode(){
        setHeader("Welcome");
        if(signUpPanel != null){
            remove(signUpPanel);
        }
        add(logInPanel, BorderLayout.CENTER);
        pack();
        repaint();
    }

    /**
     * Goes to signup page.
     */
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
        writeToServer("login:" + username + ":" + password);
    }

    public void signUpRequest(String username, String password){
        writeToServer("signUp:" + username + ":" + password);
    }

    /**
     * Update the whole GameFrame. Removes the loginPanel and add highScorePanel,
     * GamePanel and chattPanel.
     * Asks server for the current drawing and draw it on gamePanel. Also asks for
     * the highscore and update the highScorePanel with it.
     *
     * @param username
     */
    public synchronized void login(String username){
        setTitle("Logged in as: " + username);
        serverHandler.updateScore();
        remove(logInPanel);
        add(highScorePanel, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);
        add(chattPanel, BorderLayout.EAST);
        serverHandler.getDrawing();
        pack();
        repaint();
    }

    /**
     * Gets the current drawing from server and paints it on gamePanel.
     */
    public synchronized void drawCurrentDrawing(HashSet<Point> drawing){
        gamePanel.setDrawing(drawing);
    }

    /* Handlers for handling commands from ServerHandler */

    /**
     * Checks if the signup was approved by the server and then go back to login mode. Else show
     * errormessage.
     *
     * @param signupStatus information from server, tells if the signup was approved
     */
    public void signUpHandler(String signupStatus){
        if(signupStatus.equalsIgnoreCase("OK")){
            signUpPanel.clearFields();
            loginMode();
        }else{
            signUpPanel.setErrorMessage(signupStatus);
        }
    }

    /**
     * Handles login. Checks if the login passed on server side and if the user is the painter.
     *
     * @param loginStatus information from server if the login passed
     * @param painterStatus tells if painter or not
     * @param username
     * @param message message to show in chattpanel after log in
     * @param serverName servername
     */
    public void loginHandler(String loginStatus, String painterStatus, String username, String message, String serverName){
        if(loginStatus.equalsIgnoreCase("OK")){
            login(username);
        }else{
            logInPanel.setErrorMessage(loginStatus);
        }
        if (painterStatus.equalsIgnoreCase("true")) {
            gamePanel.setPainter(true);
            chattPanel.setSecretWordMessage(true);
            chattPanel.displayMessage(serverName.toUpperCase() + ": " + message);
        }
    }

    /**
     * Handles chattmessages from server.
     * @param username name of the user that wrote the mssage
     * @param message
     */
    public void showMessageHandler(String username, String message){
        if(message.trim().contains("logged out. The new painter")){
            gamePanel.clearPaper();
        }
        chattPanel.displayMessage(username.toUpperCase() + ": " + message);
    }

    /**
     * Clear paper and inform about the winner of the game.
     * @param username name of the winner
     * @param message
     */
    public void showWinnerMessageHandler(String username, String message){
        gamePanel.clearPaper();
        showMessageHandler(username, message);
    }

    /**
     * Update highscore panel.
     * @param scores
     */
    public synchronized void updateScoreHandler(ArrayList<Score> scores){
        highScorePanel.fillHighscoreField(scores);
    }

    /**
     * Add point to gamePanel
     * @param point
     */
    public synchronized void addPointHandler(Point point){
        gamePanel.addPoint(point);
    }

    /**
     * Define if server waits for the secret word
     * @param secretWord
     */
    public void setSecretWordHandler(boolean secretWord){
        chattPanel.setSecretWordMessage(secretWord);
    }

    /**
     * Define if painter or not
     * @param painter
     */
    public void setPainterHandler(boolean painter){
        gamePanel.setPainter(painter);
    }
}