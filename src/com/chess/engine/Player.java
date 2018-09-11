package com.chess.engine;

import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Move;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Player {

    // Player's color
    private final Color color;

    // Pieces owner by the player
    private List<Piece> pieces = new ArrayList<>(24);

    // Pieces captured by opponent
    private List<Piece> capturedPieces = new ArrayList<>(16);

    // Map of moves for each piece player owns
    private Map<Piece, Set<Move>> moves = new HashMap<>(16);

    public enum Color {
        WHITE {
            @Override
            public String toString() {
                return "w";
            }
        },
        BLACK {
            @Override
            public String toString() {
                return "b";
            }
        }
    }

    public Player(Color color) {
        this.color = color;
    }

    /**
     * Get the color of the player
     * @return what color this player is
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Add a piece for the player
     * @param piece the piece to add
     */
    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    /**
     * Capture a piece from opposing player
     * @param player the opposing player
     * @param piece the piece to capture
     */
    public void capturePiece(Player player, Piece piece) {
        player.getCapturedPieces().add(piece);
        player.getPieces().remove(piece);
    }

    /**
     * Get list of pieces that player has lost
     * @return the list of pieces the player no longer controls
     */
    public List<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }

    /**
     * Get list of pieces that player controls
     * @return a list of pieces that the player still controls
     */
    public List<Piece> getPieces() {
        return this.pieces;
    }

    /**
     * Populate all possible moves given the current game state
     * NOTE - This is to be called whenever performing a move or initializing the game
     * @param board the current board to populate moves from / for
     */
    public void populateMoves(Board board) {

        // Clear out valid moves
        moves.clear();

        // Consumer for generating the move for a piece
        Consumer<Tile> generateMoves = tile -> {
            Piece pieceOnTile = tile.getPiece();
            moves.put(pieceOnTile, pieceOnTile.generateMoves(board, tile));
        };

        // Filter out pieces not owned by me
        Predicate<Tile> sameOwner = tile -> tile.isOccupied() && this.equals(tile.getPiece().getOwner());

        // Generate moves for all pieces owned by me
        board.getTileMap().values().stream().filter(sameOwner).forEach(generateMoves);
    }

    /**
     * Get all moves for this player (even if they aren't valid) and might put their king in check
     * @return all moves for this player given current board state
     */
    public Map<Piece, Set<Move>> getMovesForPieces() {
        return this.moves;
    }

    /**
     * Checks if a player has any valid moves
     * @param board the board to check
     * @return true if has valid moves, false if not
     */
    public boolean hasValidMoves(Board board) {
        // TODO -> This throws overflow exception
        //return true;
        return getPieces().stream().anyMatch(piece -> piece.getValidMoves(board).size() > 0);
    }

    /**
     * Is the player white?
     * @return true if white, false if black
     */
    public boolean isWhite() {
        return this.getColor() == Color.WHITE;
    }

    /**
     * Get opposing player on game board
     * @param board the board the players belong to
     * @return the opposing player
     */
    public Player opposite(Board board) {
        Color otherColor = isWhite() ? Color.BLACK : Color.WHITE;
        return board.getPlayers().get(otherColor);
    }

    @Override
    public String toString() {
        return isWhite() ? Color.WHITE.toString() : Color.BLACK.toString();
    }
}
