package com.chess.engine;

import java.awt.*;
import java.util.Objects;

/*
 * Represents a piece position on the chessboard
 */
public class Position {
    private int row;
    private int column;

    public Position(int row, int column){
        this.row = row;
        this.column = column;
    }

    public Position(Position other){
        this.row = other.row;
        this.column = other.column;
    }

    public Position(Point point) {
        this.row = point.y;
        this.column = point.x;
    }

    /**
     * The position's column (File)
     * @return what column (file) this position is
     */
    public int getColumn() {
        return column;
    }

    /**
     * The position's row (Rank)
     * @return what row (rank) this position is
     */
    public int getRow() {
        return row;
    }

    /**
     * True if the position is a valid chess coordinate (A-H) and (0-7)
     * @return true if position is a valid chess coordinate
     */
    public boolean isValidCoord() {
        return this.row >= 0 && this.row < 8 && this.column >= 0 && this.column < 8;
    }

    /**
     * Whether a given position is a promotion square. Used for pawns.
     * @param player the player to check for
     * @return true if a promotion square, false if not
     */
    public boolean isPromotionSquare(Player player) {

        // If player is white, promotion row is 8
        if(player.isWhite()) {
            return getRow() == 7;
        }

        return getRow() == 0;
    }

    /**
     * Ensure unique hashcode for Positions based on row / column
     * @return unique hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * Compare the current position's row and column to another position
     * @param other the other position to compare
     * @return true if the given object is a position with matching row and column
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Position)) return false;
        Position pos = (Position) other;
        return this.row == pos.row && this.column == pos.column;
    }

    /**
     * Column (A-H) and Row (1-8) tile representation
     * @return string representation of the given position
     */
    @Override
    public String toString() {
        return (char)(this.column + 97) + "" + (getRow() + 1);
    }
}