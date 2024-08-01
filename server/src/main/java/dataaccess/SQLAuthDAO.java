package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO extends SQLDataBase implements AuthDAO{

    public SQLAuthDAO () throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auth (authtoken, username) VALUES(?,?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    return readAuth(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException (String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException (String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT authtoken FROM auth";
            try (var ps = conn.prepareStatement(statement)){
                try (var rs = ps.executeQuery()){
                    return !rs.next();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public AuthData readAuth(ResultSet rs) throws SQLException {
        if (rs.next()){
            String authToken = rs.getString("authtoken");
            String username = rs.getString("username");
            AuthData authData = new AuthData(authToken, username);
            return authData;
        } else {
            return null;
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              authtoken VARCHAR(255) NOT NULL,
              username VARCHAR(255) NOT NULL,
              PRIMARY KEY (authtoken)
            )"""
    };

}
