package game.gamePanel;

import game.serverConnection.ServerPrint;

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
    private ServerPrint serverPrint;

    public Paper(ServerPrint serverPrint){
        this.serverPrint = serverPrint;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(painter) {
                    drawPoint(e.getPoint());
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e) {
                if(painter) {
                    drawPoint(e.getPoint());
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        Iterator i = points.iterator();
        while(i.hasNext()) {
            Point p = (Point)i.next();
            g.fillOval(p.x, p.y, 5, 5);
        }
    }

    private void drawPoint(Point p){
        points.add(p);
        sendPoint(p);
        repaint();
    }

    public void addPoint(Point p){
        points.add(p);
        repaint();
    }

    public void setPainter(boolean painter){
        this.painter = painter;
    }

    public void clearPaper(){
        points.clear();
        repaint();
    }

    private void sendPoint(Point point){
        String pointToSend = Integer.toString(point.x) + ", " + Integer.toString(point.y);
        serverPrint.writeToServer("draw:" + pointToSend);
    }
}
