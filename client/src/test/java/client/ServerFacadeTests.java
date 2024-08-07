package client;

import mydataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;
import ui.ChessClient;
import ui.Repl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static Repl repl;
    private ChessClient chessClient;
    private static String URL;

    @BeforeAll
    public static void init() {
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

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
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
    void logoutPositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);
        chessClient.logout();
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
    @Order(1)
    void createGamePositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        chessClient.createGame(params);

        String expected = "GameID|WhiteUsername|BlackUsername|GameName\n" +
                "     1|             |             | newGame";
        String actual = chessClient.listGames();
        assertEquals(expected, actual);
    }

    @Test
    void createGameNegative() {
        String [] params = new String[]{"newGame"};
        assertThrows(Exception.class, ()-> chessClient.createGame(params)); // not logged in
    }

    @Test
    @Order(3)
    void listGamesPositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String firstID = chessClient.createGame(params);

        params = new String[]{"thisGame"};
        String secondID = chessClient.createGame(params);

        String expected = "GameID|WhiteUsername|BlackUsername|GameName\n" +
                "     4|             |             |thisGame\n" +
                "     3|             |             | newGame";
        String actual = chessClient.listGames();

        assertEquals(expected, actual);
    }

    @Test
    void listGamesNegative() {
        assertThrows(Exception.class, ()-> chessClient.listGames()); //not logged in
    }

    @Test
    @Order(2)
    void joinGamePositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        chessClient.createGame(params);
        String gameID = "2";

        params = new String[]{gameID, "BLACK"};
        String[] finalParams = params;
        assertDoesNotThrow(()-> chessClient.joinGame(finalParams));

        String expected = "GameID|WhiteUsername|BlackUsername|GameName\n" +
                "     2|             |      NewUser| newGame";
        String actual = chessClient.listGames();
        assertEquals(expected, chessClient.listGames());
    }

    @Test
    @Order(4)
    void joinGameNegative() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        String gameID = "-1";

        params = new String[]{gameID, "BLACK"};
        String[] finalParams = params;
        //chessClient.joinGame(finalParams);

        assertThrows(Exception.class, ()-> chessClient.joinGame(finalParams));
    }

    @Test
    @Order(5)
    void observeGamePositive() throws Exception {
        String[] params = {"NewUser", "newUserPassword", "nu@mail.com"};
        chessClient.register(params);

        params = new String[]{"newGame"};
        String gameID = chessClient.createGame(params);

        params = new String[]{"5", "BLACK"};
        chessClient.joinGame(params);

        String expected = String.format("You are now observing newGame.\n" +
                "null is playing as white and\n" +
                "NewUser is playing as black");

        params = new String[]{"5"};
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
