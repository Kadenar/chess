package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.List;

public abstract class Piece {

    private Player owner;
    private Position position;
    private boolean hasMoved;

    public Piece(Player color) {
        this(color, null);
    }

    public Piece(Player color, Position position) {
        this.owner = color;
        this.position = position;
        this.hasMoved = false;
    }

    abstract public String getPieceImagePath();
    abstract public List<Position> createPossibleMoves();
    abstract public String toString();

    boolean hasMoved() {
        return this.hasMoved;
    }

    public void setHasMoved(boolean moved) {
        if(this.hasMoved) return; // Don't allow setting back to false for now...
        this.hasMoved = moved;
    }

    public Player getOwner() {
        return this.owner;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public List<Position> getMoves() {
        return createPossibleMoves();
    }

    // Overridden for pawn, knight and king
    public int getMaxSpacesMoved() {
        return 8;
    }
}
