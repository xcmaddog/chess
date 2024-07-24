package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMovesCalculator extends PieceMovesCalculator{

    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board,myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<>();
        result.addAll(longMove(1,0));
        result.addAll(longMove(0,1));
        result.addAll(longMove(-1,0));
        result.addAll(longMove(0,-1));
        return result;
    }


}
