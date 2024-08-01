package chess;

import chess.movecalculators.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type){
            case KING: KingMovesCalculator kmc = new KingMovesCalculator(board,myPosition);
                return kmc.pieceMoves();
            case PAWN: PawnMovesCalculator pmc = new PawnMovesCalculator(board,myPosition);
                return pmc.pieceMoves();
            case ROOK: RookMovesCalculator rmc = new RookMovesCalculator(board,myPosition);
                return rmc.pieceMoves();
            case QUEEN: QueenMovesCalculator qmc = new QueenMovesCalculator(board,myPosition);
                return qmc.pieceMoves();
            case BISHOP: BishopMovesCalculator bmc = new BishopMovesCalculator(board,myPosition);
                return bmc.pieceMoves();
            case KNIGHT: KnightMovesCalculator knmc = new KnightMovesCalculator(board,myPosition);
                return knmc.pieceMoves();
            case null, default:
                return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return String.format("%s %s",pieceColor.toString(),type.toString());
    }

}
