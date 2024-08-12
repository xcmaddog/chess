package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final ChessGame game;
    private final boolean shouldDispWhite;

    public LoadGameMessage(ServerMessageType type, ChessGame game, boolean shouldDispWhite) {
        super(type);
        this.game = game;
        this.shouldDispWhite = shouldDispWhite;
    }

    public ChessGame getGame(){
        return game;
    }
    public boolean shouldDispWhite(){
        return shouldDispWhite;
    }
}
