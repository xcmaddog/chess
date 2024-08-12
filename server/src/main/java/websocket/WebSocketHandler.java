package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.GameData;.



import mydataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;

    public WebSocketHandler(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> join(action.getUsername(), action.getGameID(), session);
            case LEAVE -> leave(action.getUsername(), action.getGameID(), session);
            default -> throw new IOException("Error: wrong game command type");
        }
    }

    private void join(String username, int gameID, Session session) throws IOException, DataAccessException {
        GameData gameData= gameDAO.getGame(gameID);
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
        connections.sendBoard(gameID, username, loadGameMessage);
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
}
