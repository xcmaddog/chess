package chess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private HashMap<ChessPosition,ChessPiece> pieces;

    public ChessBoard() {
        pieces = HashMap.newHashMap(64);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces.put(position,piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces.get(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //set the white side
        //set the pawns
        ChessPosition aPosition = new ChessPosition(2,1);
        while(aPosition.isOnBoard()){
            addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            aPosition = new ChessPosition(2, aPosition.getColumn()+1);
        }
        //set the rooks
        aPosition = new ChessPosition(1,1);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        aPosition = new ChessPosition(1,8);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        //set the knights
        aPosition = new ChessPosition(1,2);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        aPosition = new ChessPosition(1,7);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        //set the bishops
        aPosition = new ChessPosition(1,3);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        aPosition = new ChessPosition(1,6);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        //set the queen
        aPosition = new ChessPosition(1,4);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        //set the king
        aPosition = new ChessPosition(1,5);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        //set the black side
        //set the pawns
        aPosition = new ChessPosition(7,1);
        while(aPosition.isOnBoard()){
            addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            aPosition = new ChessPosition(7, aPosition.getColumn()+1);
        }
        //set the rooks
        aPosition = new ChessPosition(8,1);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        aPosition = new ChessPosition(8,8);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        //set the knights
        aPosition = new ChessPosition(8,2);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        aPosition = new ChessPosition(8,7);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        //set the bishops
        aPosition = new ChessPosition(8,3);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        aPosition = new ChessPosition(8,6);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        //set the queen
        aPosition = new ChessPosition(8,4);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        //set the king
        aPosition = new ChessPosition(8,5);
        addPiece(aPosition,new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

        //might need to clear the rest of the board too...
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pieces);
    }
}
