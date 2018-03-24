package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.List;

public class Pawn extends Piece {

    public Pawn(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "pawn.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        return null;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "P" : "p";
    }
}
