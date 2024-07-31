package dataaccess;

import model.AuthData;
import java.sql.DriverManager;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
