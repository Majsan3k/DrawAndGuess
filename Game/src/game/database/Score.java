package game.database;

import java.io.Serializable;

public class Score implements Serializable {

    private static final long serialVersionUID = 42L;
    private int score;
    private String name;

    public Score(int score, String name){
        this.score = score;
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return name + " " + score;
    }
}
