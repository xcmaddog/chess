package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO extends SQLDataBase implements UserDAO {

    public SQLUserDAO () throws DataAccessException{
        configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        String statement = "INSERT INTO user (username, password, email) VALUES(?,?,?)";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    return readUser(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException (String.format("Unable to read data: %s", e.getMessage()));
        }
        //there used to be a return null here
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT username FROM user";
            try (var ps = conn.prepareStatement(statement)){
                try (var rs = ps.executeQuery()){
                    return !rs.next();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public UserData readUser(ResultSet rs) throws SQLException {
        if (rs.next()){
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            UserData userData = new UserData(username,password,email);
            return userData;
        } else {
            return null;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              username VARCHAR(255) NOT NULL,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(255) NOT NULL,
              PRIMARY KEY (username)
            )"""
    };

}
