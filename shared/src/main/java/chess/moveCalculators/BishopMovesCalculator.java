package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class BishopMovesCalculator extends PieceMovesCalculator{

    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition){
        super(board,myPosition);
    }

    public HashSet<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        result.addAll(longMove(1,1));
        result.addAll(longMove(-1,1));
        result.addAll(longMove(-1,-1));
        result.addAll(longMove(1,-1));
        return result;
    }
}
