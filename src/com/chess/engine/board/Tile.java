package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Tile {

    private final Position coordinate;

    private Tile(Position coord) {
        this.coordinate = coord;
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();

    public Position getPosition() {
        return this.coordinate;
    }

    public int getBoardIndex() {
        return getPosition().getRow() * 8 + getPosition().getColumn();
    }

    public static class EmptyTile extends Tile {
        public EmptyTile(Position coord) {
            super(coord);
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "[ ]";
        }
    }

    public static class OccupiedTile extends Tile {
        private Piece piece;

        public OccupiedTile(Position coord, Piece piece) {
            super(coord);
            this.piece = piece;
        }

        public Piece getPiece() {
            return this.piece;
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public String toString() {
            return "[" + piece.toString() + "]";
        }
    }
}
