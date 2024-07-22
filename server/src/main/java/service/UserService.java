package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import model.AuthData;
import model.UserData;
import model.GameData;

public class UserService extends Service{

    public UserService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        super(userDAO, gameDAO, authDAO);
    }

    public UserService (){}

    public RegisterResult register(RegisterRequest registerRequest){
        RegisterResult dummyResult = new RegisterResult("hi","hi");
        return dummyResult;
    }

    public LoginResult login(LoginRequest loginRequest) {
        LoginResult dummyResult = new LoginResult("hi","hi");
        return dummyResult;
    }

    public LogoutResult logout(LogoutRequest logoutRequest){
        LogoutResult dummyResult = new LogoutResult();
        return dummyResult;
    }
}
