package com.chess.engine.board;

import com.chess.engine.Position;
import com.chess.engine.pieces.Piece;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;

public class Tile extends JPanel {

    private final Position coordinate;
    private Piece piece;
    private boolean isHighlighted = false;

    // Empty tile
    public Tile(Position pos) {
        this(pos, null);
    }

    // Tile with potential piece
    public Tile(Position pos, Piece piece) {
        super();
        this.coordinate = pos;
        highlightTile(false, null);
        setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        setPiece(piece);
    }

    /**
     * Highlight the given tile
     * @param highlight whether the tile should be highlighted
     */
    void highlightTile(boolean highlight, Color color) {
        if(highlight) {
            setBackground(color);
            isHighlighted = true;
        } else {
            setBackground(isLight() ? Color.WHITE : Color.GRAY);
            isHighlighted = false;
        }
    }

    /**
     * Determine whether this tile is currently highlighted
     * @return {@code true} if tile is highlighted, {@code false} if it is not
     */
    boolean isHighlighted() {
        return this.isHighlighted;
    }

    /**
     * Get the tile's position
     * @return returns the position of the tile
     */
    public Position getPosition() {
        return this.coordinate;
    }

    /**
     * Whether this tile is occupied
     * @return if there is a piece, it is occupied
     */
    public boolean isOccupied() {
        return getPiece() != null;
    }

    /**
     * Get the piece for this tile
     * @return the piece on this tile, or null if there isn't one
     */
    public Piece getPiece() {
        return this.piece;
    }

    /**
     * Put a piece on this tile and if not null add it as a component as well
     * @param piece the piece to add, or null
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Return whether the given tile is a light or dark square
     * @return is this tile light or dark
     */
    private boolean isLight() {
        return (getPosition().getRow() % 2) == (getPosition().getColumn() % 2);
    }

    /**
     * Ensure unique hashcode for tiles based on tile position
     * @return unique hash
     */
    @Override
    public int hashCode() {
        return getPosition().hashCode();
    }

    /**
     * Returns if given tile is the same as passed in tile
     * @param other the other tile to check
     * @return true if same tile, false if not
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof  Tile)) return false;
        Tile tile = (Tile) other;
        return this.getPosition().equals(tile.getPosition());
    }

    /**
     * The piece on the tile or just empty brackets if not occupied
     * @return string representation of this tile
     */
    @Override
    public String toString() {
        return isOccupied() ? "[" + getPiece().toString() + "]" : "[ ]";
    }
}
