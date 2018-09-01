package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MoveUtils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Piece extends JLabel {

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
     * Create the possible moves for the given piece (to be implemented based on type of piece)
     * @param currentTile the current tile of the piece
     * @return list of possible moves that are possible
     */
    public abstract Set<Move> generateMoves(Board board, Tile currentTile);

    /**
     * Force subclasses to implement toString method
     * @return the string representation of this piece
     */
    abstract public String toString();

    /**
     * Get all moves for this piece
     * @return get list of moves for this piece
     */
    public Set<Move> getMoves() {
        return getOwner().getMovesForPieces().getOrDefault(this, new HashSet<>());
    }

    /**
     * Get only valid moves for this piece
     * (preventing the piece from actually moving if it would put the Player in check)
     * @param board the current board state
     * @return the list of valid moves for this piece
     */
    public Set<Move> getValidMoves(Board board) {
        // TODO -> Reenable this once fixing castling
        return getMoves()/*.stream()
            .filter(move -> MoveUtils.performTestMove(board, move.getOrigin(), move.getDestination()))
            .collect(Collectors.toSet())*/;
    }

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

    /**
     * Scaled down image of the piece
     * @return the scaled down image as a JLabel for the given piece
     */
    public final JLabel getScaledImg() {
        return this.scaledImg;
    }
}
