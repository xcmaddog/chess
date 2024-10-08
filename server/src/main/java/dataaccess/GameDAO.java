package dataaccess;

import model.GameData;
import model.GameInfo;
import mydataaccess.DataAccessException;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    Collection<GameInfo> listGameInfo() throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean isEmpty() throws DataAccessException;
    int getMaxGameID() throws DataAccessException;
}
