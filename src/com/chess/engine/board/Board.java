package com.chess.engine.board;

import com.chess.engine.GameSettings;
import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.moves.MoveHistory;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.ui.UIConstants;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board extends JPanel {

    // All tiles for the current board
    private final Map<Position, Tile> tileMap;

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

    // Map of tiles that are or are not highlighted
    private Map<Tile, Boolean> highlightedTiles;

    /**
     * Clone the other board to create another instance without UI
     * @param otherBoard the board to clone
     */
    public Board(Board otherBoard) {
        this(FenUtils.getFen(otherBoard), false);
    }

    /**
     * Create a board with UI always
     * @param fen the fen to load the board with
     */
    public Board(String fen) {
        this(fen, true);
    }

    /**
     * Create a new board representing the given fen string
     * Given the option of not rendering UI for the board
     * @param fen the fen string to present on a chessboard
     * @param withUI true if should UI be rendered, false if not
     */
    public Board(String fen, boolean withUI) {
        super(new GridLayout(8, 8));
        Player white = new Player(Player.Color.WHITE);
        Player black = new Player(Player.Color.BLACK);
        this.tileMap = new LinkedHashMap<>(64);
        this.highlightedTiles = new HashMap<>(64);
        this.players = new HashMap<>(2);
        this.players.put(Player.Color.WHITE, white);
        this.players.put(Player.Color.BLACK, black);
        this.gameState = new GameState(white);
        this.moveHistory = new MoveHistory(this);
        updateBoardFromFen(fen);

        // Only add UI portion if desired
        if(withUI) {
            // Create layered pane for dragging purposes
            this.layeredPane = new JLayeredPane();
            // TODO need to figure out how to add layout here for resizing
            //layeredPane.setLayout(new FlowLayout());
            setBounds(0, 0, UIConstants.BOARD_WIDTH, UIConstants.BOARD_HEIGHT);
            setPreferredSize(new Dimension(UIConstants.BOARD_WIDTH, UIConstants.BOARD_HEIGHT));

            // Add our piece listener
            BoardListener listener = new BoardListener(this);
            addMouseListener(listener);
            addMouseMotionListener(listener);

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

        try {
            // Load in the fen string
            FenUtils.loadFen(this, fen);

            // Initialize highlighted tiles to same as our map of tiles
            getTileMap().forEach((key, value) -> {
                // TODO is this needed? value.highlightTile(false);
                highlightedTiles.put(value, false);
            });

        } catch (FenUtils.FenException e) {
            System.err.println(e.getMessage());
        }

        /*// If player doesn't have valid moves, then the game is over
        if(!gameState.getPlayerTurn().hasValidMoves(Board.this)) {
            gameState.setGameOver(true);
            System.out.println("Game is over!");
        }*/
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
     * Map of tiles on the given board
     * @return all of the tiles for the given board
     */
    public Map<Position, Tile> getTileMap() { return this.tileMap; }

    /**
     * Map of players based on player color (White and Black)
     * @return the two players for the given board
     */
    public Map<Player.Color, Player> getPlayers() { return this.players; }

    /**
     * The current game state of the board
     * @return the {@link GameState} for the current board
     */
    public GameState getGameState() { return this.gameState; }

    /**
     * The move history of the board
     * @return the {@link MoveHistory} for the current board
     */
    public MoveHistory getMoveHistory() { return this.moveHistory; }

    public void undo() {
        moveHistory.undo();
    }

    public void redo() {
        moveHistory.redo();
    }

    /**
     * Get the white king's current position
     * @return the position of the white king
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

        // Remove highlight from all tiles that were previously highlighted
        highlightedTiles.entrySet().stream().filter(Map.Entry::getValue).forEach(entry -> {
            entry.setValue(false);
            entry.getKey().highlightTile(false);
        });

        // Add all of our tiles to the chess panel
        getTileMap().values().forEach(tile -> {

            // TODO -> Replace this with more performant version so we are only updating tiles that need to be
            tile.highlightTile(false);

            // Remove all UI elements from the tile
            tile.removeAll();

            // If displaying them, add tile positions
            if(GameSettings.INSTANCE.isDisplayTilePositions()) {
                Position tilePos = tile.getPosition();
                tile.add(new JLabel(tilePos + " - [" + tilePos.getRow() + "," + tilePos.getColumn() + "]"));
            }

            // If tile is occupied, then display the piece
            if(tile.isOccupied()) {
                Piece piece = tile.getPiece();
                tile.add(piece);

                // Highlight tiles containing pieces with valid moves for current player
                if(GameSettings.INSTANCE.isEnableHighlighting() && piece.getOwner().equals(getGameState().getPlayerTurn())) {
                    // As long as the piece has moves, highlight it
                    if(piece.getValidMoves(this).size() > 0) {
                        tile.setBackground(Color.GREEN);
                        highlightedTiles.replace(tile, true);
                    }
                }
            }

            // Add the tile to the board at last index
            this.add(tile, this.getComponents().length);
        });

        // If player doesn't have valid moves, then the game is over
        // TODO -> If the game is over before frame loads, then pane shows first..
        if(!gameState.getPlayerTurn().hasValidMoves(this)) {
            gameState.setGameOver(true);
            String[] options = {"Yes", "No", "Quit" };
            JOptionPane.showOptionDialog(this, "Game is ended in " + (gameState.isStaleMate() ? " stale mate." : " check mate.")
                    + "\nWould you like to start a new game?", "Game over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        }

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
}
