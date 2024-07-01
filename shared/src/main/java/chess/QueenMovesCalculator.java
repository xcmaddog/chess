package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMovesCalculator extends PieceMovesCalculator{

    public QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board,myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        //all diagonal directions
        result.addAll(movesInOneDirection(1,1));
        result.addAll(movesInOneDirection(-1,1));
        result.addAll(movesInOneDirection(-1,-1));
        result.addAll(movesInOneDirection(1,-1));
        //all cardinal directions
        result.addAll(movesInOneDirection(0,1));
        result.addAll(movesInOneDirection(-1,0));
        result.addAll(movesInOneDirection(0,-1));
        result.addAll(movesInOneDirection(1,0));
        return result;
    }
}
