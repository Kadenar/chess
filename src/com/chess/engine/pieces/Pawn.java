package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.MoveUtils.Direction;

import java.util.List;

public class Pawn extends Piece {

    public Pawn(Player color, Position position) {
        super(color, position);
    }

    public boolean canMoveTwoSquares() {
        return !hasMoved();
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "pawn.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        // Vertical movement
        Direction d  = getOwner().isWhite() ? Direction.UP : Direction.DOWN;

        // Return valid positions that the pawn can move to
        return MoveUtils.addPositionsForPawn(this, d);
    }

    @Override
    public int getMaxSpacesMoved() {
        return hasMoved() ? 1 : 2;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "P" : "p";
    }
}
