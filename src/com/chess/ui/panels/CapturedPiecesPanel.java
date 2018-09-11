package com.chess.ui.panels;

import com.chess.engine.pieces.Piece;
import com.chess.ui.UIConstants;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

class CapturedPiecesPanel extends JPanel {

    private final JPanel blackPieces;
    private final JPanel whitePieces;

    CapturedPiecesPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        TitledBorder border = new TitledBorder("Captured");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        this.setBorder(border);

        blackPieces = new JPanel();
        blackPieces.setPreferredSize(new Dimension(UIConstants.HISTORY_WIDTH, UIConstants.HISTORY_HEIGHT / 8));

        whitePieces = new JPanel();
        whitePieces.setPreferredSize(new Dimension(UIConstants.HISTORY_WIDTH, UIConstants.HISTORY_HEIGHT / 8));

        this.add(blackPieces);
        this.add(whitePieces);
    }

    /**
     * Add a piece to the captured panel
     * @param piece the piece to be added
     */
    void addCaptured(Piece piece) {
        JLabel scaledImg = piece.getScaledImg();
        if(piece.getOwner().isWhite()) {
            blackPieces.add(scaledImg);
        } else {
            whitePieces.add(scaledImg);
        }
    }

    /**
     * Reset captured pieces
     */
    void reset() {
        blackPieces.removeAll();
        whitePieces.removeAll();
    }
}