package websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;



import mydataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request.JoinGameRequest;
import request.RegisterRequest;
import service.GamesService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GamesService gamesService;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<HashMap<ChessPosition,
            ChessPiece>>(){}.getType(), new PositionPieceMapAdapter()).create();

    public WebSocketHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) throws DataAccessException {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gamesService = new GamesService(userDAO, gameDAO, authDAO);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand action = gson.fromJson(message, UserGameCommand.class);
        String authToken = action.getAuthToken();
        AuthData autData = authDAO.getAuth(authToken);
        String username = autData.username();
        switch (action.getCommandType()) {
            case CONNECT -> join(username, action.getGameID(), session);
            case LEAVE -> leave(username, action.getGameID(), session);
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                move(username, moveCommand.getGameID(), moveCommand.getChessMove(), session);
            }
            default -> sendError(username, action.getGameID(), "Error: game command failed");
        }
    }

    private void join(String username, int gameID, Session session) {
        GameData gameData = null;
        try{
            gameData= gameDAO.getGame(gameID);
        } catch(DataAccessException e){
            String msg = "There is not a game by that ID";
            sendError(username, gameID, msg);
        }
        if (gameData != null){
            try{
                boolean shouldDispWhite = true;
                if(Objects.equals(gameData.getBlackUsername(), username)){
                    shouldDispWhite = false;
                }
                connections.add(gameID, username, session);
                String message;
                if(Objects.equals(gameData.getBlackUsername(), username)){
                    message = String.format("%s has joined game %s. They are the BLACK player", username, gameID);
                } else if(Objects.equals(gameData.getWhiteUsername(), username)){
                    message = String.format("%s has joined game %s. They are the WHITE player", username, gameID);
                } else{
                    message = String.format("%s has joined game %s. They are an observer.", username, gameID);
                }
                NotificationMessage notification =
                        new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(gameID, username, notification);
                ChessGame chessGame = gameData.getGame();
                LoadGameMessage loadGameMessage =
                        new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame,shouldDispWhite);
                connections.individualMessage(gameID, username, loadGameMessage);
            }catch(Exception e){
                sendError(username, gameID, e.getMessage());
            }
        }
    }

    private void leave(String username, int gameID, Session session) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        String message;
        if(Objects.equals(gameData.getBlackUsername(), username)){
            message = String.format("%s has left game %s. They were the BLACK player", username, gameID);
        } else if(Objects.equals(gameData.getWhiteUsername(), username)){
            message = String.format("%s has left game %s. They were the WHITE player", username, gameID);
        } else{
            message = String.format("%s has left game %s. They were an observer.", username, gameID);
        }
        connections.remove(gameID, username);
        NotificationMessage notification =
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID,null, notification);
    }

    private void move (String username, int gameID, ChessMove chessMove, Session session)
            throws DataAccessException, IOException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame chessGame = gameData.getGame();
        try{
            chessGame.makeMove(chessMove);
            gameDAO.updateGame(new GameData(gameID, gameData.getGameName(), chessGame, gameData.getWhiteUsername(),
                    gameData.getBlackUsername()));
            gameData = gameDAO.getGame(gameID);
            chessGame = gameData.getGame();
            //send picture of board
            ChessGame.TeamColor oppColor = ChessGame.TeamColor.BLACK;
            boolean shouldDispWhite = true;
            if(Objects.equals(username, gameData.getBlackUsername())){
                shouldDispWhite = false;
                oppColor = ChessGame.TeamColor.WHITE;
            }
            LoadGameMessage loadGameMessage =
                    new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame, shouldDispWhite);
            connections.broadcast(gameID, null, loadGameMessage);
            //sent text notification
            sendMoveText(chessMove, username, gameID);
            //notify of check/checkmate/stalemate
            sendGameStatus(chessGame,username,gameID,oppColor);
        } catch(InvalidMoveException e){
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.individualMessage(gameID, username, errorMessage);
        }
    }
    private void sendMoveText(ChessMove chessMove, String username, int gameID) throws IOException {
        String message;
        if(chessMove.getPromotionPiece() != null){
            message = String.format("%s moved a pawn from %s to %s and promoted it to a %s",
                    username, chessMove.getStartPosition(), chessMove.getEndPosition(), chessMove.getPromotionPiece());
        } else{
            message = String.format("%s moved from %s to %s",
                    username, chessMove.getStartPosition(), chessMove.getEndPosition());
        }
        NotificationMessage notificationMessage =
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(gameID, username, notificationMessage);
        if (chessMove.getPromotionPiece() == null){
            message = String.format("You successfully moved from %s to %s",
                    chessMove.getStartPosition(), chessMove.getEndPosition());
        }else{
            message = String.format("You successfully moved from %s to %s and the piece was promoted to a %s",
                    chessMove.getStartPosition(), chessMove.getEndPosition(), chessMove.getPromotionPiece());
        }
        notificationMessage =
                new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.individualMessage(gameID, username, notificationMessage);
    }
    private void sendGameStatus(ChessGame chessGame, String username, int gameID, ChessGame.TeamColor oppColor)
            throws IOException {
        String message;
        NotificationMessage notificationMessage;
        if (chessGame.isInCheckmate(oppColor)){
            message = String.format("The game is now in checkmate and %s wins", username);
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, null, notificationMessage);
        } else if (chessGame.isInCheck(oppColor)) {
            message = String.format("%s put their opponent in check",username);
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, null, notificationMessage);
        } else if (chessGame.isInStalemate(oppColor)) {
            message = "The game is now in stalemate";
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, null, notificationMessage);
        }
    }

    private void sendError(String username,int gameID, String message){
        try{
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.individualMessage(gameID, username, errorMessage);
        } catch (IOException e){//beware of infinite loops
            sendError(username, gameID, e.getMessage());
        }
    }
}
