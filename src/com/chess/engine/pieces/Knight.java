package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;

import java.util.List;

public class Knight extends Piece {

    public Knight(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "knight.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        // Return valid positions that the knight can move to
        return MoveUtils.addPositionsForKnight(this);
    }

    @Override
    public int getMaxSpacesMoved() {
        return 1;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "N" : "n";
    }
}
