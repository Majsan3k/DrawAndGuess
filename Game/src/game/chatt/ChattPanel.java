package game.chatt;

import game.mainFrame.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChattPanel extends JPanel{

    private JTextField writeMessage;
    private JTextArea messageView;
    private GameFrame gameFrame;
    private boolean secretWordMessage;

    public ChattPanel(GameFrame gameFrame){

        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(250, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(new BorderLayout());
        JLabel heading = new JLabel("Chatt", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.PLAIN, 25));
        add(heading);

        writeMessage = new JTextField();

        writeMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(secretWordMessage){
                        secretWordMessage = false;
                        gameFrame.writeToServer("secretword:" + writeMessage.getText());
                    }else {
                        gameFrame.writeToServer("message:" + writeMessage.getText());
                    }
                    writeMessage.setText("");
                }
            }
        });

        add((writeMessage), BorderLayout.NORTH);
        messageView = new JTextArea();
        messageView.setEditable(false);
        messageView.setLineWrap(true);
        messageView.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(messageView);
        add(scroll);
    }

    public void setSecretWordMessage(boolean secretword){
        secretWordMessage = secretword;
    }

    public void displayMessage(String message){
        messageView.append(message + "\n\n");
    }
}
