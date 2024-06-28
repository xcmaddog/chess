package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(){
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        ChessPosition aPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
        while (aPosition.isOnBoard()){
            result.add(new ChessMove(myPosition,aPosition));
            aPosition = new ChessPosition(aPosition.getRow()+1, aPosition.getColumn()+1);
        }
        aPosition = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()+1);
        while (aPosition.isOnBoard()){
            result.add(new ChessMove(myPosition,aPosition));
            aPosition = new ChessPosition(aPosition.getRow()-1, aPosition.getColumn()+1);
        }
        aPosition = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()-1);
        while (aPosition.isOnBoard()){
            result.add(new ChessMove(myPosition,aPosition));
            aPosition = new ChessPosition(aPosition.getRow()-1, aPosition.getColumn()-1);
        }
        aPosition = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()-1);
        while (aPosition.isOnBoard()){
            result.add(new ChessMove(myPosition,aPosition));
            aPosition = new ChessPosition(aPosition.getRow()+1, aPosition.getColumn()-1);
        }
        return result;
    }
}
