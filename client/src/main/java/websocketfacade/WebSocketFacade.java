package websocketfacade;

import chess.ChessGame;
import com.google.gson.Gson;
import mydataaccess.DataAccessException;
import ui.BoardDisplay;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    BoardDisplay boardDisplay;


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
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);


                    //right now it only displays a generic board from the white pov
                    boardDisplay.displayWhiteBoard(new ChessGame());
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
    public void joinGame(String username, String authToken, int GameId) throws DataAccessException {
        try{
            UserGameCommand userGameCommand =
                    new UserGameCommand(UserGameCommand.CommandType.CONNECT, username, authToken, GameId);
        } catch(Exception e){ // maybe catch specific excecption type
            throw new DataAccessException(e.getMessage());
        }
    }
}
