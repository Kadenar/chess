package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.MoveUtils.Direction;

import java.util.ArrayList;
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
        List<Position> validPositions = new ArrayList<>();

        // Vertical movement
        Direction d  = getOwner().isWhite() ? Direction.UP : Direction.DOWN;
        validPositions.addAll(MoveUtils.addPositionsForPawn(this, d));

        // Diagonal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.UP, true));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.DOWN, true));

        // Return valid positions that the pawn can move to
        return validPositions;
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
