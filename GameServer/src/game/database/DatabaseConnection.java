package game.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

//TODO: Ska metoderna vara privata? Och ha en typ switch-metod som anropas istället?
public class DatabaseConnection {

    //SQL-queries
    private static final String CREATE_USER = "INSERT INTO player (username, password) VALUES (?, ?)";
    private static final String INSERT_HIGHSCORE = "INSERT INTO highscore (score, userId) VALUES (?, ?)";
    private static final String GET_HIGHSCORE = "SELECT score, userName FROM player JOIN highscore " +
            "ON player.id = highscore.userId ORDER BY score";
    private static final String LOGIN_QUERY = "SELECT password, id FROM player WHERE BINARY username = ?";
    private static final String UPDATE_HIGHSCORE = "UPDATE highscore JOIN player ON player.id = highscore.userId " +
            "SET score = score + 1 WHERE userName = ?";

    private PreparedStatement prepStmt = null;
    private Statement stmt = null;
    private Connection dbCon = null;

    private void connectToDb(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://" + "atlas.dsv.su.se" + "/" + "db_17921331";
        String userName = "usr_17921331";
        String password = "921331";
        try {
            dbCon = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            System.out.println("SQL exception connect: " + e.getMessage());
        }
    }

    private void close(){
        try {
            if (prepStmt != null) {
                prepStmt.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(dbCon != null) {
                dbCon.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String createUser(String userName, String password) {
        connectToDb();
        int userId = 0;

        try {
            prepStmt = dbCon.prepareStatement(CREATE_USER);
            prepStmt.setString(1, userName);
            prepStmt.setString(2, password);
            prepStmt.execute();

            ResultSet rs = prepStmt.getGeneratedKeys();
                if(rs.next()){
                    userId = rs.getInt(1);
                }
                insertScore(0, userId);

    } catch (SQLException e) {
            if(e.getMessage().contains("Duplicate entry")){
                return "The username already exists";
            }
            System.out.println(e.getMessage());
            return "Something went wrong";
        } finally {
            close();
        }
        return "OK";
    }

    public boolean login(String username, String password) {
        connectToDb();

        try {
            prepStmt = dbCon.prepareStatement(LOGIN_QUERY);
            prepStmt.setString(1, username);
            prepStmt.execute();
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("password").equals(password)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        return false;
    }

    public String insertScore(int score, int userId){
        connectToDb();
        prepStmt = null;

        try{
            prepStmt = dbCon.prepareStatement(INSERT_HIGHSCORE);
            prepStmt.setInt(1, score);
            prepStmt.setInt(2, userId);
            prepStmt.execute();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }finally {
            close();
        }
        return "OK";
    }

    public void updateScore(String username){
        connectToDb();
        try{
            prepStmt = dbCon.prepareStatement(UPDATE_HIGHSCORE);
            prepStmt.setString(1, username);
            prepStmt.execute();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            close();
        }
    }

    public ArrayList<Score> getHighscore(){
        connectToDb();
        ArrayList<Score> scores = new ArrayList<>();

        try{
            stmt = dbCon.createStatement();
            ResultSet rs = stmt.executeQuery(GET_HIGHSCORE);

            while(rs.next()){
                int score = rs.getInt("score");
                String userName = rs.getString("userName");
                scores.add(new Score(score, userName));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            close();
        }
        return scores;
    }
}
