package com.chess.ui;

import com.chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PieceUI extends JLabel implements ActionListener{
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

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
    }
}
