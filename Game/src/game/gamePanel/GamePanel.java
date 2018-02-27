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

    /**
     * Send point to server
     * @param message the point to be send
     */
    public synchronized void sendPoint(String message){
        gameFrame.writeToServer(message);
    }

    /**
     * Add point to paper
     * @param point point to be added
     */
    public synchronized void addPoint(Point point){
        paper.addPoint(point);
    }

    /**
     * Set current drawing
     * @param drawing new drawing
     */
    public synchronized void setDrawing(HashSet<Point> drawing){
        paper.setDrawing(drawing);
        paper.repaint();
    }

    /**
     * @param painter information about if user is painter or not
     */
    public void setPainter(boolean painter){
        paper.setPainter(painter);
    }

    /**
     * Clear the drawing
     */
    public synchronized void clearPaper(){
        paper.clearPaper();
    }
}

