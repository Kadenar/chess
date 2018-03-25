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
        this.piece = piece;
        BufferedImage myPicture;
        try {
            myPicture = ImageIO.read(new File(piece.getPieceImagePath()));
            ImageIcon pieceIcon = new ImageIcon(myPicture);
            setIcon(pieceIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Piece getPiece() {
        return this.piece;
    }
}
