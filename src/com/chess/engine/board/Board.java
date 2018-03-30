package com.chess.engine.board;

import com.chess.engine.utils.BoardUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board {
    private Map<String, Tile> tileMap = new LinkedHashMap<>();
    private Map<String, Player> players = new HashMap<>();

    public Board() {
        this("");
    }

    public Board(String fen) {
        // Add both players
        this.players.put(Player.WHITE.toString(), Player.WHITE);
        this.players.put(Player.BLACK.toString(), Player.BLACK);

        // Load in the default position
        BoardUtils.getInstance().updateBoardWithFen(this, fen);
    }

    // GETTERS
    public Map<String, Tile> getTileMap() { return this.tileMap; }
    public Map<String, Player> getPlayers() { return this.players; }

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
