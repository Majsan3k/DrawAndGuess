package game.database;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseConnection {

    //SQL-queries
    private static final String CREATE_USER = "INSERT INTO player (username, password) VALUES (?, ?)";
    private static final String INSERT_HIGHSCORE = "INSERT INTO highscore (score, userId) VALUES (?, ?)";
    private static final String GET_HIGHSCORE = "SELECT score, userName FROM player JOIN highscore " +
            "ON player.id = highscore.userId ORDER BY score";
    private static final String LOGIN_QUERY = "SELECT password FROM player WHERE username = ?";
    private static final String UPDATE_HIGHSCORE = "UPDATE highscore JOIN player ON player.id = highscore.userId " +
            "SET score = score + 1 WHERE userName = ?";

    private PreparedStatement prepStmt = null;
    private Statement stmt = null;
    private Connection dbCon = null;

    /**
     * Encrypt password.
     * @param password_plaintext password
     * @return the encrypted password
     */
    private static String hashPassword(String password_plaintext) {
        return BCrypt.hashpw(password_plaintext, BCrypt.gensalt());
    }

    /**
     * Decrypt password and compare with the password in plain text.
     * @param password_plaintext password to be checked
     * @param stored_hash the stored encrypted password
     * @return true if the passwords matches, otherwise false
     */
    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        if(stored_hash == null || !stored_hash.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }
        return BCrypt.checkpw(password_plaintext, stored_hash);
    }

    /**
     * Connect to the mysql database
     */
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

    /**
     * Close statements and connections.
     */
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

    /**
     * Insert username and encrypted password to database.
     * @param userName new username
     * @param password new password
     * @return status about if the registration was approved or not
     */
    public String createUser(String userName, String password){
        connectToDb();
        int userId = 0;

        try {
            prepStmt = dbCon.prepareStatement(CREATE_USER);
            prepStmt.setString(1, userName);
            prepStmt.setString(2, hashPassword(password));
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

    /**
     * Asks database for the user's password and compares it with the given password.
     * @param username user to log in
     * @param password given password
     * @return true if password and username matches, else false
     */
    public boolean login(String username, String password) {
        connectToDb();

        try {
            prepStmt = dbCon.prepareStatement(LOGIN_QUERY);
            prepStmt.setString(1, username);
            prepStmt.execute();
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                if (checkPassword(password, rs.getString("password"))) {
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

    /**
     * Insert new score in database.
     * @param score the score to be inserted
     * @param userId userId that have the score
     * @return status of the insertion
     */
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

    /**
     * Update score for the given user.
     * @param username the user whose score should be updated
     */
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

    /**
     * Get highscores from database
     * @return all highscores from database
     */
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
