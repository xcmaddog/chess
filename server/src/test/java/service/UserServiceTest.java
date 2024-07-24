package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private GameData thisGame;
    private GameData oneGame;
    private UserService userService;

    @BeforeEach
    void setup(){
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        UserData firstUser = new UserData("JoeBob", "IcannotThink", "joebob@gmail.com");
        memoryUserDAO.createUser(firstUser);
        userService = new UserService(memoryUserDAO, memoryGameDAO, memoryAuthDAO);
    }

    @Test
    void registerPositive() throws dataaccess.DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("JillSmith", "ConfidentPassword",
                "jill2002@yahoo.com");
        assertTrue(userService.register(registerRequest) instanceof RegisterResult);// a new user was made

        registerRequest = new RegisterRequest("JoeBob","IcannotThink", "joebob@gmail.com");
        RegisterRequest finalRegisterRequest = registerRequest;
        assertThrows(dataaccess.DataAccessException.class, () -> userService.register(finalRegisterRequest));//a new user was not made
    }

    @Test
    void registerNegative() throws dataaccess.DataAccessException {}

    @Test
    void login() throws dataaccess.DataAccessException {
        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        assertTrue(userService.login(loginRequest) instanceof LoginResult); // successful login

        loginRequest = new LoginRequest("JoeBob", "IThinkThereforeIAm");
        LoginRequest finalLoginRequest = loginRequest;
        assertThrows(dataaccess.DataAccessException.class, ()-> userService.login(finalLoginRequest));//failed to log in

    }

    @Test
    void logout() throws dataaccess.DataAccessException {
        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        LoginResult loginResult = userService.login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        LogoutResult expected = new LogoutResult();
        assertEquals(expected, userService.logout(logoutRequest)); //successful logout

        logoutRequest = new LogoutRequest("ILikePie");
        LogoutRequest finalLogoutRequest = logoutRequest;
        assertThrows(dataaccess.DataAccessException.class, ()-> userService.logout(finalLogoutRequest));//fail to logout

    }
}