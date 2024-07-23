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

import java.util.Objects;
import java.util.UUID;

public class UserService extends Service{

    public UserService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        super(userDAO, gameDAO, authDAO);
    }

    public UserService (UserDAO userDAO,AuthDAO authDAO) {
        this.setUserDAO(userDAO);
        this.setAuthDAO(authDAO);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws dataaccess.DataAccessException {
        String username = registerRequest.getUsername();
        if (super.userDAO.getUser(username) == null){
            UserData userData = new UserData(username, registerRequest.getPassword(), registerRequest.getEmail());
            userDAO.createUser(userData);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken,username);
            authDAO.createAuth(authData);
            RegisterResult registerResult = new RegisterResult(username, authToken);
            return registerResult;
        } else {
            throw new dataaccess.DataAccessException("A user with that username already exists");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws dataaccess.DataAccessException{
        String username = loginRequest.getUsername();
        UserData userData = userDAO.getUser(username);
        if (userData != null){
            if (Objects.equals(userData.password(), loginRequest.getPassword())){
                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(authToken,username);
                authDAO.createAuth(authData);
                LoginResult loginResult = new LoginResult(username,authToken);
                return loginResult;
            }else {
                throw new dataaccess.DataAccessException("The password provided was incorrect");
            }
        } else {
            throw new dataaccess.DataAccessException("The username provided is invalid");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws dataaccess.DataAccessException {
        String authToken = logoutRequest.getAuthToken();
        if (isAuthorized(authToken)){
            //log them out
            authDAO.deleteAuth(authToken);
            LogoutResult logoutResult = new LogoutResult();
            return logoutResult;
        } else {
            throw new dataaccess.DataAccessException("The authToken was not recognized");
        }
    }
}
