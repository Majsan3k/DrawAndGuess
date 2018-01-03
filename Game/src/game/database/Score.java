package game.database;

import java.io.Serializable;

public class Score implements Serializable, Comparable {

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

    @Override
    public int compareTo(Object other){
        return ((Score)other).getScore() - score;
    }

    public String toString(){
        return name + " " + score;
    }
}
