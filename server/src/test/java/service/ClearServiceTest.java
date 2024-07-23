package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    @Test
    void clearAll() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        UserData firstUser = new UserData("JoeBob", "IcannotThink", "joebob@gmail.com");
        memoryUserDAO.createUser(firstUser);
        String firstAuthToken = "thisIsTheFirstAuthToken";
        AuthData firstAuth = new AuthData(firstAuthToken,"JoeBob");
        memoryAuthDAO.createAuth(firstAuth);

        UserData secondUser = new UserData("JillSmith", "ConfidentPassword", "jills2002@yahoo.com");
        memoryUserDAO.createUser(secondUser);
        String secondAuthToken = "thisIsTheSecondAuthToken";
        AuthData secondAuth = new AuthData(secondAuthToken, "JillSmith");
        memoryAuthDAO.createAuth(secondAuth);

        ChessGame withAMove = new ChessGame();
        ChessMove theMove = new ChessMove(new ChessPosition(2,3),new ChessPosition(3,3));
        withAMove.makeMove(theMove);
        GameData thisGame = new GameData(4321,"Hippocampus",withAMove);
        GameData oneGame = new GameData(1234,"TheGame", new ChessGame());
        memoryGameDAO.createGame(thisGame);
        memoryGameDAO.createGame(oneGame);
    }
}