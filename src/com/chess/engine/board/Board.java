package com.chess.engine.board;

import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board {
    private Map<String, Tile> tileMap = new LinkedHashMap<>();
    private Map<String, Player> players = new HashMap<>();

    // Variables used to load/save FEN:
    // Full FEN string
    private String fen;

    // Which player's turn it is
    private Player currentMove;

    // State of castling / enPassant
    private String castling, enPassant;

    // Number of halfmoves / full moves
    private int halfMoves, fullMoves;

    public Board() {
        this("");
    }

    public Board(String fen) {
        // Add both players
        this.players.put(Player.WHITE.toString(), Player.WHITE);
        this.players.put(Player.BLACK.toString(), Player.BLACK);

        // Load in the default position
        try {
            BoardUtils.getInstance().updateBoardWithFen(this, fen);
        } catch(Exception e) {
            System.out.println("Error encountered parsing fen");
            e.printStackTrace();
        }
    }

    // SETTERS
    public void setCurrentFen(String fen) { this.fen = fen; }
    public void setCurrentMove(Player currentMove) { this.currentMove = currentMove; }
    public void setCastlingAbility(String castlingAbility) { this.castling = castlingAbility; }
    public void setEnpassantSquare(String enpassantSquare) { this.enPassant = enpassantSquare; }
    public void setHalfMoves(int halfMoves) { this.halfMoves = halfMoves; }
    public void setFullMoves(int fullMoves) { this.fullMoves = fullMoves; }

    // GETTERS
    public Map<String, Tile> getTileMap() { return this.tileMap; }
    public Map<String, Player> getPlayers() { return this.players; }
    public String getCurrentFen() { return fen; }

    // DEBUGGING
    public void printFen() {
        String fen = getCurrentFen();
        // Print the current fen
        System.out.println(fen);
        System.out.println("=========");
        System.out.println("Board positions: " + FenUtils.getGamePieces(fen));
        System.out.println("Turn: " + currentMove.toString());
        System.out.println("Castling: " + castling);
        System.out.println("EnPassant: " + enPassant);
        System.out.println("Half moves: " + halfMoves);
        System.out.println("Full moves: " + fullMoves);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        int i = 0;
        for(Map.Entry<String, Tile> entry : getTileMap().entrySet()) {
            if(i == 0 || i % 8 == 0) {
                builder.append(7 - i / 8 + 1).append(" ");
            }

            builder.append(String.format("%3s", entry.getValue().toString()));

            if ((i + 1) % 8 == 0) {
                builder.append("\n");
            }
            i++;
        }

        builder.append("   a  b  c  d  e  f  g  h");
        return builder.toString();
    }
}
