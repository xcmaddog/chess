package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMovesCalculator extends PieceMovesCalculator{

    public KnightMovesCalculator(ChessBoard board, ChessPosition myPosition){
        super(board,myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        //all diagonal directions
        result.addAll(movesInOneDirection(2,1));
        result.addAll(movesInOneDirection(2,-1));
        result.addAll(movesInOneDirection(-2,1));
        result.addAll(movesInOneDirection(-2,-1));
        result.addAll(movesInOneDirection(1,2));
        result.addAll(movesInOneDirection(1,-2));
        result.addAll(movesInOneDirection(-1,2));
        result.addAll(movesInOneDirection(-1,-2));

        return result;
    }

    @Override
    public HashSet<ChessMove> movesInOneDirection(int rowAdd, int colAdd){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        ChessPosition aPosition = myPosition.getNextSquare(rowAdd,colAdd);
        if (aPosition.isOnBoard()){
            ChessPiece potentialPiece = board.getPiece(aPosition);
            if (potentialPiece==null || potentialPiece.getTeamColor()==opponentColor){ //if there isn't a piece there, or they are an opponent's piece
                result.add(new ChessMove(myPosition, aPosition));
            }
        }
        return result;
    }
}
