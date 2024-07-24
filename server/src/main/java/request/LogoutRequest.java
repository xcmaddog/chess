package request;

import java.util.Objects;

public record LogoutRequest(String authToken) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogoutRequest that = (LogoutRequest) o;
        return Objects.equals(authToken, that.authToken);
    }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "authToken='" + authToken + '\'' +
                '}';
    }
}
