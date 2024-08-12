package dataaccess;

import model.GameData;
import model.GameInfo;
import mydataaccess.DataAccessException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MemoryGameDAO implements GameDAO{

    final HashMap<Integer, GameData> games;

    public MemoryGameDAO(){
        games = new HashMap<>();
    }

    public void createGame (GameData gameData){
        int gameID = gameData.getGameID();
        games.put(gameID,gameData);
    }
    public GameData getGame(int gameID){
        return games.get(gameID);
    }
    public Collection<GameData> listGames(){
        return games.values();
    }
    public Collection<GameInfo> listGameInfo(){
        Collection<GameData> allGameData = this.listGames();
        Collection<GameInfo> allGameInfo = new HashSet<>();
        for(GameData g: allGameData){
            allGameInfo.add(new GameInfo(g));
        }
        return allGameInfo;
    }
    public void updateGame(GameData gameData){
        //I will probably need to catch a case where there is not a matching gameData to update
        int gameID = gameData.getGameID();
        games.remove(gameID);
        games.put(gameID, gameData);
    }
    public void clear(){
        games.clear();
    }

    @Override
    public boolean isEmpty() {
        return games.isEmpty();
    }

    @Override
    public int getMaxGameID() throws DataAccessException {
        Set<Integer> gameIDs = games.keySet();
        int maxID = 0;
        for(int id : gameIDs){
            if (id > maxID){
                maxID = id;
            }
        }
        return maxID;
    }
}
