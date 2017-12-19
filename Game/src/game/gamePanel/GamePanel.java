package game.gamePanel;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel{

    public GamePanel(Paper paper){
        setPreferredSize(new Dimension(200, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Lets draw!", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.PLAIN, 25));
        add(heading, BorderLayout.PAGE_START);

        add(paper, BorderLayout.CENTER);
    }
}

