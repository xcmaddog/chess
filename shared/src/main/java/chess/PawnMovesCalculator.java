package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMovesCalculator extends PieceMovesCalculator{

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition){
        super(board,myPosition);
    }
    @Override
    public Collection<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        //straight
        result.addAll(movesInOneDirection(1,0));
        //diagonal right
        ChessPosition aPosition = myPosition.getNextSquare(1,1);
        if(board.getPiece(aPosition).getTeamColor() == opponentColor){
            result.addAll(movesInOneDirection(1,1));
        }
        //diagonal left
        aPosition = myPosition.getNextSquare(1,-1);
        if(board.getPiece(aPosition).getTeamColor() == opponentColor){
            result.addAll(movesInOneDirection(1,-1));
        }
        return result;
    }

    @Override
    public HashSet<ChessMove> movesInOneDirection(int rowAdd, int colAdd) {
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        ChessPosition aPosition = myPosition.getNextSquare(rowAdd,colAdd);
        if (aPosition.isOnBoard()){
            ChessPiece potentialPiece = board.getPiece(aPosition);
            if (potentialPiece==null){ //if there isn't a piece there
                result.add(new ChessMove(myPosition,aPosition));
            } else if(potentialPiece.getTeamColor()==myColor){ //if the piece is a teammate
            }else if (potentialPiece.getTeamColor()==opponentColor) { //if the piece is an opponent
                result.add(new ChessMove(myPosition,aPosition));
            }
        }
        return result;
    }
}
