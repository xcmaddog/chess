package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import service.UserService;

public class UserHandler {

    private final Gson serializer;
    private final UserService userService;

    public UserHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.serializer = new Gson();
        this.userService = new UserService(userDAO,gameDAO,authDAO);
    }

    public String handleRegistration (String jsonFromServer) throws dataaccess.DataAccessException {
        //make the Gson object

        //turn the JSON fom the server into a RegisterRequest object
        RegisterRequest registerRequest =  serializer.fromJson(jsonFromServer, RegisterRequest.class);

        if(registerRequest.username() == null){
            throw new dataaccess.DataAccessException("username not provided");
        }
        if(registerRequest.password() == null){
            throw new dataaccess.DataAccessException("password not provided");
        }
        if(registerRequest.email() == null){
            throw new dataaccess.DataAccessException("email not provided");
        }
        //get a RegisterResult object from the UserService
        RegisterResult registerResult = userService.register(registerRequest);
        //turn the RegisterResult object into a JSON string
        String jsonToReturn = serializer.toJson(registerResult);
        return jsonToReturn;
    }

    public String handleLogin(String jsonFromServer) throws dataaccess.DataAccessException {
        LoginRequest loginRequest = serializer.fromJson(jsonFromServer, LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        String jsonToReturn = serializer.toJson(loginResult);
        return jsonToReturn;
    }

    public String handleLogout (String authToken) throws dataaccess.DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult logoutResult = userService.logout(logoutRequest);
        String jsonToReturn = serializer.toJson(logoutResult);
        return jsonToReturn;
    }

}
