package ui;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

class ChessClientTest {

    private static ServerFacade serverFacade;
    private static Server server;
    private static Repl repl;
    private ChessClient chessClient;
    private static String URL;

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        URL = String.format("http://localhost:%d",port);
        repl = new Repl(URL);
        serverFacade = new ServerFacade(URL);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        serverFacade.clear();
        chessClient = new ChessClient(URL, repl);
    }


    @Test
    void loginPositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);
        chessClient.logout();

        params = new String[]{"NewUser", "newUserPassword"};
        String[] finalParams = params;
        assertDoesNotThrow(()->chessClient.login(finalParams));
    }

    @Test
    void loginNegative() {
        String [] params = new String[]{"NotAUser", "notAUserPassword"};
        String[] finalParams = params;
        assertThrows(DataAccessException.class, ()-> chessClient.login(finalParams));
    }

    @Test
    void logoutPositive() {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        assertDoesNotThrow(()-> chessClient.register(params));
        assertFalse(chessClient.isSignedIn());
    }

    @Test
    void logoutNegative() {
        assertThrows(DataAccessException.class, ()-> chessClient.logout());
    }

    @Test
    void registerPositive() {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        assertDoesNotThrow(()-> chessClient.register(params));
        assertTrue(chessClient.isSignedIn());
    }

    @Test
    void registerNegative() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"ThisIsANewUser", "ANewUserPassword", "sum@mail.com"};
        String[] finalParams = params;
        assertThrows(Exception.class, ()-> chessClient.register(finalParams));
    }

    @Test
    void createGamePositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String gameID = chessClient.createGame(params);

        String expected = String.format("     %s|             |             | newGame", gameID);
        assertEquals(expected, chessClient.listGames());
    }

    @Test
    void createGameNegative() {
        String [] params = new String[]{"newGame"};
        assertThrows(Exception.class, ()-> chessClient.createGame(params)); // not logged in
    }

    @Test
    void listGamesPositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String firstID = chessClient.createGame(params);

        params = new String[]{"thisGame"};
        String secondID = chessClient.createGame(params);

        String expected = String.format("     %s|             |             | newGame\n" +
                "     %s|             |             | thisGame", firstID,secondID);

        assertEquals(expected, chessClient.listGames());
    }

    @Test
    void listGamesNegative() {
        assertThrows(Exception.class, ()-> chessClient.listGames()); //not logged in
    }

    @Test
    void joinGamePositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String gameID = chessClient.createGame(params);

        params = new String[]{gameID, "BLACK"};
        String[] finalParams = params;
        assertDoesNotThrow(()-> chessClient.joinGame(finalParams));

        String expected = String.format("     %s|             |      NewUser| newGame", gameID);
        assertEquals(expected, chessClient.listGames());
    }

    @Test
    void joinGameNegative() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String gameID = chessClient.createGame(params);

        params = new String[]{gameID, "BLACK"};
        String[] finalParams = params;
        chessClient.joinGame(finalParams);

        assertThrows(Exception.class, ()-> chessClient.joinGame(finalParams));
    }

    @Test
    void observeGamePositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String gameID = chessClient.createGame(params);

        String expected = String.format("You are now observing newGame.\n" +
                " is playing as white and\n" +
                "NewUser is playing as black");

        params = new String[]{gameID};
        assertEquals(expected, chessClient.observeGame(params));
    }

    @Test
    void observeGameNegative() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"-1"};
        String[] finalParams = params;
        assertThrows(Exception.class, ()-> chessClient.observeGame(finalParams));
    }
}