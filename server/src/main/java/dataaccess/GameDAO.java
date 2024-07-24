package dataaccess;

import model.GameData;
import model.GameInfo;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    Collection<GameInfo> listGameInfo();
    void updateGame(GameData gameData);
    void clear();
    boolean isEmpty();
}
