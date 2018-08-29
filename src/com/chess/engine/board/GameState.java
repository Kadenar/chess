package com.chess.engine.board;

import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    // Lists of white / black captured pieces
    private List<Piece> whiteCaptured;
    private List<Piece> blackCaptured;

    // Is the game over?
    private boolean gameOver;

    // Which player's turn it is
    private Player playerTurn;

    private Position whiteKingPos;
    private Position blackKingPos;

    // State of castling / enPassant
    private String castling;
    private Position enPassant;

    // Number of halfmoves / full moves
    private int halfMoves, fullMoves;

    // Initialize singleton instance
    private static final GameState INSTANCE = getInstance();

    /**
     * Get the current game state of a game
     * @return the current game state of a given game
     */
    public static GameState getInstance() {
        if(INSTANCE == null) {
            return new GameState(Player.WHITE);
        }

        return INSTANCE;
    }

    /**
     * Game state instance to hold game specific information
     * @param turn which player's turn it is
     */
    private GameState(Player turn) {
        this.playerTurn = turn;
        gameOver = false;
        whiteCaptured = new ArrayList<>();
        blackCaptured = new ArrayList<>();

        // TODO - time controls

        // TODO player in check

        // TODO player in checkmate

        // TODO stalemate
    }

    // Getters
    public Player getPlayerTurn() { return this.playerTurn; }
    public String getCastles() { return castling; }
    public Position getEPSquare() { return enPassant; }
    public int getFullMoves() { return fullMoves; }
    public int getHalfMoves() { return halfMoves; }

    // Setters
    public void setPlayerTurn(Player player) { this.playerTurn = player; }
    public void setCastlingAbility(String castlingAbility) { this.castling = castlingAbility; }
    public void setEnpassantSquare(Position enpassantSquare) { this.enPassant = enpassantSquare; }
    public void setHalfMoves(int halfMoves) { this.halfMoves = halfMoves; }
    public void setFullMoves(int fullMoves) { this.fullMoves = fullMoves; }

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
     * Check whether given player can king side castle
     * @param player the player to check
     * @return true if can king side castle, false if not
     */
    public boolean canCastleKingSide(Player player) {
        return player.isWhite() ? castling.contains("K") : castling.contains("k");
    }

    /**
     * Check whether given player can queen side castle
     * @param player the player to check
     * @return true if can queen side castle, false if not
     */
    public boolean canCastleQueenSide(Player player) {
        return player.isWhite() ? castling.contains("Q") : castling.contains("q");
    }

    /**
     * Add a piece to capture pieces
     * @param piece the piece that was captured
     */
    public void addCapturedPiece(Piece piece) {
        if(piece.getOwner().isWhite()) {
            whiteCaptured.add(piece);
        } else {
            blackCaptured.add(piece);
        }
    }

    /**
     * Get pieces that have been captured by black
     * @return white pieces that have been captured
     */
    public List<Piece> getWhiteCaptured() {
        return whiteCaptured;
    }

    /**
     * Get pieces that have been captured by white
     * @return black pieces that have been captured
     */
    public List<Piece> getBlackCaptured() {
        return blackCaptured;
    }
}
