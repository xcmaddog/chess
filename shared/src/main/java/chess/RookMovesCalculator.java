package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMovesCalculator extends PieceMovesCalculator{

    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board,myPosition);
    }

    @Override public Collection<ChessMove> pieceMoves() {
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        result.addAll(movesInOneDirection(0,1));
        result.addAll(movesInOneDirection(-1,0));
        result.addAll(movesInOneDirection(0,-1));
        result.addAll(movesInOneDirection(1,0));
        return result;
    }


}
