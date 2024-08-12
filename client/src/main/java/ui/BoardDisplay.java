package ui;

import chess.ChessGame;
import chess.ChessPosition;

import java.util.HashSet;

public interface BoardDisplay {
    public void displayBlackBoard(ChessGame chessGame, HashSet<ChessPosition> validSquares);
    public void displayWhiteBoard(ChessGame chessGame, HashSet<ChessPosition> validSquares);
    public void displayMessage(String message);
}
