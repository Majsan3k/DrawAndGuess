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

public class GameFrame extends JFrame{

    private JLabel title;
    private Paper paper;
    private LogInPanel logInPanel;
    private ChattPanel chattPanel;
    private SignUpPanel signUpPanel;
    private HighScorePanel highScorePanel;
    private Socket socket;
    private ServerPrint serverPrint;
    private ServerReader serverReader;

    public GameFrame(Socket socket) throws IOException {
        this.socket = socket;
        this.serverPrint = new ServerPrint(socket);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        title = new JLabel("Welcome", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        title.setPreferredSize(new Dimension(0, 60));
        add(title, BorderLayout.PAGE_START);

        paper = new Paper(serverPrint);
        chattPanel = new ChattPanel(this, serverPrint);
        logInPanel = new LogInPanel(this);
        signUpPanel = new SignUpPanel(this);
        highScorePanel = new HighScorePanel();
        serverReader = new ServerReader(socket, chattPanel, logInPanel, paper, this, signUpPanel);
        Thread thread = new Thread(serverReader);
        thread.start();

        loginMode();

        //TODO: Ta bort, bara för testning
        loginRequest("maja", "aa");

        pack();
        setVisible(true);
    }

    public void loginMode(){
        setTitle("Welcome");
        if(signUpPanel != null){
            remove(signUpPanel);
        }
        add(logInPanel, BorderLayout.CENTER);
        pack();
        repaint();
    }

    public void loginRequest(String username, String password){
        serverPrint.writeToServer("login:" + username + ":" + password);
    }

    public void login(){
        updateScore();
        remove(logInPanel);
        add(highScorePanel, BorderLayout.WEST);
        add(new GamePanel(paper), BorderLayout.CENTER);
        add(chattPanel, BorderLayout.EAST);

        pack();
        repaint();
    }

    public void signUpRequest(String username, String password){
        serverPrint.writeToServer("signUp:" + username + ":" + password);
    }

    public synchronized void updateScore(){
        serverPrint.writeToServer("getHighScore");
        ArrayList<Score> scores = serverReader.getHighScore();
        highScorePanel.fillHighscoreField(scores);
    }

    public void signUp(){
        remove(logInPanel);
        add(signUpPanel);

        pack();
        repaint();
    }

    public void setTitle(String newTitle){
        title.setText(newTitle);
    }
}