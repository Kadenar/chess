package com.chess.ui;

import com.chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PieceUI extends JLabel {

    private final Piece piece;

    PieceUI(Piece piece) {
        super();
        this.piece = piece;
        try {
            BufferedImage myPicture = ImageIO.read(new File(piece.getPieceImagePath()));
            ImageIcon pieceIcon = new ImageIcon(myPicture);
            setIcon(pieceIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the piece that this ui is sourced from
     * @return the piece
     */
    public Piece getPiece() {
        return this.piece;
    }
}
