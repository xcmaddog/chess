package request;

import java.util.Objects;

public class JoinGameRequest {

    private final String authToken;
    private String playerColor;
    private final int gameID;

    public JoinGameRequest(String authToken, String playerColor, int gameID) throws Exception {
        this.authToken = authToken;
        setPlayerColor(playerColor);
        this.gameID = gameID;
    }

    private void setPlayerColor(String playerColor) throws Exception {
        if (!(Objects.equals(playerColor, "black") || Objects.equals(playerColor, "white"))){
            throw new Exception("player color was not valid");
        } else {
            this.playerColor = playerColor;
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getPlayerColor() {
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
        return gameID == that.gameID && Objects.equals(authToken, that.authToken) && Objects.equals(playerColor, that.playerColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, playerColor, gameID);
    }
}
