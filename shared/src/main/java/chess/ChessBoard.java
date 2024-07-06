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
    private ChessPosition blackKingPosition;
    private ChessPosition whiteKingPosition;

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
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                blackKingPosition = position;
            }else{
                whiteKingPosition = position;
            }
        }
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
        pieces = HashMap.newHashMap(64);
        //white side
        //pawns
        for(int i = 1;i<=8;i++){
            this.addPiece(new ChessPosition(2,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        //rooks
        this.addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        //knights
        this.addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        //bishops
        this.addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        //queen
        this.addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        //king
        this.addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        //black side
        //pawns
        for(int i = 1;i<=8;i++){
            this.addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        //rooks
        this.addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        //knights
        this.addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        //bishops
        this.addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        //queen
        this.addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        //king
        this.addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }

    public HashSet<ChessPosition> getTeamPieces(ChessGame.TeamColor color){
        HashSet<ChessPosition> result = new HashSet<ChessPosition>();
        for(int row = 1; row<=8; row++){
            for(int col = 1; col<=8; col++){
                ChessPosition aPosition = new ChessPosition(row,col);
                ChessPiece potentialPiece = pieces.get(aPosition);
                if(potentialPiece != null && potentialPiece.getTeamColor() == color){
                    result.add(aPosition);
                }
            }
        }
        return result;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color){
        if (color == ChessGame.TeamColor.WHITE){
            return whiteKingPosition;
        }else {
            return blackKingPosition;
        }
    }

    private void removePiece(ChessPosition position){
        pieces.remove(position);
    }

    public void makeMove(ChessMove move){
        ChessPosition endPosition = move.getEndPosition();
        ChessPosition startPosition = move.getStartPosition();
        ChessGame.TeamColor color = pieces.get(startPosition).getTeamColor();
        ChessPiece.PieceType type = pieces.get(startPosition).getPieceType();
        removePiece(endPosition);
        removePiece(startPosition);
        if(move.getPromotionPiece() != null){
            ChessPiece promotedPiece = new ChessPiece(color,move.getPromotionPiece());
            addPiece(endPosition,promotedPiece);
        } else{
            ChessPiece copyPiece = new ChessPiece(color,type);
            addPiece(endPosition,copyPiece);
        }
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

    @Override
    protected ChessBoard clone() {
        ChessBoard theCopy = new ChessBoard();
        for(int row = 1; row<=8; row++){
            for(int col = 1; col<=8; col++){
                ChessPosition aPosition = new ChessPosition(row,col);
                ChessPiece potentialPiece = pieces.get(aPosition);
                if(potentialPiece != null){
                    theCopy.addPiece(aPosition,potentialPiece);
                }
            }
        }
        return theCopy;
    }
}
