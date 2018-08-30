package com.chess.engine;

import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Move;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Player {
    WHITE,
    BLACK;

    private List<Piece> pieces = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();
    private Map<Piece, List<Move>> allValidMoves = new HashMap<>();

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
        return capturedPieces;
    }

    /**
     * Get list of pieces that player controls
     * @return a list of pieces that the player still controls
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * Populate all possible moves given the current game state
     * NOTE - This should be called whenever performing a move or initializing the game
     */
    public void populateMoves(Board board) {
        // Clear out valid moves
        allValidMoves.clear();

        Map<String, Tile> tiles = board.getTileMap();
        tiles.values().stream()
        .filter(Tile::isOccupied) // Filter out unoccupied tiles
        .filter(tile -> tile.getPiece().getOwner().equals(this)) // Filter out pieces not owned by me
        .forEach(tile -> allValidMoves.put(tile.getPiece(), tile.getPiece().generateValidMoves(board, tile)));
    }

    /**
     * Get valid moves for given player
     * @return all valid moves for this player given current board state
     */
    public Map<Piece, List<Move>> getAllValidMoves() {
        return allValidMoves;
    }

    /**
     * Get the opposing player
     * @return opposite color of given Player
     */
    public Player opposite() {
        return isWhite() ? Player.BLACK : Player.WHITE;
    }

    /**
     * Is the player white?
     * @return true if white, false if black
     */
    public boolean isWhite() {
        return this == WHITE;
    }

    @Override
    public String toString() {
        return isWhite() ? "w" : "b";
    }
}
