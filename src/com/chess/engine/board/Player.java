package com.chess.engine.board;

import com.chess.engine.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.BoardUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Player {
    WHITE,
    BLACK;

    private List<Piece> pieces = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

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
