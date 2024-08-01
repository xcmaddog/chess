package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Test
    void createAuthPositive() {
    }

    @Test
    void createAuthNegative() {
    }

    @Test
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void clear() {
    }

    @Test
    void isEmpty() {
    }
}