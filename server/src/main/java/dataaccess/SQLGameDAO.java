package dataaccess;

import model.GameData;
import model.GameInfo;
import java.util.Collection;
import java.util.List;
import java.sql.DriverManager;

public class SQLGameDAO implements GameDAO{

    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public Collection<GameInfo> listGameInfo() {
        return List.of();
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
