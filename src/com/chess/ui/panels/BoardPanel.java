package com.chess.ui.panels;

import com.chess.engine.GameSettings;
import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.SoundUtils;
import com.chess.ui.ChessFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel {

    private final JLayeredPane layeredPane;
    private Board board;

    public BoardPanel(Board board) {
        super();

        // Create layered pane for dragging purposes
        this.layeredPane = new JLayeredPane();
        // TODO need to figure out how to add layout here for resizing
        //layeredPane.setLayout(new FlowLayout());
        Border border = BorderFactory.createLineBorder(Color.BLUE, 5);
        this.layeredPane.setBorder(border);

        // Set our board's layout to an 8x8 grid
        this.setLayout(new GridLayout(8, 8));
        this.setBounds(0, 0, ChessFrame.WINDOW_HEIGHT-60, ChessFrame.WINDOW_HEIGHT-80);

        // TODO Remove border
        border = BorderFactory.createLineBorder(Color.RED, 5);
        this.setBorder(border);

        // Create the chess tiles
        PieceListener listener = new PieceListener();
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);

        // Reload the tiles
        reloadBoard(board);

        // Add the myself to our layered pane's default layer
        this.layeredPane.add(this, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * Initialize the game board
     * - All chess tiles
     */
    public void reloadBoard(Board board) {
        this.board = board;

        // Add all the chess tiles to the UI
        int i = 0;
        for (Map.Entry<String, Tile> entry : board.getTileMap().entrySet()) {
            // Add the individual tiles to the board in the center
            this.add(entry.getValue(), i);
            i++;
        }

        SoundUtils.playMoveSound("startGame");
    }

    /*
     * Mouse coordinates are the inverse of the tile coordinates
     * So we need to take the absolute value of 8 - mouse coordinate
     */
    private Point getTilePositionFromMouse(MouseEvent evt) {
        //Point mouse = evt.getPoint();
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, this);
        int tile_x_pos = (mouse.x / (this.getWidth() / 8) - 1);
        int tile_y_pos = mouse.y / (this.getHeight() / 8);
        tile_y_pos = Math.abs(8 - tile_y_pos);
        return new Point(tile_x_pos, tile_y_pos);
    }

    /**
     * Move originating piece to dragged to tile location
     * @param originatingTile the originating tile on mouse press
     * @return true if the piece was moved, false if not
     */
    private boolean attemptPieceMove(MouseEvent evt, Tile originatingTile) {
        Position draggedToPosition = new Position(getTilePositionFromMouse(evt));
        Tile draggedToTile = board.getTileMap().getOrDefault(draggedToPosition.toString(), null);

        // Attempt to move the piece on the originating tile to the new tile
        return MoveUtils.executeMove(this.getBoard(), originatingTile, draggedToTile);
    }

    /**
     * Get the board that we are representing
     * @return the board object that is being displayed
     */
    public Board getBoard() {
        return this.board;
    }

    public JLayeredPane getLayeredPane() {
        return this.layeredPane;
    }

    /**
     * Handle mouse interactions with pieces
     */
    class PieceListener implements MouseListener, MouseMotionListener {
        private Tile originatingTile = null;
        private Piece originatingPiece = null;
        private int xAdjustment, yAdjustment;
        private List<Tile> targetMoveTiles = new ArrayList<>();
        JPanel printOut = new JPanel();
        JLabel label = new JLabel();

        PieceListener() {
            super();
            layeredPane.add(printOut, JLayeredPane.DRAG_LAYER);
        }

        /**
         * Add indicators to the UI for tiles we can move to
         */
        private void addIndicators() {
            if(GameSettings.getInstance().isEnableHighlighting()) {
                List<Move> validMoves = originatingPiece.getMoves();

                for (Move validMove : validMoves) {
                    Tile destination = validMove.getDestination();
                    targetMoveTiles.add(destination);
                    destination.highlightTile(true);
                }

                layeredPane.revalidate();
                layeredPane.repaint();
            }
        }

        /**
         * Remove all indicators from the layered pane
         */
        private void removeIndicators() {
            if(GameSettings.getInstance().isEnableHighlighting()) {
                for (Tile targetTile : targetMoveTiles) {
                    targetTile.highlightTile(false);
                }
            }
        }

        /**
         * Prevent player from moving a piece if it is not their turn
         * @return whether the player can pickup a given piece
         */
        private boolean canPickupPiece() {
            return originatingPiece != null && originatingPiece.getOwner().equals(GameState.getInstance().getPlayerTurn());
        }


        @Override
        public void mousePressed(MouseEvent evt) {

            // Clear out lists
            targetMoveTiles.clear();

            // Get mouse press location in tile coordinate system
            Position mousePressPosition = new Position(getTilePositionFromMouse(evt));
            this.originatingTile = board.getTileMap().getOrDefault(mousePressPosition.toString(), null);

            // If user clicked on a piece
            if(this.originatingTile != null) {

                originatingTile.highlightTile(true); // TODO remove this
                originatingPiece = originatingTile.getPiece();

                // If we have a piece, add it to our layered pane to drag it around
                if(canPickupPiece()) {
                    Point origLoc = originatingTile.getLocation();
                    originatingPiece.setLocation(origLoc.x + 2, origLoc.y - 2);
                    layeredPane.add(originatingPiece, JLayeredPane.DRAG_LAYER);

                    // Set adjustments for user when dragging
                    xAdjustment = origLoc.x - evt.getX();
                    yAdjustment = origLoc.y - evt.getY();

                    // Add indicators for possible moves
                    addIndicators();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent evt) {

            // Remove the indicators from tiles
            removeIndicators();

            // TODO remove this
            if(originatingTile != null)
                originatingTile.highlightTile(false);

            // If the piece was not moved, then restore it to previous position
            if(canPickupPiece() && !attemptPieceMove(evt, this.originatingTile)) {
                // If we had a piece that we attempted to move from a tile
                // Remove it from our layered pane and add it back to the originating tile
                originatingPiece.setVisible(false);
                layeredPane.remove(originatingPiece);
                originatingTile.add(originatingPiece);
                originatingPiece.setVisible(true);
            }

            layeredPane.revalidate();
            layeredPane.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent evt) {

            // If we have a piece, then drag it with the mouse
            if(canPickupPiece()) {
                // Calculate x-coords - prevent going too far left or right
                int xLoc = evt.getX() + xAdjustment;
                int xMax = layeredPane.getWidth() - originatingPiece.getWidth();
                xLoc = Math.min(xLoc, xMax);
                xLoc = Math.max(xLoc, 0);

                // Calculate y-coords - prevent going too far up or down
                int yLoc = evt.getY() + yAdjustment;
                int yMax = layeredPane.getHeight() - originatingPiece.getHeight();
                yLoc = Math.min(yLoc, yMax);
                yLoc = Math.max(yLoc, 0);

                // Update location of the piece
                originatingPiece.setLocation(xLoc, yLoc);
            }
        }

        /*
         * No other mouse events necessary
         */

        @Override
        public void mouseClicked(MouseEvent evt) {}

        @Override
        public void mouseMoved(MouseEvent evt) {
            label.setText("<html>Mouse pos: [" + evt.getX() + ", " + evt.getY() + "]"
                    + "<br/>" + "Tile pos: [y: " + getTilePositionFromMouse(evt).y + ", x: " + getTilePositionFromMouse(evt).x + "]</html>");
            printOut.add(label);
            printOut.setBounds(0,0, 200, 50);
            printOut.setLocation(evt.getX(), evt.getY());
            layeredPane.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent evt) { }

        @Override
        public void mouseExited(MouseEvent evt) { }
    }
}
