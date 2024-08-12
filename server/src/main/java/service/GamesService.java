package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import mydataaccess.DataAccessException;
import model.GameData;
import model.GameInfo;
import request.CreateGameRequest;
import request.GetGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.GetGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class GamesService extends Service{

    private int nextGameID;

    public GamesService (UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) throws DataAccessException{
        super(userDAO, gameDAO, authDAO);
        int maxDBID = gameDAO.getMaxGameID();
        this.nextGameID = maxDBID+1;
    }

    public ListGamesResult getGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        if(isAuthorized(listGamesRequest.authToken())){
            Collection<GameInfo> listOfGames =  gameDAO.listGameInfo();
            ListGamesResult listGamesResult = new ListGamesResult((HashSet<GameInfo>) listOfGames);
            return listGamesResult;
        } else{
            throw new DataAccessException("Invalid AuthToken");
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws DataAccessException {
        if (isAuthorized(authToken)){
            GameData gameData = new GameData(nextGameID, createGameRequest.gameName(), new ChessGame());
            gameDAO.createGame(gameData);
            nextGameID++;
            return new CreateGameResult(nextGameID - 1);
        }else{
            throw new DataAccessException("Invalid AuthToken");
        }
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest joinGameRequest) throws DataAccessException {
        if(! isAuthorized(authToken)){
            throw new DataAccessException("Invalid AuthToken");
        }
        String username = authDAO.getAuth(authToken).username();
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null){
            throw new DataAccessException("Game not found");
        }
        ChessGame.TeamColor color = joinGameRequest.playerColor();
        if (color == ChessGame.TeamColor.WHITE) {
            if ((gameData.getWhiteUsername() != null) && (!Objects.equals(gameData.getWhiteUsername(), username))) {
                throw new DataAccessException("There is already someone else playing white in this game");
            }
            GameData newData = new GameData(gameData.getGameID(), gameData.getGameName(), gameData.getGame(), username,
                    gameData.getBlackUsername());
            gameDAO.updateGame(newData);
            JoinGameResult joinGameResult = new JoinGameResult(newData);
            return joinGameResult;
        } else {
            if ((gameData.getBlackUsername() != null) && (!Objects.equals(gameData.getBlackUsername(), username))) {
                throw new DataAccessException("There is already someone playing black in this game");
            }
            GameData newData = new GameData(gameData.getGameID(), gameData.getGameName(), gameData.getGame(),
                    gameData.getWhiteUsername(), username);
            gameDAO.updateGame(newData);//theoretically could make this more efficient by not using DAO if existing player
            JoinGameResult joinGameResult = new JoinGameResult(newData);
            return joinGameResult;
        }

    }

    public GetGameResult getGame(String authToken, GetGameRequest getGameRequest) throws DataAccessException {
        if(! isAuthorized(authToken)){
            throw new DataAccessException("Invalid AuthToken");
        }
        GameData gameData = gameDAO.getGame(getGameRequest.gameID());
        GetGameResult getGameResult = new GetGameResult(gameData);
        return getGameResult;
    }
}
