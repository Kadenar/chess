package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Piece extends JLabel {

    private final Player owner;
    private JLabel scaledImg;

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
     * Force subclasses to implement toString method
     * @return the {@code String} representation of this piece
     */
    public abstract String toString();

    /**
     * Create the possible moves for the given piece (to be implemented based on type of piece)
     * @param board the {@code Board} to generate moves for this piece
     * @param currentTile the current {@code Tile} of the piece
     * @return set of possible moves that are possible
     */
    abstract Set<Move> generateMoves(Board board, Tile currentTile);

    /**
     * Add the possible moves for the given piece to the board object
     * @param board the {@code Board} to add moves to
     * @param currentTile the {@code Tile} of the piece
     * @param turn the turn to add moves to the board for
     */
    public final void addMovesToBoard(Board board, Tile currentTile, int turn) {
        board.getMovesForTurn(turn, getOwner()).put(this, generateMoves(board, currentTile));
    }

    /**
     * Movement for all available directions (Up, Down, Left, Right, Diagonal Up, Diagonal Down)
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param dir the direction of movement (up, down, left, right)
     * @param isDiagonal whether diagonal movement
     * @return the positions that are valid to be moved to
     */
    final Set<Move> addPositionsForDirection(Board board, Piece piece, Tile currentTile,
                                             Direction dir, boolean isDiagonal) {
        Set<Move> positions = new HashSet<>();

        // Diagonal movement
        if(isDiagonal) {
            int rowOffset = dir == Direction.UP ? 1 : -1;
            positions.addAll(addPositionsForDiagonal(board, piece, currentTile, 1, rowOffset));
            positions.addAll(addPositionsForDiagonal(board, piece, currentTile, -1, rowOffset));
        }
        // Vertical up
        else if(dir == Direction.UP) {
            positions.addAll(addPositionsForVertical(board, piece, currentTile, 1));
        }
        // Vertical down
        else if(dir == Direction.DOWN) {
            positions.addAll(addPositionsForVertical(board, piece, currentTile, -1));
        }
        // Horizontal left
        else if(dir == Direction.LEFT) {
            positions.addAll(addPositionsForHorizontal(board, piece, currentTile, -1));
        }
        // Horizontal right
        else if(dir == Direction.RIGHT) {
            positions.addAll(addPositionsForHorizontal(board, piece, currentTile, 1));
        }

        return positions;
    }

    /**
     * Add positions for a diagonal in given x and y offset direction
     * @param piece the piece to determine positions for
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right)
     * @param rowOffset the row offset (up and down)
     * @return the positions that are valid to be moved to
     */
    private Set<Move> addPositionsForDiagonal(Board board, Piece piece, Tile currentTile,
                                              int colOffset, int rowOffset) {
        return addPositionsForOffset(board, piece, currentTile, colOffset, rowOffset);
    }

    /**
     * Add positions for a vertical in given y offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param rowOffset the row offset (up and down)
     * @return the positions that are valid to be moved to
     */
    private Set<Move> addPositionsForVertical(Board board, Piece piece,
                                              Tile currentTile, int rowOffset) {
        return addPositionsForOffset(board, piece, currentTile, 0, rowOffset);
    }

    /**
     * Add positions for a horizontal in given column offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right)
     * @return the positions that are valid to be moved to
     */
    private Set<Move> addPositionsForHorizontal(Board board, Piece piece,
                                                Tile currentTile, int colOffset) {
        return addPositionsForOffset(board, piece, currentTile, colOffset, 0);
    }

    /**
     * Add positions for a given offset
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right movement)
     * @param rowOffSet the row offset (up and down movement)
     * @return the positions that are valid to be moved to
     */
     final Set<Move> addPositionsForOffset(Board board, Piece piece, Tile currentTile,
                                           int colOffset, int rowOffSet) {
        Position currentPosition = currentTile.getPosition();
        boolean isPawn = piece instanceof Pawn;
        int maxSpacesMoved = piece.getMaxSpacesMoved();

        // Set max spaces to 1 for pawns not on home row
        if(isPawn && (currentPosition.getRow() != 1 && currentPosition.getRow() != 6)) {
            maxSpacesMoved = 1;
        }

        // While we haven't checked the max spaces allowed by this piece
        Set<Move> positionsSet = new HashSet<>();
        Map<Position, Tile> tiles = board.getTileMap();
        Position offSetPos = currentPosition;
        for(int tilesCounted = 0; tilesCounted < maxSpacesMoved; tilesCounted++) {

            // Get our next position based on our offset
            offSetPos = BoardUtils.getOffSetPosition(offSetPos, colOffset, rowOffSet);

            // Ensure offset position is a valid coordinate
            if(!offSetPos.isValidCoord()) {
                break;
            }

            // Get offset tile and check whether it is occupied
            Tile offSetTile = tiles.get(offSetPos);
            boolean offSetTileIsOccupied = offSetTile.isOccupied();
            Piece offSetTilePiece = offSetTile.getPiece();

            // If the offset tile is occupied by another piece
            if(offSetTileIsOccupied) {

                // Don't allow a pawn to be moved to another pawn's location unless it is a diagonal move
                // Don't allow a pawn to move diagonally if the pawn on that tile is the same owner
                if ((isPawn && colOffset == 0) || piece.sameSide(offSetTilePiece)) {
                    break;
                }

            }
            // If the offset tile is not occupied by another piece
            else {

                // If we are a pawn, don't allow diagonal movement unless en passant
                boolean enpassant = offSetPos.equals(board.getGameState().getEPSquare());
                if(isPawn && colOffset != 0 && !enpassant) {
                    break;
                }

            }

            // Add our position if it was not occupied or was an opposing piece and does not cause check
            positionsSet.add(new Move(piece, currentTile, offSetTilePiece, offSetTile));

            // Break if the tile was occupied as we can't go past an occupied tile
            if(offSetTileIsOccupied) {
                break;
            }
        }

        return positionsSet;
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
    public final boolean sameSide(Piece piece) {
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
