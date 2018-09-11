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
    private int halfMoves, fullMoves;

    /**
     * Game state instance to hold game specific information
     * @param turn which {@code Player} turn it is
     */
    GameState(Player turn) {
        this.playerTurn = turn;
        this.gameOver = false;
        this.stalemate = false;
        this.castling = "";
        this.enPassant = null;
        this.halfMoves = 0;
        this.fullMoves = 0;

        // TODO - time controls
    }

    /**
     * Game state instance with the same values as another given game state
     * @param otherState the {@code GameState} to copy
     */
    GameState(GameState otherState) {
        this.playerTurn = otherState.playerTurn;
        this.gameOver = otherState.gameOver;
        this.stalemate = otherState.stalemate;
        this.castling = otherState.castling;
        this.enPassant = otherState.enPassant;
        this.halfMoves = otherState.halfMoves;
        this.fullMoves = otherState.fullMoves;
    }


    // Getters
    public Player getPlayerTurn() { return this.playerTurn; }
    public String getCastles() { return this.castling; }
    public Position getEPSquare() { return this.enPassant; }
    public int getFullMoves() { return this.fullMoves; }
    public int getHalfMoves() { return this.halfMoves; }
    public boolean isStaleMate() { return this.stalemate; }
    public boolean isGameOver() { return this.stalemate || this.gameOver; }

    // Setters
    public void setPlayerTurn(Player player) { this.playerTurn = player; }
    public void setCastlingAbility(String castlingAbility) { this.castling = castlingAbility; }
    public void setEnpassantSquare(Position enpassantSquare) { this.enPassant = enpassantSquare; }
    public void setHalfMoves(int halfMoves) { this.halfMoves = halfMoves; }
    public void setFullMoves(int fullMoves) { this.fullMoves = fullMoves; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

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
               "Game over: " + isGameOver();
    }
}
