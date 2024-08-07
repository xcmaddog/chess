package dataaccess;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.PositionPieceMapAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.GameInfo;
import mydataaccess.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class SQLGameDAO extends SQLAuthDAO implements GameDAO{

    public SQLGameDAO () {
        try{
            configureDatabase(createStatements);
        }
        catch(DataAccessException e){
            System.out.println("The SQLGameDAO failed to configure the database");
        }
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        ChessGame chessGame = gameData.getGame();
        String jsonChess = new Gson().toJson(chessGame, ChessGame.class);

        String statement = "INSERT INTO game (id, gamename, whiteusername, blackusername, chessgame) VALUES(?,?,?,?,?)";
        executeUpdate(statement, gameData.getGameID(), gameData.getGameName(), gameData.getWhiteUsername(),
                gameData.getBlackUsername(), jsonChess);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return readGame(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException (String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new HashSet<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    result = dataListMaker(rs);
                    return result;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public Collection<GameInfo> listGameInfo() throws DataAccessException{
        var result = new HashSet<GameInfo>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, gamename, whiteusername, blackusername FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    result = infoListMaker(rs);
                    return result;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (getGame(gameData.getGameID()) == null){
            throw new DataAccessException("The game you are trying to update does not exist");
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET whiteusername=? WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameData.getWhiteUsername());
                ps.setInt(2, gameData.getGameID());
                ps.executeUpdate();
            }
            statement = "UPDATE game SET blackusername=? WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameData.getBlackUsername());
                ps.setInt(2, gameData.getGameID());
                ps.executeUpdate();
            }
            statement = "UPDATE game SET chessgame=? WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                String jsonGame = new Gson().toJson(gameData.getGame(), ChessGame.class);
                ps.setString(1, jsonGame);
                ps.setInt(2, gameData.getGameID());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException (String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT id FROM game";
            try (var ps = conn.prepareStatement(statement)){
                try (var rs = ps.executeQuery()){
                    return !rs.next();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public GameData readGame(ResultSet rs) throws SQLException {
        if (rs.next()){
            int gameId = rs.getInt("id");
            String gameName = rs.getString("gamename");
            String whiteUsername = rs.getString("whiteusername");
            String blackUsername = rs.getString("blackusername");
            String jsonGame = rs.getString("chessgame");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new PositionPieceMapAdapter())
                    .create();

            ChessGame chessGame = gson.fromJson(jsonGame, ChessGame.class);

            GameData gameData = new GameData(gameId,gameName,chessGame);
            gameData.setWhiteUsername(whiteUsername);
            gameData.setBlackUsername(blackUsername);
            return gameData;
        } else {
            return null;
        }
    }

    public GameInfo readInfo(ResultSet rs) throws SQLException {
        if (rs.next()){
            int gameId = rs.getInt("id");
            String gameName = rs.getString("gamename");
            String whiteUsername = rs.getString("whiteusername");
            String blackUsername = rs.getString("blackusername");

            GameInfo gameInfo = new GameInfo(gameId,gameName,whiteUsername,blackUsername);
            return gameInfo;
        } else {
            return null;
        }
    }

    private HashSet<GameInfo> infoListMaker(ResultSet rs ) throws SQLException {
        HashSet<GameInfo> result = new HashSet<GameInfo>();
        boolean more = true;
        while (more) {
            GameInfo gameInfo = readInfo(rs);
            if(gameInfo == null){
                more = false;
            }else{
                result.add(gameInfo);
            }
        }
        return result;
    }

    private HashSet<GameData> dataListMaker(ResultSet rs ) throws SQLException {
        HashSet<GameData> result = new HashSet<GameData>();
        boolean more = true;
        while (more) {
            GameData gameData = readGame(rs);
            if(gameData == null){
                more = false;
            }else{
                result.add(gameData);
            }
        }
        return result;
    }

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
}
