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

    public RegisterResult register(RegisterRequest registerRequest){

    }

    public LoginResult login(LoginRequest loginRequest) {

    }

    public LogoutResult logout(LogoutRequest logoutRequest){

    }
}
