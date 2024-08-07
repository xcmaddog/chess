package result;

public record CreateGameResult(int gameID) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateGameResult that = (CreateGameResult) o;
        return gameID == that.gameID;
    }

}
