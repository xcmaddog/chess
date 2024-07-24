package request;

import chess.ChessGame;

import java.util.Objects;

public class JoinGameRequest {

    //private final String authToken;
    private final ChessGame.TeamColor playerColor;
    private final int gameID;

    public JoinGameRequest(ChessGame.TeamColor playerColor, int gameID){
        //this.authToken = authToken;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    //public String getAuthToken() {
    //    return authToken;
    //}

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinGameRequest that = (JoinGameRequest) o;
        return gameID == that.gameID && Objects.equals(playerColor, that.playerColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerColor, gameID);
    }
}
