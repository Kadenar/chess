package com.chess.engine.board;

import com.chess.engine.Player;
import com.chess.engine.Position;

public class GameState {

    // Is the game over?
    private boolean gameOver;

    // Which player's turn it is
    private Player playerTurn;

    // State of castling / enPassant
    private String castling;
    private Position enPassant;

    // Number of halfmoves / full moves
    private int halfMoves, fullMoves;

    /**
     * Game state instance to hold game specific information
     * @param turn which player's turn it is
     */
    public GameState(Player turn) {
        this.playerTurn = turn;
        gameOver = false;

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
}
