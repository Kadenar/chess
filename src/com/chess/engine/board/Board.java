package com.chess.engine.board;

import com.chess.engine.utils.BoardUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board {

    // All tiles for the current board
    private Map<String, Tile> tileMap = new LinkedHashMap<>();

    // The players for the current board
    private Map<String, Player> players = new HashMap<>();

    public Board(String fen) {
        this.players.put(Player.WHITE.toString(), Player.WHITE);
        this.players.put(Player.BLACK.toString(), Player.BLACK);
        BoardUtils.getInstance().updateBoardFromFen(this, fen);
    }

    // GETTERS
    public Map<String, Tile> getTileMap() { return this.tileMap; }
    public Map<String, Player> getPlayers() { return this.players; }

    /**
     * String representation of the board
     * 8 [r] [n] [b] [q] [k] [b] [n] [r]
     * 7 [p] [p] [p] [p] [p] [p] [p] [p]
     * 6 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 5 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 4 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 3 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 2 [P] [P] [P] [P] [P] [P] [P] [P]
     * 1 [R] [N] [B] [Q] [K] [N] [B] [R]
     *    a   b   c   d   e   f   g   h
     * @return can be used to print out the board in a string representation
     */
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
