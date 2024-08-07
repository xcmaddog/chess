package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import model.UserData;
import mydataaccess.DataAccessException;
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
    void registerPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("JillSmith", "ConfidentPassword",
                "jill2002@yahoo.com");
        assertTrue(userService.register(registerRequest) instanceof RegisterResult);// a new user was made
    }

    @Test
    void registerNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("JoeBob","IcannotThink",
                "joebob@gmail.com");
        RegisterRequest finalRegisterRequest = registerRequest;
        //a new user was not made
        assertThrows(DataAccessException.class, () -> userService.register(finalRegisterRequest));
    }

    @Test
    void loginPositive() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        assertTrue(userService.login(loginRequest) instanceof LoginResult); // successful login

    }
    @Test
    void loginNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        loginRequest = new LoginRequest("JoeBob", "IThinkThereforeIAm");
        LoginRequest finalLoginRequest = loginRequest;
        assertThrows(DataAccessException.class, ()-> userService.login(finalLoginRequest));//failed to log in
    }

    @Test
    void logoutPositive() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        LoginResult loginResult = userService.login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        LogoutResult expected = new LogoutResult();
        assertEquals(expected, userService.logout(logoutRequest)); //successful logout
    }
    @Test
    void logoutNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        LoginResult loginResult = userService.login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest("ILikePie");
        LogoutRequest finalLogoutRequest = logoutRequest;
        assertThrows(DataAccessException.class, ()-> userService.logout(finalLogoutRequest));//fail to logout
    }
}