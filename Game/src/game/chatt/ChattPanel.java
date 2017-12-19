package game.chatt;

import game.mainFrame.GameFrame;
import game.serverConnection.ServerPrint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChattPanel extends JPanel{

    private JTextField writeMessage;
    private JTextArea messageView;
    private GameFrame gameFrame;

    public ChattPanel(GameFrame gameFrame, ServerPrint serverPrint){

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
                    serverPrint.writeToServer("message:" + writeMessage.getText());
                    writeMessage.setText("");
                }
            }
        });

        add((writeMessage), BorderLayout.NORTH);
        messageView = new JTextArea();
        messageView.setEditable(false);
        JScrollPane scroll = new JScrollPane(messageView);
        add(scroll);
    }

    public void displayMessage(String message){
        messageView.append(message + "\n");
    }
}
