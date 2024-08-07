package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import mydataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.ClearRequest;
import result.ClearResult;
import service.ClearService;


public class ClearHandler {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public String handleClearAll() throws DataAccessException {

        //System.out.println("You made it to the handleClearAll function");

        var serializer = new Gson();

        ClearService clearService = new ClearService(userDAO,gameDAO,authDAO);
        ClearResult clearResult = clearService.clearAll(new ClearRequest());
        String result = serializer.toJson(clearResult);
        return result;
    }

}
