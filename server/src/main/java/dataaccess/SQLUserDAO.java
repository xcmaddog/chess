package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import java.sql.DriverManager;
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
            var statement = "SELECT id, json FROM pet WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException (String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String json = rs.getString("json");
        UserData userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(255) NOT NULL,
              `password` varchar(255) NOT NULL,
              'email' varchar(255) NOT NULL,
              'json' TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
            )"""
    };

}
