package com.chess.ui;

import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;
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

public class BoardUI extends JFrame {

    private JLayeredPane layeredPane;
    private JPanel chessBoard;
    private Board board;
    private final static int WINDOW_WIDTH = 600;
    private final static int WINDOW_HEIGHT = 600;

    public BoardUI(Board board) {
        super("Chess");
        this.board = board;
        initFrame();
    }

    /**
     * Initialize the frame object
     */
    private void initFrame() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        getContentPane().setLayout(new BorderLayout());
        // Add file and rank headers
        getContentPane().add(new FileHeaders(), BorderLayout.SOUTH);
        getContentPane().add(new RankHeaders(), BorderLayout.WEST);
        initBoardUI();
        this.setVisible(true);
        SoundUtils.playMoveSound("startGame");
    }

    /**
     * Initialize the game board
     * - File and rank headers
     * - History pane
     * - New game button
     * - All chess tiles
     */
    private void initBoardUI() {
        // Add new game button
        Button newGame = new Button("New game");
        getContentPane().add(newGame, BorderLayout.NORTH);
        newGame.addActionListener(e -> resetGameState());

        // Create the chess tiles
        PieceListener listener = new PieceListener();
        this.chessBoard = new JPanel(new GridLayout(8, 8));
        this.chessBoard.addMouseListener(listener);
        this.chessBoard.addMouseMotionListener(listener);
        // TODO Remove border
        Border border = BorderFactory.createLineBorder(Color.RED, 5);
        this.chessBoard.setBorder(border);
        // Set bounds for the chessboard when adding it to layered pane
        this.chessBoard.setBounds(0, 0, WINDOW_WIDTH-10, WINDOW_HEIGHT-70);

        // Create layered pane for dragging purposes
        this.layeredPane = new JLayeredPane();
        // TODO need to figure out how to add layout here for resizing
        //layeredPane.setLayout(new FlowLayout());
        border = BorderFactory.createLineBorder(Color.BLUE, 5);
        this.layeredPane.setBorder(border);
        this.layeredPane.setBounds(0, 0, WINDOW_WIDTH-10, WINDOW_HEIGHT-70);

        // Add all the chess tiles to the UI
        int i = 0;
        for (Map.Entry<String, Tile> entry : board.getTileMap().entrySet()) {
            this.chessBoard.add(entry.getValue(), i);
            i++;
        }

        // Add the individual tiles to the board in the center
        this.layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);

        // Add the chess board layered pane in the center
        getContentPane().add(this.layeredPane, BorderLayout.CENTER);
    }

    /**
     * Reset the game state (on click of new game button)
     */
    private void resetGameState() {

        // Remove all objects from the frame
        this.getContentPane().removeAll();

        // Recreate the board object with default position
        BoardUtils.getInstance().updateBoardFromFen(board, FenUtils.DEFAULT_POSITION);

        // Re-add all UI headers etc
        initBoardUI();

        // Reload the UI
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /*
    * Mouse coordinates are the inverse of the tile coordinates
    * So we need to take the absolute value of 8 - mouse coordinate
    */
    private Point getTilePositionFromMouse() {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        int tile_x_pos = (mouse.x / (this.chessBoard.getWidth() / 8) - 1);
        int tile_y_pos = mouse.y / (this.chessBoard.getHeight() / 8);
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
        return MoveUtils.executeMove(this.getBoard(), originatingTile, draggedToTile);
    }

    public Board getBoard() {
        return board;
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
            List<Move> validMoves = originatingPiece.getMoves(originatingTile);

            for (Move validMove : validMoves) {
                Tile destination = validMove.getDestination();
                targetMoveTiles.add(destination);
                destination.highlightTile(true);
            }

            layeredPane.revalidate();
            layeredPane.repaint();
        }

        /**
         * Remove all indicators from the layered pane
         */
        private void removeIndicators() {
            for (Tile targetTile : targetMoveTiles) {
                targetTile.highlightTile(false);
            }
        }

        /**
         * When pressing the mouse, figure out which tile we are on
         * Check whether there is a piece and if so, start holding it
         * Also highlight tiles that are available to move to
         * @param evt the mouse event that occurred
         */
        @Override
        public void mousePressed(MouseEvent evt) {
            // Clear out lists
            targetMoveTiles.clear();

            this.originatingTile = board.getTileMap().getOrDefault(new Position(getTilePositionFromMouse()).toString(), null);

            // If user clicked on a piece
            if(this.originatingTile != null) {

                originatingPiece = originatingTile.getPiece();

                // If we have a piece, add it to our layered pane to drag it around
                if(originatingPiece != null) {

                    // Calculate original location to add piece we are dragging into drag layer
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

        /**
         * When releasing the mouse:
         * - Remove highlighting of tiles
         * - Attempt to move the piece we dragged
         * - If piece could not be moved, restore it
         * @param evt the mouse release event
         */
        @Override
        public void mouseReleased(MouseEvent evt) {

            // Remove the indicators from tiles
            removeIndicators();

            // If the piece was not moved, then restore it to previous position
            if(originatingPiece != null && !attemptPieceMove(this.originatingTile)) {
                // If we had a piece that we attempted to move from a tile
                // Remove it from our layered pane and add it back to the originating tile
                originatingPiece.setVisible(false);
                layeredPane.remove(originatingPiece);
                originatingTile.add(originatingPiece);
                originatingPiece.setVisible(true);
            }

            // revalidate and repaint
            layeredPane.revalidate();
            layeredPane.repaint();
        }

        /**
         * While dragging, move the piece with us
         * @param evt the mouse drag event
         */
        @Override
        public void mouseDragged(MouseEvent evt) {

            // If we have a piece, then drag it with the mouse
            if(originatingPiece != null) {
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
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }
}
