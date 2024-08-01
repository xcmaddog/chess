package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              authtoken VARCHAR(255) NOT NULL,
              username VARCHAR(255) NOT NULL,
              PRIMARY KEY (authtoken)
            )"""
    };

    @BeforeEach
    void setUp () throws DataAccessException {
        DatabaseManager.createDatabase(); //make the database
        try (var conn = DatabaseManager.getConnection()) { // get a connection
            try {
                var cleanStatement = conn.prepareStatement("DROP TABLE IF EXISTS auth"); //delete any previous table
                cleanStatement.executeUpdate();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) { // make the user table
                    preparedStatement.executeUpdate();
                }
            }
            var addUserStatement = conn.prepareStatement(
                    "INSERT INTO auth (authtoken, username)" +
                            " VALUES('thisisanauthtoken123', 'firstUser')");//put in a user
            addUserStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        //close the connection
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        SQLAuthDAO theDAO = new SQLAuthDAO();

        AuthData newAuth = new AuthData("HeReIsAnOtHeR", "secondUser");
        theDAO.createAuth(newAuth);

        AuthData result = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authtoken='HeReIsAnOtHeR'";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    result = theDAO.readAuth(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }

        assertEquals(newAuth, result);
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        SQLAuthDAO theDAO = new SQLAuthDAO();

        AuthData previousUser = new AuthData("BlahBlahBlah", "firstUser");

        assertThrows(Exception.class, () -> theDAO.createAuth(previousUser));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        SQLAuthDAO theDAO = new SQLAuthDAO();

        AuthData expected = new AuthData("thisisanauthtoken123","firstUser");
        AuthData result = theDAO.getAuth("thisisanauthtoken123");
        assertEquals(expected, result);
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        SQLAuthDAO theDAO = new SQLAuthDAO();

        AuthData result = theDAO.getAuth("nonexistentAuth");
        assertNull(result);
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        SQLAuthDAO theDAO = new SQLAuthDAO();

        theDAO.deleteAuth("thisisanauthtoken123");

        assertNull(theDAO.getAuth("thisisanauthtoken123"));
    }


    @Test
    void clear() throws DataAccessException {
        SQLAuthDAO theDAO = new SQLAuthDAO();

        theDAO.clear();
        assertTrue(theDAO.isEmpty());
    }
}