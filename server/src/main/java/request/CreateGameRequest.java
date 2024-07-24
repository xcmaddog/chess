package request;

import java.util.Objects;

public class CreateGameRequest {

    //private final String authToken;
    private final String gameName;

    public CreateGameRequest (String gameName){
        //this.authToken = authToken;
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    //public String getAuthToken() {
    //    return authToken;
    //}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameRequest that = (CreateGameRequest) o;
        return Objects.equals(gameName, that.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName);
    }
}
