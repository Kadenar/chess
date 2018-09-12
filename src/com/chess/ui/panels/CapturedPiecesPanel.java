package com.chess.ui.panels;

import com.chess.ChessConsts;
import com.chess.engine.pieces.Piece;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

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
        blackPieces.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT / 8));

        JSeparator separate = new JSeparator(SwingConstants.HORIZONTAL);

        whitePieces = new JPanel();
        whitePieces.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT / 8));

        this.add(blackPieces);
        this.add(separate);
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
