package com.chess.engine;

import com.chess.ChessConsts;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Player {

    // Player's color
    private final PlayerColor color;

    // Pieces owner by the player
    private final Set<Piece> ownedPieces;

    // Pieces captured by opponent
    private final Set<Piece> capturedPieces;

    /**
     * Create a new {@code Player} instance with a given {@code Color}
     * @param color the {@code Color} of the player
     */
    public Player(PlayerColor color) {
        this.color = color;
        this.ownedPieces = new HashSet<>(ChessConsts.MAX_PIECES);
        this.capturedPieces = new HashSet<>(ChessConsts.MAX_PIECES);
    }

    /**
     * Create a new {@code Player} instance with same info as the other {@code Player}
     * @param other the {@code Player} to copy
     */
    public Player(Player other) {
        this.color = other.color;
        this.ownedPieces = new HashSet<>(other.ownedPieces);
        this.capturedPieces = new HashSet<>(other.capturedPieces);
    }

    /**
     * Get the color of the player
     * @return what {@code Color} this player is
     */
    public PlayerColor getColor() {
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
     * Get set of pieces that player has lost
     * @return a {@code Set<Piece>} of pieces the player no longer controls
     */
    public Set<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }

    /**
     * Get set of pieces that player controls
     * @return a {@code Set<Piece>} of pieces that the player still controls
     */
    public Set<Piece> getPieces() {
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
        return getPieces().stream().anyMatch(hasValidMoves);
    }

    /**
     * Is the given player white?
     * @return {@code true} if white, {@code false} if black
     */
    public boolean isWhite() {
        return getColor() == PlayerColor.WHITE;
    }

    /**
     * Get opposing {@code Color}
     * @return if player is white, returns black. otherwise, returns white.
     */
    private PlayerColor opposite() {
        return isWhite() ? PlayerColor.BLACK : PlayerColor.WHITE;
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
