package dataAccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final HashMap<String,UserData> Users;

    public MemoryUserDAO (){
        Users = new HashMap<>();
    }

    public void createUser(UserData userData){
        String username = userData.username();
        Users.put(username,userData);
    }
    public UserData getUser(String username){
        return Users.get(username);
    }
    public void clear(){
        Users.clear();
    }

    @Override
    public boolean isEmpty() {
        return Users.isEmpty();
    }
}
