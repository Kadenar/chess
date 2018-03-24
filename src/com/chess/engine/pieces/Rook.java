package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.List;

public class Rook extends Piece {

    public Rook(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "rook.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        return null;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "R" : "r";
    }
}
