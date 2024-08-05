package request;

public record RegisterRequest(String username, String password, String email) {

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "Username='" + username + '\'' +
                ", Password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
