package request;

import java.util.Objects;

public class RegisterRequest {

    private final String Username;
    private final String Password;
    private final String Email;

    public RegisterRequest (String username, String password, String email){
        this.Username = username;
        this.Password = password;
        this.Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(Username, that.Username) && Objects.equals(Password, that.Password) && Objects.equals(Email, that.Email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Username, Password, Email);
    }
}
