package chess.movecalculators;

import chess.*;

import java.util.HashSet;

public abstract class PieceMovesCalculator {

    protected final ChessBoard board;
    protected final ChessPosition myPosition;
    protected final ChessGame.TeamColor myColor;
    protected final ChessGame.TeamColor opponentColor;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
        this.myColor = board.getPiece(myPosition).getTeamColor();
        if(myColor == ChessGame.TeamColor.WHITE){
            opponentColor = ChessGame.TeamColor.BLACK;
        }else{
            opponentColor = ChessGame.TeamColor.WHITE;
        }
    }

    public abstract HashSet<ChessMove> pieceMoves();

    protected HashSet<ChessMove> longMove(int rowAdd, int colAdd){
        HashSet<ChessMove> results = new HashSet<>();
        ChessPosition aPosition = myPosition.getNextSquare(rowAdd,colAdd);
        while(aPosition.isOnBoard()){
            ChessPiece potentialPiece = board.getPiece(aPosition);
            if(potentialPiece == null){ //if the space is empty
                results.add(new ChessMove(myPosition,aPosition));
                aPosition = aPosition.getNextSquare(rowAdd, colAdd);
            } else if (potentialPiece.getTeamColor() == opponentColor){//if an enemy piece is there
                results.add(new ChessMove(myPosition,aPosition));
                break;
            } else {//if a friendly piece is there
                break;
            }
        }
        return results;
    }

    protected HashSet<ChessMove> oneMove(int rowAdd, int colAdd){
        HashSet<ChessMove> results = new HashSet<>();
        ChessPosition aPosition = myPosition.getNextSquare(rowAdd,colAdd);
        ChessPiece potentialPiece = board.getPiece(aPosition);
        if (aPosition.isOnBoard() && (potentialPiece == null || potentialPiece.getTeamColor() == opponentColor)){
            results.add(new ChessMove(myPosition,aPosition));
        }
        return results;
    }

}