package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


class GamesServiceTest {

    private GameData thisGame;
    private GameData oneGame;
    private GamesService gameService;

    @BeforeEach
    void setUp() throws InvalidMoveException {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        UserData firstUser = new UserData("JoeBob", "IcannotThink", "joebob@gmail.com");
        memoryUserDAO.createUser(firstUser);
        String firstAuthToken = "thisIsTheFirstAuthToken";
        AuthData firstAuth = new AuthData(firstAuthToken,"JoeBob");
        memoryAuthDAO.createAuth(firstAuth);

        UserData secondUser = new UserData("JillSmith", "ConfidentPassword",
                "jills2002@yahoo.com");
        memoryUserDAO.createUser(secondUser);
        String secondAuthToken = "thisIsTheSecondAuthToken";
        AuthData secondAuth = new AuthData(secondAuthToken, "JillSmith");
        memoryAuthDAO.createAuth(secondAuth);

        ChessGame withAMove = new ChessGame();
        ChessMove theMove = new ChessMove(new ChessPosition(2,3),new ChessPosition(3,3));
        withAMove.makeMove(theMove);
        thisGame = new GameData(4321,"Hippocampus",withAMove,"JillSmith",
                "JoeBob");
        oneGame = new GameData(1234,"TheGame", new ChessGame(), null,
                "JillSmith");
        memoryGameDAO.createGame(thisGame);
        memoryGameDAO.createGame(oneGame);

        gameService = new GamesService(memoryUserDAO, memoryGameDAO, memoryAuthDAO);
    }

    @Test
    void getGamesPositive() throws InvalidMoveException, dataaccess.DataAccessException {
        HashSet<GameInfo> expected = new HashSet<>();
        expected.add(new GameInfo(thisGame));
        expected.add(new GameInfo(oneGame));

        ListGamesRequest listGamesRequest = new ListGamesRequest("thisIsTheFirstAuthToken");
        // should have a hash set of games
        assertEquals(expected, gameService.getGames(listGamesRequest).getAllGameInfo());
    }

    @Test
    void getGamesNegative() throws InvalidMoveException, dataaccess.DataAccessException {
        HashSet<GameInfo> expected = new HashSet<>();
        expected.add(new GameInfo(thisGame));
        expected.add(new GameInfo(oneGame));

        ListGamesRequest listGamesRequest = new ListGamesRequest("thisIsAnInvalidAuthToken");
        ListGamesRequest finalListGamesRequest = listGamesRequest;
        //throws for invalid authToken
        assertThrows(dataaccess.DataAccessException.class,() -> gameService.getGames(finalListGamesRequest));
    }

    @Test
    void createGamePositive() throws InvalidMoveException, dataaccess.DataAccessException {
        String someAuth = "thisIsTheFirstAuthToken";
        CreateGameRequest createGameRequest = new CreateGameRequest("TheGame");
        CreateGameResult expected = new CreateGameResult(1);
        assertEquals(expected, gameService.createGame(someAuth, createGameRequest));
    }

    @Test
    void createGameNegative() throws InvalidMoveException, dataaccess.DataAccessException {
        String invalidAuth = "anInvalidAuthToken";
        CreateGameRequest createGameRequest = new CreateGameRequest("Bogus");
        CreateGameRequest finalCreateGameRequest = createGameRequest;
        assertThrows(dataaccess.DataAccessException.class, () -> gameService.createGame(invalidAuth,
                finalCreateGameRequest));
    }

    @Test
    void joinGamePositive() throws InvalidMoveException, dataaccess.DataAccessException {
        String anAuthToken = "thisIsTheFirstAuthToken";
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1234);
        JoinGameResult expected = new JoinGameResult();
        assertEquals(expected, gameService.joinGame(anAuthToken, joinGameRequest)); //successful add to a game
    }
    @Test
    void joinGameNegative() throws InvalidMoveException, dataaccess.DataAccessException {
        String invalidToken = "anInvalidAuthToken";
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1234);
        JoinGameRequest finalJoinGameRequest = joinGameRequest;
        assertThrows(dataaccess.DataAccessException.class, () -> gameService.joinGame(invalidToken,
                finalJoinGameRequest));// invalid AuthToken
    }
}