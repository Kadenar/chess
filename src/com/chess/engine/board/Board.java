package com.chess.engine.board;

import com.chess.ChessConsts;
import com.chess.engine.GameSettings;
import com.chess.engine.Player;
import com.chess.engine.PlayerColor;
import com.chess.engine.Position;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MoveHistory;
import com.chess.engine.moves.MoveUtils;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Board extends JPanel {

    // All tiles for the current board
    private final Map<Position, Tile> tileMap;

    // Map of players
    private final Map<PlayerColor, Player> immutablePlayers;

    // Contains the king position for each player
    private final Map<Player, Position> kingPositionMap;

    // Contains both players set of moves for all pieces owned by that player
    private final Map<Player, Map<Integer, Map<Piece, Set<Move>>>> movesForPlayers;
    private final Map<Player, Map<Integer, Map<Piece, Set<Move>>>> validMovesForPlayers;

    // The current game state / move history
    private final GameState gameState;
    private final MoveHistory moveHistory;

    // Create layered pane for dragging purposes
    private final JLayeredPane layeredPane = new JLayeredPane();

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
    private Board(String fen, boolean withUI) {
        super(new GridLayout(ChessConsts.NUM_TILE, ChessConsts.NUM_TILE));

        // Add our tiles
        this.tileMap = new LinkedHashMap<>(ChessConsts.NUM_TILES);

        // Add our players
        Map<PlayerColor, Player> players = new HashMap<>(ChessConsts.NUM_PLAYERS);
        players.put(PlayerColor.WHITE, new Player(PlayerColor.WHITE));
        players.put(PlayerColor.BLACK, new Player(PlayerColor.BLACK));
        this.immutablePlayers = Collections.unmodifiableMap(players);

        // Our moves / valid moves for both players
        this.movesForPlayers = new HashMap<>(ChessConsts.NUM_PLAYERS);
        this.validMovesForPlayers = new HashMap<>(ChessConsts.NUM_PLAYERS);
        immutablePlayers.forEach((key, value) -> {
            // Use 300 as a chess game should never reach this # of turns
            this.movesForPlayers.put(value, new HashMap<>(300));
            this.validMovesForPlayers.put(value, new HashMap<>(300));
        });

        // Initialize the current game state, move history and king positions
        this.gameState = new GameState();
        this.moveHistory = new MoveHistory(this);
        this.kingPositionMap = new HashMap<>(ChessConsts.NUM_PLAYERS);

        // Load our board information from FEN string
        this.loadBoardFromFen(fen);

        // Only add UI portion if desired
        if(withUI) {
            // TODO need to figure out how to add layout here for resizing
            //layeredPane.setLayout(new FlowLayout());
            this.setBounds(0, 0, ChessConsts.BOARD_WIDTH, ChessConsts.BOARD_HEIGHT);
            this.setPreferredSize(new Dimension(ChessConsts.BOARD_WIDTH, ChessConsts.BOARD_HEIGHT));

            // Add our piece listener
            BoardListener listener = new BoardListener(this);
            this.addMouseListener(listener);
            this.addMouseMotionListener(listener);

            // Display the pieces on the board
            displayBoard();

            // Add the myself to our layered pane's default layer
            this.layeredPane.add(this, JLayeredPane.DEFAULT_LAYER);
        }
    }

    /**
     * Clone the other board to create another instance without UI
     * @param otherBoard the board to clone
     */
    public Board(Board otherBoard) {
        this(FenUtils.getFen(otherBoard), false);
    }

    /**
     * Reset the {@code Board} to Default Position
     */
    public void reset() {
        getMoveHistory().reset();
        loadBoardFromFen(FenUtils.DEFAULT_POSITION);
        displayBoard();
    }

    /**
     * Updates reference to game board and recreates it with given fen
     * @param fen a valid fen string to update the board with. Providing an invalid fen will result in an error message.
     */
    private void loadBoardFromFen(final String fen) {

        // Clear each player's pieces / captured pieces
        getPlayers().values().forEach(player -> {
            player.getPieces().clear();
            player.getCapturedPieces().clear();
        });

        // Clear tiles / highlights on the board
        getTileMap().clear();

        try {

            // Load in the fen string
            FenUtils.loadFen(this, fen);

        } catch (FenUtils.FenException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Construct a game piece at the given position
     * @param ch the character representing the piece
     * @param positionOnBoard the {@code Position} to construct the piece at
     */
    void constructPiece(final char ch, final Position positionOnBoard) {
        // Determine the color this piece belongs to
        Player owner = Character.isUpperCase(ch) ? getPlayers().get(PlayerColor.WHITE)
                                                 : getPlayers().get(PlayerColor.BLACK);

        // Get the type of piece to be created
        Piece piece = BoardUtils.getTypeOfPieceToCreate(ch, owner);

        // If the type of piece is a king, set king position
        if(piece instanceof King) {
            setKingPosition(owner, positionOnBoard);
        }

        // Add the piece for that player
        owner.addPiece(piece);

        // Add the piece to the tile
        getTileMap().put(positionOnBoard, new Tile(positionOnBoard, piece));
    }

    /**
     * Generate moves for the current board state
     * @param player the {@code Player} to generate moves for
     */
    public void generateMovesForPlayer(Player player, int turn) {
        // Generate moves for all pieces owned by me
        getTileMap().values().stream()
                .filter(tile -> tile.isOccupied() && player.equals(tile.getPiece().getOwner()))
                .forEach(tile -> tile.getPiece().addMovesToBoard(this, tile, turn));
    }

    /**
     * Map of tiles on the given board
     * @return all of the tiles for the given board
     */
    public Map<Position, Tile> getTileMap() { return this.tileMap; }

    /**
     * Get all moves for each player and piece that player controls
     * @return all of the available moves for each piece players control
     */
    private Map<Integer, Map<Piece, Set<Move>>> getMovesForPlayer(Player player) {
        return this.movesForPlayers.getOrDefault(player, new TreeMap<>());
    }

    /**
     * Get all moves for a specific player on a given turn
     * @param turn the {@code int} representing what turn to get moves for
     * @param player the {@code Player} to get moves for
     * @return the {@code Map<Piece, Set<Move>>} of moves for the player on the given turn
     */
    public Map<Piece, Set<Move>> getMovesForTurn(int turn, Player player) {
        Map<Integer, Map<Piece, Set<Move>>> movesForPlayer = getMovesForPlayer(player);
        return movesForPlayer.computeIfAbsent(turn, k -> new HashMap<>());
    }

    /**
     * Get moves for a given piece on a specific turn
     * @param turn the {@code int} for the turn to get moves from
     * @param piece the {@code Piece} to get moves for
     * @return the {@code Set<Move>} for the given piece
     */
    public Set<Move> getMovesForPiece(int turn, Piece piece) {
        Map<Piece, Set<Move>> movesForTurn = getMovesForTurn(turn, piece.getOwner());
        return movesForTurn.computeIfAbsent(piece, k -> new HashSet<>());
    }

    /**
     * Get valid moves for a specific player and pieces that player controls
     * @return all of the available moves for each piece players control for all turns of the game thus far
     */
    private Map<Integer, Map<Piece, Set<Move>>> getValidMovesForPlayer(Player player) {
        return this.validMovesForPlayers.getOrDefault(player, new HashMap<>());
    }

    /**
     * Get valid moves for a specific player on a given turn
     * NOTE - this method is expensive as it evaluates all pieces on the board
     * @param player the {@code Player} to get moves for
     * @param turn the {@code int} representing what turn to get moves for
     * @return the {@code Map<Piece, Set<Move>>} of moves for the @{code Player} on the given turn
     */
    private Map<Piece, Set<Move>> getValidMovesForTurn(int turn, Player player) {
        Map<Integer, Map<Piece, Set<Move>>> validMovesForPlayer = getValidMovesForPlayer(player);
        return validMovesForPlayer.computeIfAbsent(turn, k -> new HashMap<>());
    }

    /**
     * Get valid moves for this piece
     * (preventing the piece from actually moving if it would put the Player in check)
     * This information is cached on a per turn basis to avoid performing test moves unnecessarily multiple times
     * @param turn the {@code int} representing what turn to get moves for
     * @param piece the {@code Piece} to get moves for
     * @return the {@code Set<Move>} for the given piece on the given turn
     */
    public Set<Move> getValidMovesForPiece(int turn, Piece piece) {
        Map<Piece, Set<Move>> validMovesForTurn = getValidMovesForTurn(turn, piece.getOwner());
        Set<Move> validMovesForPiece = validMovesForTurn.get(piece);

        if(validMovesForPiece == null) {
            Predicate<Move> validMove = move -> MoveUtils.executeTestMove(this, move.getOrigin(), move.getDestination());
            validMovesForPiece = getMovesForPiece(turn, piece).parallelStream().filter(validMove).collect(Collectors.toSet());
            validMovesForTurn.put(piece, validMovesForPiece);
        }

        return validMovesForPiece;
    }

    /**
     * Map of players based on player color (White and Black)
     * @return the two players for the given board
     */
    public Map<PlayerColor, Player> getPlayers() { return this.immutablePlayers; }

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

    /**
     * Undo the most recent move
     */
    public void undo() {
        moveHistory.undo();
    }

    /**
     * Redo the most recent move
     */
    public void redo() {
        moveHistory.redo();
    }

    /**
     * Get the king position of the given player
     * @param player the {@link Player} to get the king position for
     * @return the position of the king
     */
    public Position getKingPosition(final Player player) {
        return kingPositionMap.get(player);
    }

    /**
     * Update the position if the king piece with new position
     * @param player the player to update king position for
     * @param newPosition the new position of the king
     */
    public void setKingPosition(final Player player, final Position newPosition) {
        kingPositionMap.put(player, newPosition);
    }

    /**
     * Initialize the game board
     * - All chess tiles
     */
    public void displayBoard() {

        // Remove all tiles
        this.removeAll();

        // Remove highlight from all tiles that were previously highlighted
        getTileMap().values().stream().filter(Tile::isHighlighted)
                .forEach(tile -> tile.highlightTile(false, null));

        Player currentPlayer = gameState.getPlayerTurn();

        // Highlight last move's tiles
        Move lastMove = getMoveHistory().getLastMove();
        if(lastMove != null) {
            lastMove.getOrigin().highlightTile(true, new Color(50, 255, 200));
            lastMove.getDestination().highlightTile(true, new Color(50, 255, 150));
        }

        // Add all of our tiles to the chess panel
        getTileMap().values().forEach(tile -> {

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
                piece.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Highlight tiles containing pieces with valid moves for current player
                // As long as the piece has moves, highlight it
                if(GameSettings.INSTANCE.isEnableHighlighting()
                        && piece.getOwner().equals(currentPlayer)
                        && getValidMovesForPiece(getGameState().getFullMoves(), piece).stream().findFirst().isPresent()) {
                    tile.highlightTile(true, new Color(50,205,50));
                }
            }

            // Add the tile to the board at last index
            this.add(tile, this.getComponents().length);
        });

        // If player doesn't have valid moves, then the game is over
        // TODO -> If the game is over before frame loads, then pane shows first..
        if(!currentPlayer.hasValidMove(this)) {
            gameState.setGameOver(MoveUtils.isKingInCheck(this,
                    currentPlayer.opposite(this), currentPlayer) == null);

            int value = JOptionPane.showConfirmDialog(this,
                    "Game ended in " + (gameState.isStaleMate() ? " stale mate." : " check mate.")
                            + "\nWould you like to start a new game?", "Game over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(value == 0) {
                reset();
            }
        }

        layeredPane.revalidate();
        layeredPane.repaint();
    }

    /**
     * Get the layered pane object which we use to drag pieces around on
     * @return the layered pane which our chessboard exists within
     */
    JLayeredPane getLayeredPane() {
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
