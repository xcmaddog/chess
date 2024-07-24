package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KingMovesCalculator extends PieceMovesCalculator{

    public KingMovesCalculator(ChessBoard board, ChessPosition myPosition){
        super(board,myPosition);
    }

    @Override
    public HashSet<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<>();
        result.addAll(oneMove(1,0));
        result.addAll(oneMove(0,1));
        result.addAll(oneMove(-1,0));
        result.addAll(oneMove(0,-1));

        result.addAll(oneMove(1,1));
        result.addAll(oneMove(-1,1));
        result.addAll(oneMove(-1,-1));
        result.addAll(oneMove(1,-1));
        return result;
    }

}
