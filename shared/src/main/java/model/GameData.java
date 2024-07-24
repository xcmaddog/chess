package model;

import chess.ChessGame;

import java.util.Objects;

public class GameData {
    private int gameID;
    private String gameName;
    private ChessGame game;
    private String whiteUsername;
    private String blackUsername;

    public GameData(int gameID, String gameName, ChessGame game){
        setGameID(gameID);
        setGameName(gameName);
        setGame(game);
    }

    public GameData(int gameID, String gameName, ChessGame game, String whiteUsername, String blackUsername){
        setGameID(gameID);
        setGameName(gameName);
        setGame(game);
        setWhiteUsername(whiteUsername);
        setBlackUsername(blackUsername);
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID && Objects.equals(gameName, gameData.gameName)
                && Objects.equals(game, gameData.game) && Objects.equals(whiteUsername, gameData.whiteUsername)
                && Objects.equals(blackUsername, gameData.blackUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, gameName, game, whiteUsername, blackUsername);
    }
}
