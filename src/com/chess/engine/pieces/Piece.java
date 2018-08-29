package com.chess.engine.pieces;

import com.chess.engine.Move;
import com.chess.engine.board.Player;
import com.chess.engine.board.Tile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class Piece extends JLabel{

    private final Player owner;
    private final JLabel scaledImg;

    public Piece(Player color, String pieceImagePath) {
        super();
        this.owner = color;
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

    /**
     * Scaled down image of the piece
     * @return the scaled down image as a JLabel for the given piece
     */
    public final JLabel getScaledImg() {
        return scaledImg;
    }

    /**
     * The list of possible moves for a given piece
     * @param currentPosition the current tile the piece resides on
     * @return list of possible moves
     */
    public final List<Move> getMoves(Tile currentPosition) {
        return createPossibleMoves(currentPosition);
    }

    /**
     * Create the possible moves for the given piece (to be implemented based on type of piece)
     * @param currentPosition the current position
     * @return list of possible moves that are possible
     */
    abstract List<Move> createPossibleMoves(Tile currentPosition);

    /**
     * Force subclasses to implement toString method
     * @return the string representation of this piece
     */
    abstract public String toString();

    /**
     * Get the owner of the piece
     * @return White or Black
     */
    public final Player getOwner() {
        return this.owner;
    }

    /**
     * Whether a piece is on the same side as another piece
     * @return true if same player, false if not
     */
    public boolean sameSide(Piece piece) {
        return piece.getOwner().equals(getOwner());
    }

    /**
     * The standard maximum number of spaces
     * a piece can move by default unless overridden
     * @return 8 spaces
     */
    public int getMaxSpacesMoved() {
        return 8;
    }
}
