package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class QueenMovesCalculator extends PieceMovesCalculator{

    public QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board,myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        result.addAll(longMove(1,0));
        result.addAll(longMove(0,1));
        result.addAll(longMove(-1,0));
        result.addAll(longMove(0,-1));

        result.addAll(longMove(1,1));
        result.addAll(longMove(-1,1));
        result.addAll(longMove(-1,-1));
        result.addAll(longMove(1,-1));
        return result;
    }
}
