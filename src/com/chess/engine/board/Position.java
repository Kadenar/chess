package com.chess.engine.board;

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

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean equals(Position other){
        return (this.row == other.row) && (this.column == other.column);
    }

    public boolean isValidCoord() {
        return this.row >= 0 && this.row < 8 && this.column >= 0 && this.column < 8;
    }

    public int getTileCoord() {
        return row * 8 + column;
    }

    /**
     * 97 = a
     * 98 = b
     * 99 = c
     * 100 = d
     * 101 = e
     * 102 = f
     * 103 = g
     * 104 = h
     */
    public char getColumnChar() {
        return (char) (this.column + 97);
    }

    @Override
    public String toString() {
        return getColumnChar() + "" + (getRow() + 1);
        //return "[" + getRow() + ", " + getColumn() + "]";
    }
}