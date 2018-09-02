package com.chess.ui.panels;

import com.chess.engine.pieces.Piece;
import com.chess.ui.UIConstants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CapturedPanel extends JPanel {

    private final JPanel blackPieces;
    private final JPanel whitePieces;

    CapturedPanel() {
        super(new BorderLayout());
        blackPieces = new JPanel(new GridLayout(4, 4));
        blackPieces.setPreferredSize(new Dimension(UIConstants.HISTORY_WIDTH, UIConstants.HISTORY_HEIGHT / 2));
        whitePieces = new JPanel(new GridLayout(4, 4));
        whitePieces.setPreferredSize(new Dimension(UIConstants.HISTORY_WIDTH, UIConstants.HISTORY_HEIGHT / 2));
        this.add(blackPieces, 0);
        this.add(whitePieces, 1);

        // Set border for panel
        //Border border = new EtchedBorder(EtchedBorder.RAISED);
        TitledBorder border = new TitledBorder("Captured");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        this.setBorder(border);

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
}
