package chess;

public class RookMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public ChessPosition[] pieceMoves(){
        ChessPosition square = new ChessPosition(1,1);//for now just returns the bottom left corner
        return new ChessPosition[]{square};
    }
}
