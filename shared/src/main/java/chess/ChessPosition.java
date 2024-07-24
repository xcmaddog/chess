package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return boolean representing whether or not the position is on the board
     */
    public boolean isOnBoard(){
        int row = getRow();
        int col = getColumn();
        return row <= 8 && row >= 1 && col <= 8 && col >= 1;
    }

    public ChessPosition getNextSquare(int rowAdd, int colAdd){
        return new ChessPosition(getRow()+rowAdd, getColumn()+colAdd);
    }

    /*
     * overriding the equals() and hashCode() functions
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        boolean result = row == that.row && col == that.col;
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString(){
        return String.format("[%d,%d]",getRow(),getColumn());
    }
}
