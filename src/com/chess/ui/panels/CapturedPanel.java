package com.chess.ui.panels;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class CapturedPanel extends JPanel {

    private final JPanel blackPieces;
    private final JPanel whitePieces;

    CapturedPanel() {
        super(new BorderLayout());
        blackPieces = new JPanel(new GridLayout(8, 2));
        whitePieces = new JPanel(new GridLayout(8, 2));

        // Set border for panel
        this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        // add black and white piece holders inside
        this.add(blackPieces, BorderLayout.NORTH);
        this.add(whitePieces, BorderLayout.SOUTH);
    }

    void addCaptured(Piece piece) {
        JLabel scaledImg = piece.getScaledImg();
        // Add black pieces to white panel and white pieces to black panel
        if(piece.getOwner().isWhite()) {
            blackPieces.add(scaledImg);
        } else {
            whitePieces.add(scaledImg);
        }

        repaint();
    }
}
