package com.chess.engine.moves;

import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;

import java.util.Objects;

public class Move {

    private final Piece moved;
    private final Piece captured;
    private final Tile fromTile;
    private final Tile toTile;
    private final boolean promotion;

    public Move(final Move move) {
        this(move.moved, move.fromTile, move.captured, move.toTile, move.promotion);
    }
    public Move(final Piece piece, final Tile from, final Piece pieceCaptured, final Tile to) { this(piece, from, pieceCaptured, to, false); }
    public Move(final Piece pieceMoved, final Tile from, final Piece pieceCaptured, final Tile to, final boolean promote) {
        this.moved = pieceMoved;
        this.fromTile = from;
        this.captured = pieceCaptured;
        this.toTile = to;
        this.promotion = promote;
    }

    /**
     * Get the piece that is being moved
     * @return the piece being moved
     */
    public Piece getMovedPiece() { return this.moved; }

    /**
     * Get the piece that was captured from this move
     * @return the piece that was captured, or null if there was no capture
     */
    public Piece getCapturedPiece() { return this.captured; }

    /**
     * Get the origin tile the move comes from
     * @return the Tile to move from
     */
    public Tile getOrigin() { return this.fromTile; }

    /**
     * Get destination tile the move goes to
     * @return the Tile to move to
     */
    public Tile getDestination() {
        return this.toTile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromTile.hashCode(), toTile.hashCode());
    }

    /**
     * Check whether a given move is the same as another
     * @param other the other move to compare against
     * @return if the origin and destination tiles are the same
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Move)) return false;
        Move mov = (Move) other;
        return this.fromTile.equals(mov.fromTile) && this.toTile.equals(mov.toTile);
    }

    /**
     * String representation of the destination position
     * @return the destination tile's position as a string
     */
    @Override
    public String toString() {
        return getMovedPiece() + ": "
                + getOrigin().getPosition().toString()
                + " -> " + getDestination().getPosition().toString();
    }
}
