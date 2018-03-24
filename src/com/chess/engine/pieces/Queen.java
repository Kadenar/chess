package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.List;

public class Queen extends Piece {

    public Queen(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "queen.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        return null;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "Q" : "q";
    }
}
