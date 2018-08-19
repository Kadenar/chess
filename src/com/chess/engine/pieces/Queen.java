package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.MoveUtils.Direction;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Player color) {
        super(color, "queen.png");
    }

    @Override
    public List<Position> createPossibleMoves(Position currentPosition) {
        List<Position> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                Direction.UP, true));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                Direction.DOWN, false));

        // Right
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                Direction.RIGHT, false));

        // Left
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                Direction.LEFT, false));

        // Return valid positions that the queen can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "Q" : "q";
    }
}
