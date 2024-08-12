package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final ChessGame chessGame;
    private final boolean shouldDispWhite;

    public LoadGameMessage(ServerMessageType type, ChessGame chessGame, boolean shouldDispWhite) {
        super(type);
        this.chessGame = chessGame;
        this.shouldDispWhite = shouldDispWhite;
    }

    public ChessGame getChessGame(){
        return chessGame;
    }
    public boolean shouldDispWhite(){
        return shouldDispWhite;
    }
}
