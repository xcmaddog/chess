package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;
import model.GameInfo;
import mydataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              id int NOT NULL,
              gamename VARCHAR(255) NOT NULL,
              whiteusername VARCHAR(255),
              blackusername VARCHAR(255),
              chessgame TEXT NOT NULL,
              PRIMARY KEY (id)
            )"""
    };

    private GameData aGameData;

    @BeforeEach
    void setUp () throws DataAccessException {
        DatabaseManager.createDatabase(); //make the database

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        aGameData = new GameData(1,"firstGame",chessGame,"firstUser",
                "secondUser");

        try (var conn = DatabaseManager.getConnection()) { // get a connection
            try {
                var cleanStatement = conn.prepareStatement("DROP TABLE IF EXISTS game"); //delete any previous table
                cleanStatement.executeUpdate();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) { // make the user table
                    preparedStatement.executeUpdate();
                }
            }
            String statement = "INSERT INTO game (id, gamename, whiteusername, blackusername, chessgame)" +
                    " VALUES(?,?,?,?,?)";
            try(var addAGame = conn.prepareStatement(statement)){
                addAGame.setInt(1, aGameData.getGameID());
                addAGame.setString(2, aGameData.getGameName());
                addAGame.setString(3, aGameData.getWhiteUsername());
                addAGame.setString(4, aGameData.getBlackUsername());
                addAGame.setString(5, jsonChess);
                addAGame.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Test
    void createGamePositive() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        GameData gameData = new GameData(2,"secondGame",chessGame,"thirdUser",
                "fifthUser");

        theDAO.createGame(gameData);

        GameData result = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, 2);
                try (var rs = ps.executeQuery()) {
                    result = theDAO.readGame(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }

        assertEquals(gameData, result);

    }

    @Test
    void createGameNegative() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        GameData gameData = new GameData(1,"secondGame",chessGame,"thirdUser",
                "fifthUser");

        assertThrows(Exception.class, ()-> theDAO.createGame(gameData));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        GameData expected = new GameData(1,"firstGame",chessGame,"firstUser",
                "secondUser");

        GameData result = theDAO.getGame(1);
        System.out.println(result.getGame().toString());
        assertEquals(expected, result);
    }

    @Test
    void getGameNegative() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        GameData result = theDAO.getGame(-1);
        assertNull(result);
    }

    @Test
    void listGameInfoPositive() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        GameData gameData = new GameData(2,"secondGame",chessGame,"thirdUser",
                "fifthUser");

        theDAO.createGame(gameData);

        Collection<GameInfo> expected = new HashSet<GameInfo>();
        expected.add(new GameInfo(aGameData));
        expected.add(new GameInfo(gameData));

        Collection<GameInfo> result = theDAO.listGameInfo();

        assertEquals(expected, result);
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        GameData gameData = new GameData(2,"secondGame",chessGame,"thirdUser",
                "fifthUser");

        theDAO.createGame(gameData);

        Collection<GameData> expected = new HashSet<GameData>();
        expected.add(aGameData);
        expected.add(gameData);

        Collection<GameData> result = theDAO.listGames();

        assertEquals(expected, result);

    }

    @Test
    void updateGamePositive() throws DataAccessException, InvalidMoveException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2,3),new ChessPosition(3,3)));
        String jsonChess = new Gson().toJson(chessGame);
        GameData expected = new GameData(1,"firstGame",chessGame,"firstUser",
                "secondUser");

        theDAO.updateGame(expected);
        GameData result = theDAO.getGame(1);
        assertEquals(expected, result);
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        ChessGame chessGame = new ChessGame();
        String jsonChess = new Gson().toJson(chessGame);
        GameData gameData = new GameData(2,"firstGame",chessGame,"firstUser",
                "secondUser");

        assertThrows(Exception.class, ()-> theDAO.updateGame(gameData));
    }

    @Test
    void clear() throws DataAccessException {
        SQLGameDAO theDAO = new SQLGameDAO();

        theDAO.clear();
        assertTrue(theDAO.isEmpty());
    }
}