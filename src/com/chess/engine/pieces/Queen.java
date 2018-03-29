package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.MoveUtils.Direction;

import java.util.ArrayList;
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
        List<Position> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.UP, true));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.DOWN, false));

        // Right
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.RIGHT, false));

        // Left
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, Direction.LEFT, false));

        // Return valid positions that the queen can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "Q" : "q";
    }
}
