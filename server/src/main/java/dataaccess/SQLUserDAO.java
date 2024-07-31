package dataaccess;

import model.UserData;
import java.sql.DriverManager;

public class SQLUserDAO implements UserDAO{
    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
