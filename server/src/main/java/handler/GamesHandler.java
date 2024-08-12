package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import mydataaccess.DataAccessException;
import request.CreateGameRequest;
import request.GetGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.GetGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import service.GamesService;

public class GamesHandler {

    private final Gson serializer;
    private final GamesService gamesService;

    public GamesHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) throws DataAccessException {
        this.serializer = new Gson();
        this.gamesService = new GamesService(userDAO,gameDAO,authDAO);
    }

    public String handleCreateGame(String authToken, String jsonFromServer) throws DataAccessException {
        CreateGameRequest createGameRequest = serializer.fromJson(jsonFromServer,CreateGameRequest.class);
        if (createGameRequest.gameName() == null){
            throw new DataAccessException("Invalid request");
        }
        CreateGameResult createGameResult = gamesService.createGame(authToken, createGameRequest);
        String jsonToReturn = serializer.toJson(createGameResult);
        return jsonToReturn;
    }

    public String handleJoinGame(String authToken, String jsonFromServer) throws DataAccessException {
        JoinGameRequest joinGameRequest = serializer.fromJson(jsonFromServer,JoinGameRequest.class);
        if (joinGameRequest.playerColor() == null || joinGameRequest.gameID() < 1){
            throw new DataAccessException("Invalid request");
        }
        JoinGameResult joinGameResult = gamesService.joinGame(authToken,joinGameRequest);
        String jsonToReturn = serializer.toJson(joinGameResult);
        return jsonToReturn;
    }

    public String handleListGames(String authToken) throws DataAccessException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gamesService.getGames(listGamesRequest);
        String jsonToReturn = serializer.toJson(listGamesResult);
        return jsonToReturn;
    }

    public String getGame(String authToken, String paramsFromHeader) throws DataAccessException {
        GetGameRequest getGameRequest = new GetGameRequest(Integer.parseInt(paramsFromHeader));
        GetGameResult getGameResult = gamesService.getGame(authToken, getGameRequest);
        String jsonToReturn = serializer.toJson(getGameResult);
        return jsonToReturn;
    }
}
