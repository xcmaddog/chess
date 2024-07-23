package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

public class UserHandler {

    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public UserHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public String handleRegistration (String jsonFromServer) throws dataaccess.DataAccessException {
        //make the Gson object
        var serializer = new Gson();

        //turn the JSON fom the server into a RegisterRequest object
        RegisterRequest registerRequest =  serializer.fromJson(jsonFromServer, RegisterRequest.class);

        //System.out.println("You are in the UserHandler handleRegistration method and just created this:");
        //System.out.println(registerRequest.toString());

        if(registerRequest.getUsername() == null){
            throw new dataaccess.DataAccessException("username not provided");
        }
        if(registerRequest.getPassword() == null){
            throw new dataaccess.DataAccessException("password not provided");
        }
        if(registerRequest.getEmail() == null){
            throw new dataaccess.DataAccessException("email not provided");
        }

        //make a UserService object
        UserService userService = new UserService(userDAO, gameDAO, authDAO);
        //get a RegisterResult object from the UserService
        RegisterResult registerResult = userService.register(registerRequest);
        //turn the RegisterResult object into a JSON string
        String jsonToReturn = serializer.toJson(registerResult);
        return jsonToReturn;
    }

    public String handleLogin(String jsonFromServer) throws dataaccess.DataAccessException {
        var serializer = new Gson();
        LoginRequest loginRequest = serializer.fromJson(jsonFromServer, LoginRequest.class);
        UserService userService = new UserService(userDAO, gameDAO, authDAO);
        LoginResult loginResult = userService.login(loginRequest);
        String jsonToReturn = serializer.toJson(loginResult);
        return jsonToReturn;
    }
}
