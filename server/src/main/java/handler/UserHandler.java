package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request.RegisterRequest;
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

    public String handleRegistration (String jsonFromServer){
        //make the Gson object
        var serializer = new Gson();

        //turn the JSON fom the server into a RegisterRequest object
        RegisterRequest registerRequest =  serializer.fromJson(jsonFromServer, RegisterRequest.class);
        //make a UserService object
        UserService userService = new UserService(userDAO, gameDAO, authDAO);
        //get a RegisterResult object from the UserService
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            //turn the RegisterResult object into a JSON string
            String jsonToReturn = serializer.toJson(registerResult);
            return jsonToReturn;
        } catch (dataaccess.DataAccessException dataAccessException){
            //need to change this to return the right kind of thing
            return null;
        }
    }
}
