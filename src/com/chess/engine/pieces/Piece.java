package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.List;

public abstract class Piece {

    private Player owner;
    private Position position;

    public Piece(Player color) {
        this(color, null);
    }

    public Piece(Player color, Position position) {
        this.owner = color;
        this.position = position;
    }

    abstract public String getPieceImagePath();
    abstract public List<Position> createPossibleMoves();
    abstract public String toString();

    public Player getOwner() {
        return this.owner;
    }

    public Position getPosition() {
        return this.position;
    }

    public List<Position> getMoves() {
        return createPossibleMoves();
    }
}
