package game.gamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Iterator;

public class Paper extends JPanel {

    private HashSet points = new HashSet();
    private boolean painter = false;
    private GamePanel gamePanel;

    public Paper(GamePanel gamePanel){
        this.gamePanel = gamePanel;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(painter) {
                    sendPoint(e.getPoint());
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e) {
                if(painter) {
                    sendPoint(e.getPoint());
                }
            }
        });
    }

    /**
     * Set the current drawing to the received drawing.
     *
     * @param points drawing
     */
    public void setDrawing(HashSet<Point> points){
        this.points = points;
    }

    /**
     * Paint all points stored in the HashSet points
     *
     * @param g
     */
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        Iterator i = points.iterator();
        while(i.hasNext()) {
            Point p = (Point)i.next();
            g.fillOval(p.x, p.y, 5, 5);
        }
    }

    /**
     * Add point to HashSet points and repaint drawing
     *
     * @param p
     */
    public synchronized void addPoint(Point p){
        points.add(p);
        repaint();
    }

    /**
     * @param painter painter status
     */
    public void setPainter(boolean painter){
        this.painter = painter;
    }

    /**
     * Remove current drawing and clear the HashSet points.
     */
    public synchronized void clearPaper(){
        points.clear();
        repaint();
    }

    /**
     * Converts point to String and sends it to server.
     *
     * @param point point to be send to server
     */
    private void sendPoint(Point point){
        String pointToSend = Integer.toString(point.x) + ", " + Integer.toString(point.y);
        gamePanel.sendPoint("draw:" + pointToSend);
    }
}
