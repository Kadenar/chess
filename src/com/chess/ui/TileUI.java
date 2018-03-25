package com.chess.ui;

import com.chess.engine.board.Tile;

import javax.swing.*;
import java.awt.*;

public class TileUI extends JPanel {

    private Tile tile;
    private PieceUI thePiece = null;

    TileUI(Tile tile) {
        this.tile = tile;
        int boardIndex = (tile.getBoardIndex() + tile.getPosition().getRow()) % 2;
        setBackground( boardIndex == 0 ? Color.WHITE : Color.BLUE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // If the tile is occupied, add the image to it
        if(tile.isOccupied()) {
            thePiece = new PieceUI(tile.getPiece());
            addPieceToTile(thePiece, true);
        }
    }

    public PieceUI getPieceUI() {
        return this.thePiece;
    }

    public Tile getTile() {
        return this.tile;
    }

    public void addPieceToTile(PieceUI piece, boolean init) {

        // Get the tile we are adding the piece to
        Tile tileToAddPieceTo = this.tile;

        // If the destination tile is occupied by the opponent
        if(tileToAddPieceTo.isOccupied()
                && tileToAddPieceTo.getPiece().getOwner() != piece.getPiece().getOwner()) {

            // Remove previous piece from the UI
            removePieceFromTile(tileToAddPieceTo);

            // Add new piece to the UI
            add(piece);
        // Destination tile was not occupied or we are initializing game pieces
        } else if(!tileToAddPieceTo.isOccupied() || init){
            // Add new piece to the UI
            add(piece);
        }
    }

    public void removePieceFromTile(Tile tile) {
        // Remove the piece from the UI
        removeAll();

        if(tile.isOccupied()) {
            // Need to remove the piece from the backend as well
            //TODO
        }
    }
}
