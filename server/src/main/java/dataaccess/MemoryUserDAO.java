package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final HashMap<String,UserData> users;

    public MemoryUserDAO (){
        users = new HashMap<>();
    }

    public void createUser(UserData userData){
        String username = userData.username();
        users.put(username,userData);
    }
    public UserData getUser(String username){
        return users.get(username);
    }
    public void clear(){
        users.clear();
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }
}
