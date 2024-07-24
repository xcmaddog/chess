package result;

import model.GameInfo;

import java.util.HashSet;
import java.util.Objects;

public class ListGamesResult {

    private final HashSet<GameInfo> games;

    public ListGamesResult(HashSet<GameInfo> allGameInfo){
        this.games = allGameInfo;
    }

    public HashSet<GameInfo> getAllGameInfo() {
        return games;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListGamesResult that = (ListGamesResult) o;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(games);
    }
}
