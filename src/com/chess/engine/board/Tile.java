package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Tile {

    private final Position coordinate;

    private Tile(Position pos) {
        this.coordinate = pos;
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();
    public abstract void setPiece(Piece piece);

    /**
     * Get the tile's position
     * @return returns the position of the tile
     */
    public Position getPosition() {
        return this.coordinate;
    }

    /**
     * Returns if given tile is the same as passed in tile
     * @param tile the tile to check
     * @return true if same tile, false if not
     */

    @Override
    public boolean equals(Object tile) {
        if(!(tile instanceof  Tile)) return false;
        Tile t = (Tile) tile;
        return this.getPosition().equals(t.getPosition());
    }

    /**
     * Return whether the given tile is a light or dark square
     * @return is this tile light or dark
     */
    public boolean isLight() {
        return (getPosition().getRow() % 2) == (getPosition().getColumn() % 2);
    }

    /**
     * Represents an empty tile with no piece on it
     */
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
        public void setPiece(Piece p) { }

        @Override
        public String toString() {
            return "[ ]";
        }
    }

    /**
     * Represents an occupied tile with a given piece on it
     */
    public static class OccupiedTile extends Tile {
        private Piece piece;

        public OccupiedTile(Position pos, Piece piece) {
            super(pos);
            this.piece = piece;
        }

        @Override
        public Piece getPiece() {
            return this.piece;
        }

        @Override
        public void setPiece(Piece piece) {
            this.piece = piece;
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
