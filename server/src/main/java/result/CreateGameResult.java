package result;

import java.util.Objects;

public class CreateGameResult {

    private final int gameID;

    public CreateGameResult(int gameID){
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameResult that = (CreateGameResult) o;
        return gameID == that.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameID);
    }
}
