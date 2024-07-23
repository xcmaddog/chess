package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request.ClearRequest;
import result.ClearResult;
import service.ClearService;


public class ClearHandler {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public ClearHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public String handleClearAll(){
        var serializer = new Gson();

        ClearService clearService = new ClearService(userDAO,gameDAO,authDAO);
        ClearResult clearResult = clearService.clearAll(new ClearRequest());
        String result = serializer.toJson(clearResult);
        return result;
    }

}