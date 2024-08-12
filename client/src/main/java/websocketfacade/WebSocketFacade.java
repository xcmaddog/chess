package websocketfacade;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mydataaccess.DataAccessException;
import ui.BoardDisplay;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class WebSocketFacade extends Endpoint {

    private final Session session;
    private final BoardDisplay boardDisplay;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<HashMap<ChessPosition,
            ChessPiece>>(){}.getType(), new PositionPieceMapAdapter()).create();


    public WebSocketFacade(String url, BoardDisplay boardDisplay) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.boardDisplay = boardDisplay;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {

                    //System.out.println("Message class: " + message.getClass().getName());
                    //System.out.println("Raw message content: " + message);

                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    var messageType = serverMessage.getServerMessageType();
                    switch (messageType) {
                        case NOTIFICATION -> {
                            NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                            boardDisplay.displayMessage(notificationMessage.getMessage());
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                            boardDisplay.displayMessage(errorMessage.getErrorMessage());
                        }
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                            if(loadGameMessage.shouldDispWhite()){
                                boardDisplay.displayWhiteBoard(loadGameMessage.getChessGame(), null);
                            } else{
                                boardDisplay.displayBlackBoard(loadGameMessage.getChessGame(), null);
                            }
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    //here I will put the methods for the different actions
    public void joinGame(String username, String authToken, int GameId) throws Exception {
        try{
            UserGameCommand userGameCommand =
                    new UserGameCommand(UserGameCommand.CommandType.CONNECT, username, authToken, GameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch(Exception e){ // maybe catch specific exception type
            throw new Exception("failed to join the game");
        }
    }
    public void leaveGame(String username, String authToken, int GameId) throws Exception {
        try{
            UserGameCommand userGameCommand =
                    new UserGameCommand(UserGameCommand.CommandType.LEAVE, username, authToken, GameId);
            String json = gson.toJson(userGameCommand);
            this.session.getBasicRemote().sendText(json);
        } catch (Exception e){
            throw new Exception("failed to leave the game");
        }
    }

    public void makeMove(String username, String authToken, int gameID, ChessMove chessMove) throws Exception {
        try{
            MakeMoveCommand makeMoveCommand =
                    new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, username, authToken, gameID, chessMove);
            String json = gson.toJson(makeMoveCommand);
            this.session.getBasicRemote().sendText(json);
        } catch (Exception e){
            throw new Exception("failed to make the move");
        }
    }
}
