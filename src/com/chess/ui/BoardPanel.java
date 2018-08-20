package com.chess.ui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.MoveUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

        // Set our board's layout to an 8x8 grid
        this.setLayout(new GridLayout(8, 8));
        this.setBounds(0, 0, ChessFrame.WINDOW_WIDTH-60, ChessFrame.WINDOW_HEIGHT-60);

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
    }

    /*
     * Mouse coordinates are the inverse of the tile coordinates
     * So we need to take the absolute value of 8 - mouse coordinate
     */
    private Point getTilePositionFromMouse() {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
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
    private boolean attemptPieceMove(Tile originatingTile) {
        Position draggedToPosition = new Position(getTilePositionFromMouse());
        Tile draggedToTile = board.getTileMap().getOrDefault(draggedToPosition.toString(), null);

        // Attempt to move the piece on the originating tile to the new tile
        boolean moved = MoveUtils.executeMove(this.getBoard(), originatingTile, draggedToTile);
        layeredPane.revalidate();
        layeredPane.repaint();

        return moved;
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

        @Override
        public void mousePressed(MouseEvent e) {
            this.originatingTile = board.getTileMap().getOrDefault(new Position(getTilePositionFromMouse()).toString(), null);

            // If user clicked on a piece
            if(this.originatingTile != null) {

                originatingPiece = originatingTile.getPiece();

                // If we have a piece, add it to our layered pane to drag it around
                if(originatingPiece != null) {
                    Point origLoc = originatingTile.getLocation();
                    xAdjustment = origLoc.x - e.getX();
                    yAdjustment = origLoc.y - e.getY();
                    originatingPiece.setLocation(origLoc.x + 2, origLoc.y - 2);
                    layeredPane.add(originatingPiece, JLayeredPane.DRAG_LAYER);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            // If the piece was not moved, then restore it to previous position
            if(originatingPiece != null && !attemptPieceMove(this.originatingTile)) {
                // If we had a piece that we attempted to move from a tile
                // Remove it from our layered pane and add it back to the originating tile
                originatingPiece.setVisible(false);
                layeredPane.remove(originatingPiece);
                originatingTile.add(originatingPiece);
                originatingPiece.setVisible(true);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            // If we have a piece, then drag it with the mouse
            if(originatingPiece != null) {
                // Calculate x-coords - prevent going too far left or right
                int xLoc = e.getX() + xAdjustment;
                int xMax = layeredPane.getWidth() - originatingPiece.getWidth();
                xLoc = Math.min(xLoc, xMax);
                xLoc = Math.max(xLoc, 0);

                // Calculate y-coords - prevent going too far up or down
                int yLoc = e.getY() + yAdjustment;
                int yMax = layeredPane.getHeight() - originatingPiece.getHeight();
                yLoc = Math.min(yLoc, yMax);
                yLoc = Math.max(yLoc, 0);

                // Update location of the piece
                originatingPiece.setLocation(xLoc, yLoc);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }
}
