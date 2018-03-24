package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.List;

public class Bishop extends Piece {

    public Bishop(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "bishop.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        return null;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "B" : "b";
    }
}
