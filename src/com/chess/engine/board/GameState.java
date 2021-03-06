package com.chess.engine.board;

import com.chess.engine.Player;
import com.chess.engine.Position;

public class GameState {

    // Is the game over?
    private boolean gameOver;

    // Is the game stale mated
    private boolean stalemate;

    // Which player's turn it is
    private Player playerTurn;

    // State of castling / en passant
    private String castling;
    private Position enPassant;

    // Number of half moves / full moves
    private int halfMoves;
    private int fullMoves;

    /**
     * Game state instance to hold game specific information
     */
    GameState() {
        this.playerTurn = null;
        this.gameOver = false;
        this.stalemate = false;
        this.castling = "";
        this.enPassant = null;
        this.halfMoves = 0;
        this.fullMoves = 0;

        // TODO - time controls
    }

    GameState(GameState other) {
        this.playerTurn = other.playerTurn;
        this.gameOver = other.gameOver;
        this.stalemate = other.stalemate;
        this.castling = other.castling;
        this.enPassant = other.enPassant;
        this.halfMoves = other.halfMoves;
        this.fullMoves = other.fullMoves;
    }

    // Getters
    public Player getPlayerTurn() { return this.playerTurn; }
    String getCastles() { return this.castling; }
    public Position getEPSquare() { return this.enPassant; }
    public int getFullMoves() { return this.fullMoves; }
    public int getHalfMoves() { return this.halfMoves; }
    boolean isStaleMate() { return this.stalemate; }

    // Setters
    public void setPlayerTurn(Player player) { this.playerTurn = player; }
    public void setCastlingAbility(String castlingAbility) { this.castling = castlingAbility; }
    public void setEnpassantSquare(Position enpassantSquare) { this.enPassant = enpassantSquare; }
    public void setHalfMoves(int halfMoves) { this.halfMoves = halfMoves; }
    public void setFullMoves(int fullMoves) { this.fullMoves = fullMoves; }
    void setGameOver(boolean isStaleMate) {
        this.gameOver = true;
        this.stalemate = isStaleMate;
    }

    /**
     * Check whether given Player can king side castle
     * @param player the {@code Player} to check
     * @return {@code true} if can king side castle, {@code false} if not
     */
    public boolean canCastleKingSide(Player player) {
        return player.isWhite() ? castling.contains("K") : castling.contains("k");
    }

    /**
     * Check whether given Player can queen side castle
     * @param player the {@code Player} to check
     * @return {@code true} if can queen side castle, {@code false} if not
     */
    public boolean canCastleQueenSide(Player player) {
        return player.isWhite() ? castling.contains("Q") : castling.contains("q");
    }

    /**
     * Print out the current game state
     * @return the {@code String} representation of current game state
     */
    @Override
    public String toString() {
        return "Full moves: " + getFullMoves() + "\n" +
               "Half Moves: " + getHalfMoves() + "\n" +
               "Player turn: " + getPlayerTurn() + "\n" +
               "EP Square: " + getEPSquare() + "\n" +
               "Castling: " + getCastles() + "\n" +
               "Is stale mate: " + isStaleMate() + "\n" +
               "Game over: " + this.gameOver;
    }
}
