package com.chess.ui;

import com.chess.engine.board.Tile;

import javax.swing.*;
import java.awt.*;

public class TileUI extends JPanel {

    private Tile tile;
    private PieceUI thePiece = null;

    TileUI(Tile tile) {
        super();
        this.tile = tile;
        setBackground(tile.isLight() ? Color.WHITE : Color.BLUE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        //add(new JLabel(tile.getPosition() + " - [" + tile.getPosition().getRow() + "," + tile.getPosition().getColumn() + "]"));

        // If the tile is occupied, add the image to it
        if(tile.isOccupied()) {
            thePiece = new PieceUI(tile.getPiece());
            add(thePiece);
        }
    }

    /**
     * Get the ui representation of a piece on this tile
     * @return the ui representation of a piece
     */
    public PieceUI getPieceUI() {
        return this.thePiece;
    }

    /**
     * Get the tile that ui is sourced from
     * @return the backend tile containing the piece
     */
    public Tile getTile() {
        return this.tile;
    }

    /**
     * If given tile is the same position as current tile
     * @param tileUI the other tile to check
     * @return true if same position, false otherwise
     */
    public boolean isSameTile(TileUI tileUI) {
        return this.getTile().getPosition().equals(tileUI.getTile().getPosition());
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setPieceUI(PieceUI piece) {
        this.thePiece = piece;
    }
}