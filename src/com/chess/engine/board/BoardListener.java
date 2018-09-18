package com.chess.engine.board;

import com.chess.engine.Position;
import com.chess.engine.moves.MoveUtils;
import com.chess.engine.pieces.Piece;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handle mouse interactions with pieces on the board
 */
class BoardListener implements MouseListener, MouseMotionListener {

    private Tile originatingTile = null;
    private Piece originatingPiece = null;
    private int xAdjustment, yAdjustment;
    private final Board board;

    BoardListener(Board board) {
        super();
        this.board = board;
    }

    /**
     * Add or remove indicators to the UI for tiles we can move to
     * @param enabled {@code true} to highlight the tiles, and {@code false} to remove highlighting
     */
    private void toggleIndicators(boolean enabled) {
        board.getValidMovesForPiece(board.getGameState().getFullMoves(), originatingPiece)
                .forEach(move -> move.getDestination().highlightTile(enabled, new Color(135,206,235)));
    }

    /**
     * Prevent Player from moving a piece if it is not their turn
     * @return whether the {@code Player} can pickup a given piece
     */
    private boolean canPickupPiece() {
        return originatingPiece != null && originatingPiece.getOwner().equals(board.getGameState().getPlayerTurn());
    }

    /*
     * Get the tile position based on the location of the mouse relative to the board
     */
    private Position getTilePositionFromMouse() {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, board);
        int tile_x_pos = mouse.x / (board.getWidth() / 8);
        int tile_y_pos = mouse.y / (board.getHeight() / 8) + 1;
        tile_y_pos = Math.abs(8 - tile_y_pos);
        return new Position(new Point(tile_x_pos, tile_y_pos));
    }

    /**
     * When the mouse is pressed, determine the originating tile of the click
     * Then check if a piece is present and if so, ensure it is my turn
     * If it is my turn, add indicators for where I can move to
     */
    @Override
    public void mousePressed(MouseEvent evt) {

        // Get mouse press location in tile coordinate system
        this.originatingTile = board.getTileMap().getOrDefault(getTilePositionFromMouse(), null);

        // If user clicked on a piece
        if(this.originatingTile != null) {

            // If we have a piece, add it to our layered pane to drag it around
            originatingPiece = originatingTile.getPiece();
            if(canPickupPiece()) {
                Point origLoc = originatingTile.getLocation();
                originatingPiece.setLocation(origLoc.x + 10, origLoc.y + 5);
                board.getLayeredPane().add(originatingPiece, JLayeredPane.DRAG_LAYER);

                // Set adjustments for user when dragging
                xAdjustment = origLoc.x - evt.getX();
                yAdjustment = origLoc.y - evt.getY();

                // Add indicators for possible moves
                toggleIndicators(true);
            }
        }
    }

    /**
     * When dragging my mouse, if I have a piece picked up, move it around on screen
     */
    @Override
    public void mouseDragged(MouseEvent evt) {

        // If we have a piece, then drag it with the mouse
        if(canPickupPiece()) {
            // Calculate x-coords - prevent going too far left or right
            int xLoc = evt.getX() + xAdjustment;
            int xMax = board.getLayeredPane().getWidth() - originatingPiece.getWidth();
            xLoc = Math.min(xLoc, xMax);
            xLoc = Math.max(xLoc, 0);

            // Calculate y-coords - prevent going too far up or down
            int yLoc = evt.getY() + yAdjustment;
            int yMax = board.getLayeredPane().getHeight() - originatingPiece.getHeight();
            yLoc = Math.min(yLoc, yMax);
            yLoc = Math.max(yLoc, 0);

            // Update location of the piece
            originatingPiece.setLocation(xLoc, yLoc);
        }
    }

    /**
     * When releasing the mouse, remove highlighted indicators
     * Then determine if a piece was picked up
     * - If it was and we could not complete the move, then restore it to original tile
     * - Otherwise, it will be moved, board reloaded and the history panel will be updated
     */
    @Override
    public void mouseReleased(MouseEvent evt) {

        // If we had a piece being dragged
        if(canPickupPiece()) {

            // Remove the indicators from tiles
            toggleIndicators(false);

            // If the piece was not moved, then restore it to previous position
            Tile draggedToTile = board.getTileMap().getOrDefault(getTilePositionFromMouse(), null);
            if (!MoveUtils.executeActualMove(board, originatingTile, draggedToTile, false)) {
                // If we had a piece that we attempted to move from a tile
                // Remove it from our layered pane and add it back to the originating tile
                originatingPiece.setVisible(false);
                board.getLayeredPane().remove(originatingPiece);
                originatingTile.add(originatingPiece);
                originatingPiece.setVisible(true);
            } else {
                // Re-display the with latest updates after move is executed
                //originatingPiece.setVisible(false);
                board.getLayeredPane().remove(originatingPiece);
                board.displayBoard();
                //originatingPiece.setVisible(true);
            }
        }
    }

    /*
     * No other mouse events necessary
     */

    @Override
    public void mouseClicked(MouseEvent evt) {}

    @Override
    public void mouseMoved(MouseEvent evt) { }

    @Override
    public void mouseEntered(MouseEvent evt) { }

    @Override
    public void mouseExited(MouseEvent evt) { }
}
