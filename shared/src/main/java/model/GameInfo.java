package model;

import java.util.Objects;

public class GameInfo {

    private int gameID;
    private String gameName;
    private String whiteUsername;
    private String blackUsername;

    public GameInfo (GameData gameData){
        setGameID(gameData.getGameID());
        setGameName(gameData.getGameName());
        setWhiteUsername(gameData.getWhiteUsername());
        setBlackUsername(gameData.getBlackUsername());
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInfo gameInfo = (GameInfo) o;
        return gameID == gameInfo.gameID && Objects.equals(gameName, gameInfo.gameName) && Objects.equals(whiteUsername, gameInfo.whiteUsername) && Objects.equals(blackUsername, gameInfo.blackUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, gameName, whiteUsername, blackUsername);
    }
}
