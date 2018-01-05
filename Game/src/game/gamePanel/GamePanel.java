package game.gamePanel;

import game.mainFrame.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class GamePanel extends JPanel{

    private GameFrame gameFrame;
    private Paper paper;

    public GamePanel(GameFrame gameFrame){
        this.gameFrame = gameFrame;

        setPreferredSize(new Dimension(200, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Lets draw!", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.PLAIN, 25));
        add(heading, BorderLayout.PAGE_START);

        paper = new Paper(this);
        add(paper, BorderLayout.CENTER);
    }

    public void sendPoint(String message){
        gameFrame.writeToServer(message);
    }

    public void addPoint(Point point){
        paper.addPoint(point);
    }

    public void setDrawing(HashSet<Point> drawing){
        paper.setDrawing(drawing);
        paper.repaint();
    }

    public void setPainter(boolean painter){
        paper.setPainter(painter);
    }

    public void clearPaper(){
        paper.clearPaper();
    }
}

