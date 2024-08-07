package dataaccess;

import model.AuthData;
import mydataaccess.DataAccessException;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
}
