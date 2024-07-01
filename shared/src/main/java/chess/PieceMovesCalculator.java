package chess;

import java.util.Collection;
import java.util.HashSet;

public class PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final ChessGame.TeamColor myColor;
    private final ChessGame.TeamColor opponentColor;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.myColor = getMyColor();
        this.opponentColor = getOpponentColor();
    }

    public Collection<ChessMove> pieceMoves(){ //the default is for bishops apparently
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        result.addAll(movesInOneDirection(1,1));
        result.addAll(movesInOneDirection(-1,1));
        result.addAll(movesInOneDirection(-1,-1));
        result.addAll(movesInOneDirection(1,-1));
        return result;
    }
    private ChessGame.TeamColor getMyColor(){
        if (board.getPiece(myPosition) == null){
            return null;
        } else{
            return board.getPiece(myPosition).getTeamColor();
        }

    }

    private ChessGame.TeamColor getOpponentColor(){
        if (board.getPiece(myPosition) == null){
            return null;
        }
        if(board.getPiece(myPosition).getTeamColor()==ChessGame.TeamColor.WHITE){
            return ChessGame.TeamColor.BLACK;
        }else{
            return ChessGame.TeamColor.WHITE;
        }
    }
    private HashSet<ChessMove> movesInOneDirection (int rowAdd,int colAdd){//the default goes to the edge of the board unless blocked by other pieces
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        ChessPosition aPosition = myPosition.getNextSquare(rowAdd,colAdd);
        while (aPosition.isOnBoard()){
            ChessPiece potentialPiece = board.getPiece(aPosition);
            if (potentialPiece==null){ //if there isn't a piece there
                result.add(new ChessMove(myPosition,aPosition));
                aPosition = aPosition.getNextSquare(rowAdd,colAdd);
            } else if(potentialPiece.getTeamColor()==myColor){ //if the piece is a teammate
                break;
            }else if (potentialPiece.getTeamColor()==opponentColor) { //if the piece is an opponent
                result.add(new ChessMove(myPosition,aPosition));
                break;
            }
        }
        return result;
    }

}
