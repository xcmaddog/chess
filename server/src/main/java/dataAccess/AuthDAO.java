package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData authData);
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);
    public void clear();
    public boolean isEmpty();
}
