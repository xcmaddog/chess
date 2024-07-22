package dataAccess;

import model.GameData;
import model.GameInfo;

import java.util.HashSet;

public interface GameDAO {
    public void createGame (GameData gameData);
    public GameData getGame(int gameID);
    public HashSet<GameData> listGames();
    public HashSet<GameInfo> listGameInfo();
    public void updateGame(GameData gameData);
    public void clear();
}
