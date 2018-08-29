package com.chess.ui;

import com.chess.engine.GameSettings;
import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.SoundUtils;

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

    private JLayeredPane layeredPane;
    private Board board;

    BoardPanel(Board board) {
        super();
        this.board = board;
        initBoardUI();
    }

    /**
     * Initialize the game board
     * - All chess tiles
     */
    public void initBoardUI() {
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

        // Add all the chess tiles to the UI
        int i = 0;
        for (Map.Entry<String, Tile> entry : board.getTileMap().entrySet()) {
            // Add the individual tiles to the board in the center
            this.add(entry.getValue(), i);
            i++;
        }

        // Add the myself to our layered pane's default layer
        this.layeredPane.add(this, JLayeredPane.DEFAULT_LAYER);
        SoundUtils.playMoveSound("startGame");

    }

    /*
     * Mouse coordinates are the inverse of the tile coordinates
     * So we need to take the absolute value of 8 - mouse coordinate
     */
    private Point getTilePositionFromMouse(MouseEvent evt) {
        //Point mouse = MouseInfo.getPointerInfo().getLocation();
        Point mouse = evt.getLocationOnScreen();
        int tile_x_pos = (mouse.x / (this.getWidth() / 8) - 1);
        int tile_y_pos = mouse.y / (this.getHeight() / 8);
        tile_y_pos = Math.abs(8 - tile_y_pos);

        System.out.println("Pane Width: " + getLayeredPane().getWidth());
        System.out.println("Pane Height: " + getLayeredPane().getHeight());

        System.out.println("Board Width: " + this.getWidth());
        System.out.println("Board Height: " + this.getHeight());

        System.out.println("Mouse pos: [" + mouse.getX() + ", " + mouse.getY() + "]");
        System.out.println("Tile pos: [" + tile_y_pos + ", " + tile_x_pos + "]");

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

        /**
         * Add indicators to the UI for tiles we can move to
         */
        private void addIndicators() {
            if(GameSettings.getInstance().isEnableHighlighting()) {
                List<Move> validMoves = originatingPiece.getMoves(originatingTile);

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
         * @return
         */
        private boolean canPickupPiece() {
            return originatingPiece != null && originatingPiece.getOwner().equals(GameState.getInstance().getPlayerTurn());
        }


        @Override
        public void mousePressed(MouseEvent evt) {

            // Clear out lists
            targetMoveTiles.clear();

            this.originatingTile = board.getTileMap().getOrDefault(new Position(getTilePositionFromMouse(evt)).toString(), null);

            // If user clicked on a piece
            if(this.originatingTile != null) {

                originatingTile.highlightTile(true); // TODO remove this
                originatingPiece = originatingTile.getPiece();

                // If we have a piece, add it to our layered pane to drag it around
                if(canPickupPiece()) {
                    Point origLoc = originatingTile.getLocation();
                    xAdjustment = origLoc.x - evt.getX();
                    yAdjustment = origLoc.y - evt.getY();
                    originatingPiece.setLocation(origLoc.x + 2, origLoc.y - 2);
                    layeredPane.add(originatingPiece, JLayeredPane.DRAG_LAYER);


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
            JPanel printOut = new JPanel();
            JLabel label = new JLabel();
            label.setText(getTilePositionFromMouse(evt).x + " " + getTilePositionFromMouse(evt).y);
            printOut.add(label);
            printOut.setBounds(0,0, 10, 10);
            layeredPane.add(printOut, JLayeredPane.DRAG_LAYER);
            layeredPane.revalidate();
            layeredPane.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent evt) { }

        @Override
        public void mouseExited(MouseEvent evt) { }
    }
}
