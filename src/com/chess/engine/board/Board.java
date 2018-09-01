package com.chess.engine.board;

import com.chess.engine.GameSettings;
import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MoveHistory;
import com.chess.engine.moves.MoveUtils;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.ui.ChessFrame;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board extends JPanel {

    // All tiles for the current board
    private final Map<String, Tile> tileMap;

    // The players for the current board
    private final Map<Player.Color, Player> players;

    // The current game state
    private final GameState gameState;
    private final MoveHistory moveHistory;

    // This is set when loading board from FEN string
    private Position whiteKingPos;
    private Position blackKingPos;

    // Pane the board is added to
    private JLayeredPane layeredPane;

    public Board(String fen) {
        this(fen, true);
    }

    /**
     * Create a new board with representing the given fen string
     * @param fen the fen string to present on a chessboard
     */
    public Board(String fen, boolean withUI) {
        super();
        Player white = new Player(Player.Color.WHITE);
        Player black = new Player(Player.Color.BLACK);
        tileMap = new LinkedHashMap<>(64);
        players = new HashMap<>(2);
        this.players.put(Player.Color.WHITE, white);
        this.players.put(Player.Color.BLACK, black);
        this.gameState = new GameState(white);
        this.moveHistory = new MoveHistory();
        updateBoardFromFen(fen);

        // Only add UI portion if desired
        if(withUI) {
            // Create layered pane for dragging purposes
            this.layeredPane = new JLayeredPane();
            // TODO need to figure out how to add layout here for resizing
            //layeredPane.setLayout(new FlowLayout());

            // Set our board's layout to an 8x8 grid
            this.setLayout(new GridLayout(8, 8));
            this.setBounds(0, 0, ChessFrame.WINDOW_WIDTH - 60, ChessFrame.WINDOW_HEIGHT - 80);

            // Create the chess tiles
            Board.PieceListener listener = new Board.PieceListener();
            this.addMouseListener(listener);
            this.addMouseMotionListener(listener);

            // Display the pieces on the board
            displayBoard();

            // Add the myself to our layered pane's default layer
            this.layeredPane.add(this, JLayeredPane.DEFAULT_LAYER);
        }
    }

    /**
     * Updates reference to game board and recreates it with given fen
     * @param fen the fen string to update the board with
     */
    public void updateBoardFromFen(String fen) {
        // Clear each player's pieces / captured pieces
        getPlayers().values().forEach(player -> {
            player.getPieces().clear();
            player.getCapturedPieces().clear();
        });

        // Clear tiles on the board
        getTileMap().clear();

        // Load in the default position
        try {
            FenUtils.loadFen(this, fen);
        } catch (FenUtils.FenException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     * Construct a game piece at the given position
     */
    Piece constructPiece(final char ch) {
        Piece piece = null;

        // Determine the color this piece belongs to
        Player color = Character.isUpperCase(ch) ? getPlayers().get(Player.Color.WHITE)
                                                 : getPlayers().get(Player.Color.BLACK);

        // Determine the type of piece
        switch(Character.toLowerCase(ch)) {
            case 'p':
                piece = new Pawn(color);
                break;
            case 'b':
                piece = new Bishop(color);
                break;
            case 'r':
                piece = new Rook(color);
                break;
            case 'n':
                piece = new Knight(color);
                break;
            case 'q':
                piece = new Queen(color);
                break;
            case 'k':
                piece = new King(color);
                break;
            default:
                break;
        }

        // Add the piece for that player
        color.addPiece(piece);

        // return the piece
        return piece;
    }

    /**
     * Clone the other board to create another instance
     * @param otherBoard the board to clone
     */
    public Board(Board otherBoard) {
        this(FenUtils.getFen(otherBoard), false);
    }

    // GETTERS
    public Map<String, Tile> getTileMap() { return this.tileMap; }
    public Map<Player.Color, Player> getPlayers() { return this.players; }
    public GameState getGameState() { return this.gameState; }
    public MoveHistory getMoveHistory() { return this.moveHistory; }

    /**
     * Update the position if the king piece with new position
     * @param kingPiece the king piece to update
     * @param newPosition the new position of the king
     */
    public void setKingPosition(King kingPiece, Position newPosition) {
        if(kingPiece.getOwner().isWhite()) {
            whiteKingPos = newPosition;
        } else {
            blackKingPos = newPosition;
        }
    }

    /**
     * Get the white king's current position
     * @return the positino of the white king
     */
    public Position getWhiteKingPosition() {
        return whiteKingPos;
    }

    /**
     * Get the black king's current position
     * @return the position of the black king
     */
    public Position getBlackKingPosition() {
        return blackKingPos;
    }

    /**
     * String representation of the board
     * 8 [r] [n] [b] [q] [k] [b] [n] [r]
     * 7 [p] [p] [p] [p] [p] [p] [p] [p]
     * 6 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 5 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 4 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 3 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 2 [P] [P] [P] [P] [P] [P] [P] [P]
     * 1 [R] [N] [B] [Q] [K] [N] [B] [R]
     *    a   b   c   d   e   f   g   h
     * @return can be used to print out the board in a string representation
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        final int[] i = {0};
        getTileMap().values().forEach( tile -> {
            if(i[0] == 0 || i[0] % 8 == 0) {
                builder.append(7 - i[0] / 8 + 1).append(" ");
            }

            builder.append(String.format("%3s", tile.toString()));

            if ((i[0] + 1) % 8 == 0) {
                builder.append("\n");
            }
            i[0]++;
        });

        builder.append("   a  b  c  d  e  f  g  h");
        return builder.toString();
    }

    /*
     * UI related part of the board
     */

    /**
     * Initialize the game board
     * - All chess tiles
     */
    public void displayBoard() {

        // Remove all tiles
        this.removeAll();

        // Add all of our tiles to the chess panel
        getTileMap().values().forEach(tile -> {

            // Remove all UI elements from the tile
            tile.removeAll();

            // If debugging, display tile positions too
            if(GameSettings.getInstance().isEnableDebugging()) {
                Position tilePos = tile.getPosition();
                JLabel locationText = new JLabel(tilePos + " - [" + tilePos.getRow() + "," + tilePos.getColumn() + "]");
                tile.add(locationText);
            }

            // If tile is occupied, then display the piece
            if(tile.isOccupied()) {
                Piece piece = tile.getPiece();
                tile.add(piece);

                // Highlight tiles containing pieces with valid moves
                if(GameSettings.getInstance().isEnableHighlighting()) {
                    if(piece.getOwner() == getGameState().getPlayerTurn()
                        && piece.getValidMoves(this).size() > 0) {
                        //System.out.println("Piece: " + piece);
                        //System.out.println("Owner: " + piece.getOwner());
                        //System.out.println("Position: " + tile.getPosition());
                        tile.setBackground(Color.RED);
                    } else {
                        tile.highlightTile(false);
                    }
                }
            }

            // Add the tile to the board
            this.add(tile, this.getComponents().length);
        });

        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Get the layered pane object which we use to drag pieces around on
     * @return the layered pane which our chessboard exists within
     */
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

        // Debugging
        JPanel printOut = new JPanel();
        JLabel label = new JLabel();

        /**
         * Add indicators to the UI for tiles we can move to
         */
        private void addIndicators() {
            if(GameSettings.getInstance().isEnableHighlighting()) {
                Set<Move> validMoves = originatingPiece.getValidMoves(Board.this);

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
         * Prevent Player from moving a piece if it is not their turn
         * @return whether the Player can pickup a given piece
         */
        private boolean canPickupPiece() {
            return originatingPiece != null && originatingPiece.getOwner().equals(getGameState().getPlayerTurn());
        }

        /*
         * Mouse coordinates are the inverse of the tile coordinates
         * So we need to take the absolute value of 8 - mouse coordinate
         */
        private Point getTilePositionFromMouse() {
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouse, Board.this);
            int tile_x_pos = mouse.x / (getWidth() / 8);
            int tile_y_pos = mouse.y / (getHeight() / 8) + 1;
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
            Tile draggedToTile = getTileMap().getOrDefault(draggedToPosition.toString(), null);
            return MoveUtils.executeMove(Board.this, originatingTile, draggedToTile);
        }

        @Override
        public void mousePressed(MouseEvent evt) {

            // Clear out lists
            targetMoveTiles.clear();

            // Get mouse press location in tile coordinate system
            Position mousePressPosition = new Position(getTilePositionFromMouse());
            this.originatingTile = getTileMap().getOrDefault(mousePressPosition.toString(), null);

            // If user clicked on a piece
            if(this.originatingTile != null) {

                // If we have a piece, add it to our layered pane to drag it around
                originatingPiece = originatingTile.getPiece();
                if(canPickupPiece()) {
                    Point origLoc = originatingTile.getLocation();
                    originatingPiece.setLocation(origLoc.x + 10, origLoc.y + 5);
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


            // If we had a piece being dragged
            if(canPickupPiece()) {

                // If the piece was not moved, then restore it to previous position
                if (!attemptPieceMove(this.originatingTile)) {
                    // If we had a piece that we attempted to move from a tile
                    // Remove it from our layered pane and add it back to the originating tile
                    originatingPiece.setVisible(false);
                    layeredPane.remove(originatingPiece);
                    originatingTile.add(originatingPiece);
                    originatingPiece.setVisible(true);
                } else {
                    // Update game history
                    ChessFrame frame = (ChessFrame) SwingUtilities.getAncestorOfClass(ChessFrame.class, layeredPane);
                    frame.getHistoryPanel().updateHistory();
                    displayBoard();
                }
            }
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

            if(GameSettings.getInstance().isEnableDebugging()) {
                layeredPane.remove(printOut);
                layeredPane.add(printOut, JLayeredPane.DRAG_LAYER);

                label.setText("<html>Mouse pos: [" + evt.getX() + ", " + evt.getY() + "]"
                        + "<br/>" + "Tile pos: [y: " + getTilePositionFromMouse().y + ", x: " + getTilePositionFromMouse().x + "]</html>");
                printOut.add(label);
                printOut.setBounds(0, 0, 200, 50);
                printOut.setLocation(evt.getX(), evt.getY());
                layeredPane.repaint();
            }
        }

        @Override
        public void mouseEntered(MouseEvent evt) { }

        @Override
        public void mouseExited(MouseEvent evt) { }
    }
}
