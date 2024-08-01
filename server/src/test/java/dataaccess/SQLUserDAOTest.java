package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              username VARCHAR(255) NOT NULL,
              password VARCHAR(255) NOT NULL,
              email VARCHAR(255) NOT NULL,
              PRIMARY KEY (username)
            )"""
    };

    @BeforeEach
    void setUp () throws DataAccessException {
        DatabaseManager.createDatabase(); //make the database
        try (var conn = DatabaseManager.getConnection()) { // get a connection
            try {
                var cleanStatement = conn.prepareStatement("DROP TABLE user"); //delete any previous table
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
                    "INSERT INTO user (username, password, email)" +
                            " VALUES('firstUser', 'firstPassword', 'badEmail')");//put in a user
            addUserStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        //close the connection
    }

    @Test
    void createUserPositive() throws DataAccessException {
        SQLUserDAO theDAO = new SQLUserDAO();

        UserData newUser = new UserData("secondUser", "secondPass", "just@email.com");
        theDAO.createUser(newUser);

        UserData result = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM user WHERE username='secondUser'";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    result = theDAO.readUser(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }

        assertEquals(newUser, result);
    }

    @Test
    void createUserNegative() throws DataAccessException {
        SQLUserDAO theDAO = new SQLUserDAO();

        UserData previousUser = new UserData("firstUser", "thisDoesn'tMatter", "my@email.com");

        assertThrows(Exception.class, () -> theDAO.createUser(previousUser));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        SQLUserDAO theDAO = new SQLUserDAO();

        UserData expected = new UserData("firstUser", "firstPassword",
                "badEmail");
        UserData result = theDAO.getUser("firstUser");
        assertEquals(expected, result);
    }

    @Test
    void getUserNegative() throws DataAccessException {
        SQLUserDAO theDAO = new SQLUserDAO();

        UserData result = theDAO.getUser("nonexistentUser");
        assertNull(result);
    }

    @Test
    void clear() throws DataAccessException {
        SQLUserDAO theDAO = new SQLUserDAO();

        theDAO.clear();
        assertTrue(theDAO.isEmpty());
    }
}