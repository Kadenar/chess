package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import javax.swing.*;
import java.awt.*;

public class Tile extends JPanel {

    private final Position coordinate;
    private Piece piece;

    public Tile(Position pos) {
        this(pos, null);
    }

    public Tile(Position pos, Piece piece) {
        super();
        this.coordinate = pos;
        this.piece = piece;
        highlightTile(false);
        setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));

        add(new JLabel(getPosition() + " - [" + getPosition().getRow() + "," + getPosition().getColumn() + "]"));
        // If the piece isn't null, add it
        if(piece != null) {
            add(piece);
        }
    }

    /**
     * Highlight the given tile
     * @param highlight whether the tile should be highlighted
     */
    public void highlightTile(boolean highlight) {
        if(highlight) {
            setBackground(Color.MAGENTA);
        } else {
            setBackground(isLight() ? Color.WHITE : Color.GRAY);
        }
    }

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
    private boolean isLight() {
        return (getPosition().getRow() % 2) == (getPosition().getColumn() % 2);
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

        // If we are putting a piece on the tile, add it to the UI as well as the backend tile
        if(piece != null) {
            this.add(piece);
        }
    }

    @Override
    public String toString() {
        return isOccupied() ? "[" + getPiece().toString() + "]" : "[ ]";
    }
}
