package request;

import java.util.Objects;

public class RegisterRequest {

    private final String username;
    private final String password;
    private final String email;

    public RegisterRequest (String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "Username='" + username + '\'' +
                ", Password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
