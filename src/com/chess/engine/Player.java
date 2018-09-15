package com.chess.engine;

import com.chess.ChessConsts;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player {

    // Player's color
    private final Color color;

    // Pieces owner by the player
    private List<Piece> ownedPieces;

    // Pieces captured by opponent
    private List<Piece> capturedPieces;

    // The available colors for a player
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

    /**
     * Create a new {@code Player} instance with a given {@code Color}
     * @param color the {@code Color} of the player
     */
    public Player(Color color) {
        this.color = color;
        this.ownedPieces = new ArrayList<>(ChessConsts.MAX_PIECES);
        this.capturedPieces = new ArrayList<>(ChessConsts.MAX_PIECES);
    }

    /**
     * Create a new {@code Player} instance with same info as the other {@code Player}
     * @param other the {@code Player} to copy
     */
    public Player(Player other) {
        this.color = other.color;
        this.ownedPieces = new ArrayList<>(other.ownedPieces);
        this.capturedPieces = new ArrayList<>(other.capturedPieces);
    }

    /**
     * Get the color of the player
     * @return what {@code Color} this player is
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Add a piece for the player
     * @param piece the {@code Piece} to add as controlled
     */
    public void addPiece(Piece piece) {
        this.ownedPieces.add(piece);
    }

    /**
     * Capture a piece from opposing player
     * @param player the opposing {@code Player}
     * @param piece the {@code Piece} to capture
     */
    public void capturePiece(Player player, Piece piece) {
        player.getCapturedPieces().add(piece);
        player.getPieces().remove(piece);
    }

    /**
     * Get list of pieces that player has lost
     * @return a {@code List<Piece>} of pieces the player no longer controls
     */
    public List<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }

    /**
     * Get list of pieces that player controls
     * @return a {@code List<Piece>} of pieces that the player still controls
     */
    public List<Piece> getPieces() {
        return this.ownedPieces;
    }

    /**
     * Checks if a player has any valid moves
     * @param board the {@code Board} to check
     * @return {@code true} if has valid moves, {@code false} if no valid moves
     */
    public boolean hasValidMove(Board board) {
        int fullMoves = board.getGameState().getFullMoves();
        Predicate<Piece> hasValidMoves = piece -> board.getValidMovesForPiece(fullMoves, piece)
                                                       .stream().findFirst().isPresent();

        return getPieces().stream().filter(hasValidMoves).collect(Collectors.toList()).size() > 0;
        //return true;
    }

    /**
     * Is the given player white?
     * @return {@code true} if white, {@code false} if black
     */
    public boolean isWhite() {
        return getColor() == Color.WHITE;
    }

    /**
     * Get opposing {@code Color}
     * @return if player is white, returns black. otherwise, returns white.
     */
    private Color opposite() {
        return isWhite() ? Color.BLACK : Color.WHITE;
    }

    /**
     * Get opposing player on game board
     * @param board the {@code Board} the players are playing on
     * @return the opposing {@code Player}
     */
    public Player opposite(Board board) {
        return board.getPlayers().get(opposite());
    }

    @Override
    public String toString() {
        return getColor().toString();
    }
}
