package com.chess.engine.board;

import com.chess.engine.Position;

public class BoardUtils {

    // This object can only reference one board at a time
    private BoardUtils() { /*Singleton*/ }

    /*
    * Return a position given a square index such as A8
    */
    static Position sqiToPosition(final String sqi) {
        if("-".equals(sqi)) {
            return null;
        }

        char colChar = sqi.charAt(0);
        int col = colChar - 97;
        char row = sqi.charAt(1);

        return new Position(Integer.parseInt(row + ""), col);
    }

    /**
     * Get a position with given row and column offset
     * @param origin the starting position
     * @param colOffset how many columns (left or right)
     * @param rowOffset how many rows (up or down)
     * @return the position with given offset
     */
    public static Position getOffSetPosition(Position origin, int colOffset, int rowOffset) {
        return new Position(origin.getRow() + rowOffset, origin.getColumn() + colOffset);
    }

    /*
    * Get difference in rows
    */
    public static int deltaRow(final Tile pos1, final Tile pos2) {
        return Math.abs(pos1.getPosition().getRow() - pos2.getPosition().getRow());
    }

    /*
     * Get difference in cols
     */
    public static int deltaCol(final Tile pos1, final Tile pos2) {
        return Math.abs(pos1.getPosition().getColumn() - pos2.getPosition().getColumn());
    }
}