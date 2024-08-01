package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
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

    public RegisterResult register(RegisterRequest registerRequest) throws dataaccess.DataAccessException {
        String username = registerRequest.username();
        if (super.userDAO.getUser(username) == null){
            String password = registerRequest.password();
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            UserData userData = new UserData(username, hashedPassword, registerRequest.email());
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
        String username = loginRequest.username();
        UserData userData = userDAO.getUser(username);
        if (userData != null){
            if (BCrypt.checkpw(loginRequest.password(), userData.password())){
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
        String authToken = logoutRequest.authToken();
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
