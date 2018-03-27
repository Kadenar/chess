package com.chess.engine;

import com.chess.engine.board.Tile;

public class Move {

    private final Tile fromTile;
    private final Tile toTile;

    public Move(Tile from, Tile to) {
        fromTile = from;
        toTile = to;
    }

    public boolean equals(Move other) {
        return this.fromTile == other.fromTile && this.toTile == other.toTile;
    }

}
