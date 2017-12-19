package game.highscore;

import game.database.Score;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HighScorePanel extends JPanel{

    private JTextArea highscoreField;

    public HighScorePanel(){
        setPreferredSize(new Dimension(100, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel heading = new JLabel("Top Ten");
        heading.setFont(new Font("Arial", Font.PLAIN, 15));
        add(heading);

        highscoreField = new JTextArea();
        highscoreField.setEditable(false);
        JScrollPane scroll = new JScrollPane(highscoreField);
        add(scroll);
    }

    public void fillHighscoreField(ArrayList<Score> scores){
        highscoreField.setText("");
        for(int i = 1; i < scores.size()+1; i++){
            Score s = scores.get(i-1);
            highscoreField.append(i + ". " + s.getName() + ": " + s.getScore() + "\n");
        }
    }
}
