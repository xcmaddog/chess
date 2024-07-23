package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import dataaccess.DataAccessException;
import model.GameData;
import model.GameInfo;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.Collection;
import java.util.HashSet;

public class GamesService extends Service{

    private int nextGameID = 1;

    public GamesService (UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        super(userDAO, gameDAO, authDAO);
    }

    public ListGamesResult getGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        if(isAuthorized(listGamesRequest.getAuthToken())){
            Collection<GameInfo> listOfGames =  gameDAO.listGameInfo();
            ListGamesResult listGamesResult = new ListGamesResult((HashSet<GameInfo>) listOfGames);
            return listGamesResult;
        } else{
            throw new DataAccessException("Invalid AuthToken");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (isAuthorized(createGameRequest.getAuthToken())){
            GameData gameData = new GameData(nextGameID, createGameRequest.getGameName(), new ChessGame());
            gameDAO.createGame(gameData);
            nextGameID++;
            return new CreateGameResult(nextGameID - 1);
        }else{
            throw new DataAccessException("Invalid AuthToken");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        if(! isAuthorized(joinGameRequest.getAuthToken())){
            throw new DataAccessException("Invalid AuthToken");
        }
        GameData gameData = gameDAO.getGame(joinGameRequest.getGameID());
        if (gameData == null){
            throw new DataAccessException("Game not found");
        }
        ChessGame.TeamColor color = joinGameRequest.getPlayerColor();
        if(color == ChessGame.TeamColor.WHITE){
            if(gameData.getWhiteUsername() != null){
                throw new DataAccessException("There is already someone playing white in this game");
            }
            String username = authDAO.getAuth(joinGameRequest.getAuthToken()).username();
            GameData newData = new GameData(gameData.getGameID(), gameData.getGameName(),gameData.getGame(), username, gameData.getBlackUsername());
            gameDAO.updateGame(newData);
            JoinGameResult joinGameResult = new JoinGameResult();
            return joinGameResult;
        } else {
            if (gameData.getBlackUsername() != null){
                throw new DataAccessException("There is already someone playing black in this game");
            }
            String username = authDAO.getAuth(joinGameRequest.getAuthToken()).username();
            GameData newData = new GameData(gameData.getGameID(), gameData.getGameName(),gameData.getGame(), gameData.getWhiteUsername(), username);
            gameDAO.updateGame(newData);
            JoinGameResult joinGameResult = new JoinGameResult();
        }
        throw new DataAccessException("Sorry, something went wrong when trying to join the game");
    }
}