package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

   HashMap<String, AuthData> Auths;

    public MemoryAuthDAO (){
        Auths = new HashMap<String, AuthData>();
    }

    public void createAuth(AuthData authData){
        String authToken = authData.authToken();
        Auths.put(authToken, authData);
    }
    public AuthData getAuth(String authToken){
        return Auths.get(authToken);
    }
    public void deleteAuth(String authToken){
        Auths.remove(authToken);
    }
    public void clear(){
        Auths.clear();
    }

    @Override
    public boolean isEmpty() {
        return (Auths.isEmpty());
    }
}
