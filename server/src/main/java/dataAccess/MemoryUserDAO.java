package dataAccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    HashMap<String,UserData> Users;

    public MemoryUserDAO (){}

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
}
