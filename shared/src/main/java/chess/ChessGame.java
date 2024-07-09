package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        ChessBoard b = new ChessBoard();
        b.resetBoard();
        this.board = b;
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessGame.TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> unfilteredMoves = board.getPiece(startPosition).pieceMoves(board,startPosition);
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        for(ChessMove m : unfilteredMoves){
            ChessBoard potentialFutureBoard = board.clone();
            potentialFutureBoard.makeMove(m);
            ChessGame potentialFutureGame = new ChessGame();
            potentialFutureGame.setBoard(potentialFutureBoard);
            if(!potentialFutureGame.isInCheck(teamColor)){
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece potientialPiece = board.getPiece(move.getStartPosition());
        if (potientialPiece == null){
            throw new InvalidMoveException("There is no piece at the starting position");
        }
        TeamColor attemptingTeam = board.getPiece(move.getStartPosition()).getTeamColor();
        if (attemptingTeam != teamTurn){
            throw new InvalidMoveException("It is not your turn");
        }
        Collection<ChessMove> allowedMoves = validMoves(move.getStartPosition());
        boolean isValid = allowedMoves.contains(move);
        if (isValid){
            board.makeMove(move);
            if(potientialPiece.getTeamColor() == TeamColor.WHITE){
                this.setTeamTurn(TeamColor.BLACK);
            } else {
                this.setTeamTurn(TeamColor.WHITE);
            }
        } else {
            throw new InvalidMoveException("This is not a valid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //store the opponent's color as a variable
        TeamColor opponentColor;
        if(teamColor == TeamColor.WHITE){
            opponentColor = TeamColor.BLACK;
        } else {
            opponentColor = TeamColor.WHITE;
        }
        //get a HashSet of all the opponent's pieces
        HashSet<ChessPosition> opponentTeamPieces = board.getTeamPieces(opponentColor);
        //get the position of our king
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        //see if any of the moves that any of the enemy team pieces can make can take out the king
        for(ChessPosition p : opponentTeamPieces){
            Collection<ChessMove> someMoves = board.getPiece(p).pieceMoves(board,p);
            for(ChessMove m : someMoves){
                ChessPosition endPosition = m.getEndPosition();
                boolean endVsKing = endPosition.equals(kingPosition);
                if(endVsKing){
                    return true;
                }
            }
        }
        //if none of them can, we are not in check
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // first, check if we are in check
        if(!isInCheck(teamColor)){
            return false;
        }
        /* if we are in check, then we will be in checkmate if none of the moves
        we can make next turn end with us not in check
        */
        //check each possible move, if any of them end with us not in check, we are not in checkmate
        HashSet<ChessPosition> teamPieces = board.getTeamPieces(teamColor);
        for (ChessPosition p: teamPieces){
            Collection<ChessMove> movesOfThisPiece = board.getPiece(p).pieceMoves(board,p);
            for(ChessMove m : movesOfThisPiece){ //This can get swapped out using the validMoves method
                ChessBoard potentialFutureBoard = board.clone();
                potentialFutureBoard.makeMove(m);
                ChessGame potentialFutureGame = new ChessGame();
                potentialFutureGame.setBoard(potentialFutureBoard);
                if(!potentialFutureGame.isInCheck(teamColor)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        HashSet<ChessPosition> teamPieces = board.getTeamPieces(teamColor);
        for (ChessPosition p : teamPieces){
            Collection<ChessMove> potentialMoves = validMoves(p);
            if(!potentialMoves.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean canCastle (){}

}
