package game.highscore;

import game.database.Score;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HighScorePanel extends JPanel{

    private JTextArea highscoreField;

    public HighScorePanel(){
        setPreferredSize(new Dimension(140, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel heading = new JLabel("<html><u>Top Ten</u></html>");
        heading.setFont(new Font("Arial", Font.PLAIN, 25));

        highscoreField = new JTextArea();
        highscoreField.setEditable(false);
        highscoreField.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(highscoreField);
        scroll.setBorder(null);

        add(heading);
        add(Box.createVerticalStrut(30));
        add(scroll);
    }

    /**
     * Fills the highscore table with the 10 best players.
     * @param scores highscore from database
     */
    public void fillHighscoreField(ArrayList<Score> scores){
        highscoreField.setText("");
        for(int i = 1; i < 11; i++){
            if(i <= scores.size()) {
                Score s = scores.get(i - 1);
                highscoreField.append(i + ". " + s.getName() + ": " + s.getScore() + "\n\n");
            }else{
                highscoreField.append(i + ".\n\n");
            }
        }
    }
}
