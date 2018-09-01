package com.chess.engine.moves;

import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;

public class Move {

    private final Piece piece;
    private final Tile fromTile;
    private final Tile toTile;
    private final boolean promotion;

    public Move(final Move move) {
        this(move.piece, move.fromTile, move.toTile, move.promotion);
    }
    public Move(final Piece piece, final Tile from, final Tile to) { this(piece, from, to, false); }
    public Move(final Piece piece, final Tile from, final Tile to, final boolean promote) {
        this.piece = piece;
        this.fromTile = from;
        this.toTile = to;
        this.promotion = promote;
    }

    /**
     * Get the piece that is being moved
     * @return the piece being moved
     */
    public Piece getMovedPiece() { return this.piece; }

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
        int result = 17;
        return 31 * result + (fromTile.hashCode() + toTile.hashCode());
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
