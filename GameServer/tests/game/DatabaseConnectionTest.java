package game;

import game.database.DatabaseConnection;
import game.database.Score;
import org.junit.Test;


import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class DatabaseConnectionTest {

    private DatabaseConnection dbCon = new DatabaseConnection();

    @Test
    public void testCreateUser(){
        dbCon.createUser("Test", "t");
    }

    @Test
    public void testInsertHighscoreExistingUser(){
        assertEquals("OK", dbCon.insertScore(12, 1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInsertHighScoreUserNotExisting(){
        dbCon.insertScore(10, 5);
    }

    @Test
    public void testGetScore(){
        ArrayList<Score> scores = dbCon.getHighscore();
        for(int i = 0; i < scores.size(); i++){
            System.out.println(scores.get(i).getName() + " " + scores.get(i).getScore());
        }
    }

    @Test
    public void testLogIn(){
        assertEquals(1, dbCon.login("Maja", "aa"));
    }

    @Test
    public void testIncreaseScore(){
//        dbCon.updateScore(1);
    }
}
