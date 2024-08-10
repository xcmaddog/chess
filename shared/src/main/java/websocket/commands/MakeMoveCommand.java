package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove chessMove;

    public MakeMoveCommand(CommandType commandType, String username, String authToken, Integer gameID, ChessMove chessMove) {
        super(commandType, username, authToken, gameID);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove() {
        return chessMove;
    }
}
