package chess;

import java.util.Collection;
import java.util.HashSet;

import static chess.ChessPiece.PieceType.*;

public class PawnMovesCalculator extends PieceMovesCalculator{

    private final int forwards;
    private final int startingRow;
    private final int promotionRow;
    private final HashSet<ChessPiece.PieceType> promotionTypes;

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition){
        super(board,myPosition);
        if (myColor == ChessGame.TeamColor.WHITE){
            forwards = 1;
            startingRow = 2;
            promotionRow = 8;
        } else {
            forwards = -1;
            startingRow = 7;
            promotionRow = 1;
        }
        promotionTypes = new HashSet<ChessPiece.PieceType>();
        promotionTypes.add(QUEEN);
        promotionTypes.add(ROOK);
        promotionTypes.add(BISHOP);
        promotionTypes.add(KNIGHT);

    }
    @Override
    public Collection<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        //straight
        if (myPosition.getRow()==startingRow) { // double move
            if (board.getPiece(myPosition.getNextSquare(forwards,0)) == null
                    && board.getPiece(myPosition.getNextSquare(2*forwards,0)) == null){
                result.add(new ChessMove(myPosition,myPosition.getNextSquare(2*forwards,0)));
            }
        }
        if (board.getPiece(myPosition.getNextSquare(forwards,0)) == null){ //single move
            result.addAll(movesInOneDirection(forwards,0));
        }
        //diagonal right
        ChessPosition aPosition = myPosition.getNextSquare(forwards,1);
        if(board.getPiece(aPosition) != null && board.getPiece(aPosition).getTeamColor() == opponentColor){
            result.addAll(movesInOneDirection(forwards,1));
        }
        //diagonal left
        aPosition = myPosition.getNextSquare(forwards,-1);
        if(board.getPiece(aPosition) != null && board.getPiece(aPosition).getTeamColor() == opponentColor){
            result.addAll(movesInOneDirection(forwards,-1));
        }
        return result;
    }

    @Override
    public HashSet<ChessMove> movesInOneDirection(int rowAdd, int colAdd) {
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        ChessPosition aPosition = myPosition.getNextSquare(rowAdd,colAdd);
        if (aPosition.isOnBoard()){
            ChessPiece potentialPiece = board.getPiece(aPosition);
            if (potentialPiece==null || potentialPiece.getTeamColor()==opponentColor){ //if there isn't a piece there, or they are an opponent's piece
                if (aPosition.getRow() == promotionRow){
                    for(ChessPiece.PieceType type : promotionTypes){
                        result.add(new ChessMove(myPosition, aPosition, type));
                    }
                }else { // if we aren't promoting, just move
                    result.add(new ChessMove(myPosition, aPosition));
                }
            }
        }
        return result;
    }
}
