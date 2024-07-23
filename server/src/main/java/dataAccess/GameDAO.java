package dataAccess;

import model.GameData;
import model.GameInfo;

import java.util.Collection;

public interface GameDAO {
    public void createGame (GameData gameData);
    public GameData getGame(int gameID);
    public Collection<GameData> listGames();
    public Collection<GameInfo> listGameInfo();
    public void updateGame(GameData gameData);
    public void clear();
    public boolean isEmpty();
}
