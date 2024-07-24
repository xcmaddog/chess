package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GamesService;

public class GamesHandler {

    private Gson serializer;
    private GamesService gamesService;

    public GamesHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.serializer = new Gson();
        this.gamesService = new GamesService(userDAO,gameDAO,authDAO);
    }

    public String handleCreateGame(String authToken, String jsonFromServer) throws dataaccess.DataAccessException {
        CreateGameRequest createGameRequest = serializer.fromJson(jsonFromServer,CreateGameRequest.class);
        CreateGameResult createGameResult = gamesService.createGame(authToken, createGameRequest);
        String jsonToReturn = serializer.toJson(createGameResult);
        return jsonToReturn;
    }

}
