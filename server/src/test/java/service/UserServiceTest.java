package service;

import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserData firstUser = new UserData("JoeBob", "IcannotThink", "joebob@gmail.com");
        memoryUserDAO.createUser(firstUser);
        UserService userService = new UserService();
        userService.setUserDAO(memoryUserDAO);

        RegisterRequest registerRequest = new RegisterRequest("JillSmith", "ConfidentPassword", "jill2002@yahoo.com");
        assertTrue(userService.register(registerRequest) instanceof RegisterResult);// a new user was made
        //a new user was not made
    }

    @Test
    void login() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserData firstUser = new UserData("JoeBob", "IcannotThink", "joebob@gmail.com");
        memoryUserDAO.createUser(firstUser);
        UserService userService = new UserService();
        userService.setUserDAO(memoryUserDAO);

        LoginRequest loginRequest = new LoginRequest("JoeBob", "IcannotThink");
        userService.login(loginRequest);
        assertTrue(userService.login(loginRequest) instanceof LoginResult); // successful login

        //loginRequest = new LoginRequest("JoeBob", "IThinkThereforeIAm");
        //assertThrows(dataaccess.DataAccessException.class, execute(userService.login(loginRequest)));//failed to log in

    }

    @Test
    void logout() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserData firstUser = new UserData("JoeBob", "IcannotThink", "joebob@gmail.com");
        memoryUserDAO.createUser(firstUser);
        UserService userService = new UserService();
        userService.setUserDAO(memoryUserDAO);


    }
}