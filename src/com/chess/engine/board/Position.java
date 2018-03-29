package com.chess.engine.board;

import java.awt.*;

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

    public Position getOffSetPosition(int x, int y) {
        return new Position(this.getRow() + y, this.getColumn() + x);
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean isValidCoord() {
        return this.row >= 0 && this.row < 8 && this.column >= 0 && this.column < 8;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Position)) return false;
        Position pos = (Position) other;
        return this.row == pos.row && this.column == pos.column;
    }

    @Override
    public String toString() {
        return (char)(this.column + 97) + "" + (getRow() + 1);
    }
}