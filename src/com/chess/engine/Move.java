package com.chess.engine;

import com.chess.engine.board.Tile;

public class Move {

    private final Tile fromTile;
    private final Tile toTile;
    private final boolean promotion;

    public Move(final Move move) {
        this(move.fromTile, move.toTile, move.promotion);
    }
    public Move(final Tile from, final Tile to) {
        this(from, to, false);
    }

    public Move(final Tile from, final Tile to, final boolean promote) {
        this.fromTile = from;
        this.toTile = to;
        this.promotion = promote;
    }

    // Don't need to check for promotion for equivalent
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Move)) return false;
        Move mov = (Move) other;
        return this.fromTile == mov.fromTile && this.toTile == mov.toTile;
    }
}
