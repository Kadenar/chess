package com.chess.engine.moves;

import com.chess.engine.pieces.Piece;

import javax.swing.JPanel;
import java.awt.Color;

class PromotionSelectionTile extends JPanel {

    private final Piece piece;

    PromotionSelectionTile(Piece piece) {
        super();
        this.piece = piece;
        this.add(piece);
    }

    void toggle(boolean enabled) {
        setBackground(enabled ? Color.BLUE : null);
    }

    Piece getPiece() {
        return this.piece;
    }
}
