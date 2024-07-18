package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMovesCalculator extends PieceMovesCalculator{

    public KnightMovesCalculator(ChessBoard board, ChessPosition myPosition){
        super(board,myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        result.addAll(oneMove(1,2));
        result.addAll(oneMove(1,-2));
        result.addAll(oneMove(-1,2));
        result.addAll(oneMove(-1,-2));

        result.addAll(oneMove(2,1));
        result.addAll(oneMove(2,-1));
        result.addAll(oneMove(-2,1));
        result.addAll(oneMove(-2,-1));
        return result;
    }

}
