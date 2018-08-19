package com.chess.ui;

import com.chess.engine.board.Player;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

class CapturedPanel extends JPanel {

    private JPanel blackPieces;
    private JPanel whitePieces;

    CapturedPanel() {
        super(new BorderLayout());
        blackPieces = new JPanel(new GridLayout(8, 2));
        whitePieces = new JPanel(new GridLayout(8, 2));

        // Set border for panel
        this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        // add black and white piece holders inside
        this.add(blackPieces, BorderLayout.NORTH);
        this.add(whitePieces, BorderLayout.SOUTH);

        addCaptured(new Pawn(Player.WHITE));
        addCaptured(new Pawn(Player.BLACK));
        addCaptured(new Pawn(Player.WHITE));
        addCaptured(new Knight(Player.WHITE));
        addCaptured(new Knight(Player.BLACK));
        addCaptured(new Rook(Player.WHITE));
    }

    private void addCaptured(Piece piece) {
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
