package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class Piece extends JLabel{

    private final Player owner;
    private boolean hasMoved;
    private final JLabel scaledImg;

    public Piece(Player color, String pieceImagePath) {
        this.owner = color;
        this.hasMoved = false;
        JLabel testImg = null;

        try {
            BufferedImage myPicture = ImageIO.read(new File("images/" + color.toString() + pieceImagePath));
            ImageIcon pieceIcon = new ImageIcon(myPicture);
            setIcon(pieceIcon);
            testImg = new JLabel(new ImageIcon(pieceIcon.getImage().getScaledInstance(
                    pieceIcon.getIconWidth() - 30, pieceIcon.getIconWidth() - 30, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scaledImg = testImg;
    }

    public JLabel getScaledImg() {
        return scaledImg;
    }

    // Methods for override in subclasses
    abstract public List<Position> createPossibleMoves(Position currentPosition);
    abstract public String toString();

    // Check whether this piece has moved
    boolean hasMoved() {
        return this.hasMoved;
    }

    // Set whether this piece has moved
    public void setHasMoved(boolean moved) {
        if(this.hasMoved) {
            return; // Don't allow setting back to false for now...
        }

        this.hasMoved = moved;
    }

    // Get owner of the piece (White or Black)
    public Player getOwner() {
        return this.owner;
    }

    /// Get possible moves
    public List<Position> getMoves(Position currPosition) {
        return createPossibleMoves(currPosition);
    }

    // Overridden for pawn, knight and king
    public int getMaxSpacesMoved() {
        return 8;
    }
}
