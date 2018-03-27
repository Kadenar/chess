package com.chess.ui;

import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Tile.EmptyTile;
import com.chess.engine.board.Tile.OccupiedTile;
import com.chess.engine.utils.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

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

    public boolean equals(TileUI tileUI) {
        return this.getTile().getPosition().equals(tileUI.getTile().getPosition());
    }

    /**
     * Move the given piece to the current tile
     * @param fromTile the tile which we are moving from
     */
    public void movePieceToTile(TileUI fromTile) {

        // If the destination tile is not occupied or is an opponent's piece
        if(!getTile().isOccupied()
            || getTile().getPiece().getOwner() != fromTile.getTile().getPiece().getOwner()) {
            // Replace the opponents piece with our piece
            this.updatePieceOnTile(fromTile.getPieceUI());

            // Remove dragged piece from original tile
            fromTile.updatePieceOnTile(null);
        }
    }

    private void updatePieceOnTile(PieceUI piece) {

        // Remove the old piece from the UI
        if(getComponents().length > 0) {
            remove(0);
        }

        // Need to remove the piece from the backend as well and replace with an empty tile
        Position origPos = tile.getPosition();
        String origPosStr = origPos.toString();
        Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();

        // If we replacing tile with nothing, remove old piece and replace tile with empty tile
        if(piece == null) {
            EmptyTile newEmpty = new EmptyTile(origPos);
            tiles.put(origPosStr, newEmpty);
            this.tile = newEmpty;
            this.thePiece = null;
        } else { // Otherwise...

            // Add the new piece in the UI
            add(piece);

            // Update pointer for the PieceUI
            this.thePiece = piece;

            // If the tile was not previously occupied, then mark it as occupied now
            if(!tiles.get(origPosStr).isOccupied()) {
                OccupiedTile occupied = new OccupiedTile(origPos, piece.getPiece());
                tiles.put(origPosStr, occupied);
                this.tile = occupied;
            } else { // Otherwise, just update the piece on the tile in our board
                tiles.get(origPosStr).setPiece(piece.getPiece());
                // Update the actual piece object itself
                getTile().setPiece(piece.getPiece());
            }
        }
    }
}