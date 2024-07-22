package result;

import model.GameInfo;

import java.util.HashSet;
import java.util.Objects;

public class ListGamesResult {

    private final HashSet<GameInfo> allGameInfo;

    public ListGamesResult(HashSet<GameInfo> allGameInfo){
        this.allGameInfo = allGameInfo;
    }

    public HashSet<GameInfo> getAllGameInfo() {
        return allGameInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListGamesResult that = (ListGamesResult) o;
        return Objects.equals(allGameInfo, that.allGameInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allGameInfo);
    }
}
