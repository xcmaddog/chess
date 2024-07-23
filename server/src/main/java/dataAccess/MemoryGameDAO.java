package dataAccess;

import model.GameData;
import model.GameInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{

    HashMap<Integer, GameData> Games;

    public MemoryGameDAO(){
        Games = new HashMap<Integer, GameData>();
    }

    public void createGame (GameData gameData){
        int gameID = gameData.getGameID();
        Games.put(gameID,gameData);
    }
    public GameData getGame(int gameID){
        return Games.get(gameID);
    }
    public Collection<GameData> listGames(){
        return Games.values();
    }
    public Collection<GameInfo> listGameInfo(){
        Collection<GameData> allGameData = this.listGames();
        Collection<GameInfo> allGameInfo = new HashSet<GameInfo>();
        for(GameData g: allGameData){
            allGameInfo.add(new GameInfo(g));
        }
        return allGameInfo;
    }
    public void updateGame(GameData gameData){
        //I will probably need to catch a case where there is not a matching gameData to update
        int gameID = gameData.getGameID();
        Games.remove(gameID);
        Games.put(gameID, gameData);
    }
    public void clear(){
        Games.clear();
    }

    @Override
    public boolean isEmpty() {
        return Games.isEmpty();
    }
}
