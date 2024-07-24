package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

   final HashMap<String, AuthData> auths;

    public MemoryAuthDAO (){
        auths = new HashMap<>();
    }

    public void createAuth(AuthData authData){
        String authToken = authData.authToken();
        auths.put(authToken, authData);
    }
    public AuthData getAuth(String authToken){
        return auths.get(authToken);
    }
    public void deleteAuth(String authToken){
        auths.remove(authToken);
    }
    public void clear(){
        auths.clear();
    }

    @Override
    public boolean isEmpty() {
        return (auths.isEmpty());
    }
}
