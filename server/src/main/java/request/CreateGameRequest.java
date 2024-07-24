package request;

import java.util.Objects;

/**
 * @param gameName private final String authToken;
 */
public record CreateGameRequest(String gameName) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameRequest that = (CreateGameRequest) o;
        return Objects.equals(gameName, that.gameName);
    }
}
