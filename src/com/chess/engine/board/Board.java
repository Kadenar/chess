package com.chess.engine.board;

import com.chess.ChessConsts;
import com.chess.engine.GameSettings;
import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MoveHistory;
import com.chess.engine.moves.MoveUtils;
import com.chess.engine.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Board extends JPanel {

    // All tiles for the current board
    private final Map<Position, Tile> tileMap;

    // Map of tiles that contain a piece which can move
    private final Map<Tile, Boolean> highlightedTiles;

    private final Map<Player.Color, Player> immutablePlayers;

    // Contains the king position for each player
    private final Map<Player, Position> kingPositionMap;

    // Contains both players set of moves for all pieces owned by that player
    private final Map<Player, Map<Integer, Map<Piece, Set<Move>>>> movesForPlayers;
    private final Map<Player, Map<Integer, Map<Piece, Set<Move>>>> validMovesForPlayers;

    // The current game state
    private final GameState gameState;
    private final MoveHistory moveHistory;

    // Pane the board is added to
    private JLayeredPane layeredPane;

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
        super(new GridLayout(ChessConsts.NUM_TILE, ChessConsts.NUM_TILE));

        // Add our tiles
        this.tileMap = new LinkedHashMap<>(ChessConsts.NUM_TILES);
        this.highlightedTiles = new HashMap<>(ChessConsts.NUM_TILES);

        // Add our players
        Map<Player.Color, Player> players = new HashMap<>(ChessConsts.NUM_PLAYERS);
        players.put(Player.Color.WHITE, new Player(Player.Color.WHITE));
        players.put(Player.Color.BLACK, new Player(Player.Color.BLACK));
        this.immutablePlayers = Collections.unmodifiableMap(players);

        // Our moves / valid moves for both players
        this.movesForPlayers = new HashMap<>(ChessConsts.NUM_PLAYERS);
        this.validMovesForPlayers = new HashMap<>(ChessConsts.NUM_PLAYERS);
        immutablePlayers.forEach((key, value) -> {
            // Use 500 as a chess game should never reach this # of turns
            this.movesForPlayers.put(value, new HashMap<>(500));
            this.validMovesForPlayers.put(value, new HashMap<>(500));
        });

        // Initialize the current game state, move history and king positions
        this.gameState = new GameState();
        this.moveHistory = new MoveHistory(this);
        this.kingPositionMap = new HashMap<>(ChessConsts.NUM_PLAYERS);

        // Load our board information from FEN string
        this.loadBoardFromFen(fen);

        // Only add UI portion if desired
        if(withUI) {
            // Create layered pane for dragging purposes
            this.layeredPane = new JLayeredPane();
            // TODO need to figure out how to add layout here for resizing
            //layeredPane.setLayout(new FlowLayout());
            this.setBounds(0, 0, ChessConsts.BOARD_WIDTH, ChessConsts.BOARD_HEIGHT);
            this.setPreferredSize(new Dimension(ChessConsts.BOARD_WIDTH, ChessConsts.BOARD_HEIGHT));

            // Add our piece listener
            BoardListener listener = new BoardListener(this);
            this.addMouseListener(listener);
            this.addMouseMotionListener(listener);

            // Display the pieces on the board
            this.displayBoard();

            // Add the myself to our layered pane's default layer
            this.layeredPane.add(this, JLayeredPane.DEFAULT_LAYER);
        }
    }

    /**
     * Updates reference to game board and recreates it with given fen
     * @param fen a valid fen string to update the board with. Providing an invalid fen will result in an error message.
     */
    public void loadBoardFromFen(final String fen) {

        // Clear each player's pieces / captured pieces
        getPlayers().values().forEach(player -> {
            player.getPieces().clear();
            player.getCapturedPieces().clear();
        });

        // Clear tiles / highlights on the board
        getTileMap().clear();
        highlightedTiles.clear();

        try {
            // Load in the fen string
            FenUtils.loadFen(this, fen);

            // Initialize highlighted tiles to same as our map of tiles and default not highlighted
            getTileMap().forEach((key, value) -> highlightedTiles.put(value, false));

        } catch (FenUtils.FenException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     * Construct a game piece at the given position
     */
    void constructPiece(final char ch, final Position positionOnBoard) {
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
                setKingPosition(color, positionOnBoard);
                break;
            default:
                break;
        }

        // Add the piece for that player
        color.addPiece(piece);

        // Add the piece to the tile
        getTileMap().put(positionOnBoard, new Tile(positionOnBoard, piece));
    }

    /**
     * Generate moves for the current board state
     * @param player the {@code Player} to generate moves for
     */
    public void generateMovesForPlayer(Player player) {
        // Generate moves for all pieces owned by me
        getTileMap().values().stream()
                .filter(tile -> tile.isOccupied() && player.equals(tile.getPiece().getOwner()))
                .forEach(tile -> tile.getPiece().addMovesToBoard(this, tile));
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
        Map<Piece, Set<Move>> movesForTurn = movesForPlayer.get(turn);

        if(movesForTurn == null) {
            movesForTurn = new HashMap<>();
            movesForPlayer.put(turn, movesForTurn);
        }

        return movesForTurn;
    }

    /**
     * Get moves for a given piece on a specific turn
     * @param turn the {@code int} for the turn to get moves from
     * @param piece the {@code Piece} to get moves for
     * @return the {@code Set<Move>} for the given piece
     */
    public Set<Move> getMovesForPiece(int turn, Piece piece) {
        Map<Piece, Set<Move>> movesForTurn = getMovesForTurn(turn, piece.getOwner());
        Set<Move> movesForPiece = movesForTurn.get(piece);

        if(movesForPiece == null) {
            movesForPiece = new HashSet<>();
            movesForTurn.put(piece, movesForPiece);
        }

        return movesForPiece;
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
     * @param player the {@code Player} to get moves for
     * @param turn the {@code int} representing what turn to get moves for
     * @return the {@code Map<Piece, Set<Move>>} of moves for the @{code Player} on the given turn
     */
    public Map<Piece, Set<Move>> getValidMovesForTurn(int turn, Player player) {
        // Get valid moves for a given turn
        Map<Integer, Map<Piece, Set<Move>>> validMovesForPlayer = getValidMovesForPlayer(player);
        Map<Piece, Set<Move>> validMovesForTurn = validMovesForPlayer.get(turn);

        // If moves for the given turn have not been generated yet, then create them
        if(validMovesForTurn == null) {
            validMovesForTurn = new HashMap<>();
            int fullMoves = getGameState().getFullMoves();

            // For each piece, execute a test move and add valid moves for the turn
            for(Piece piece : player.getPieces()) {
                Set<Move> validMovesForPiece = getMovesForPiece(fullMoves, piece).stream()
                        .filter(move -> MoveUtils.executeTestMove(this, move.getOrigin(), move.getDestination()))
                        .collect(Collectors.toSet());

                validMovesForTurn.put(piece, validMovesForPiece);
            }

            // Put valid moves into our map for the current player
            validMovesForPlayer.put(fullMoves, validMovesForTurn);
        }

        // Return valid moves for the turn for the given player
        return validMovesForTurn;
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
        return getValidMovesForTurn(turn, piece.getOwner()).getOrDefault(piece, Collections.emptySet());
    }

    /**
     * Map of players based on player color (White and Black)
     * @return the two players for the given board
     */
    public Map<Player.Color, Player> getPlayers() { return this.immutablePlayers; }

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

    /*
     * UI related part of the board
     */

    /**
     * Initialize the game board
     * - All chess tiles
     */
    public void displayBoard() {

        System.out.println("---Displaying the board---");

        // Remove all tiles
        this.removeAll();

        // Remove highlight from all tiles that were previously highlighted
        highlightedTiles.entrySet().stream().filter(Map.Entry::getValue).forEach(entry -> {
            entry.setValue(false);
            entry.getKey().highlightTile(false);
        });

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

                // Highlight tiles containing pieces with valid moves for current player
                if(GameSettings.INSTANCE.isEnableHighlighting() && piece.getOwner().equals(getGameState().getPlayerTurn())) {
                    // As long as the piece has moves, highlight it
                    if(getValidMovesForPiece(getGameState().getFullMoves(), piece).size() > 0) {
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
