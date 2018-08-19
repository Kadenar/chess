package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Player color) {
        super(color, "bishop.png");
    }

    @Override
    public List<Position> createPossibleMoves(Position currentPosition) {
        List<Position> validPositions = new ArrayList<>();

        // Up 1, over 1
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.UP, true));

        // Down 1, over 1
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.DOWN, true));

        // Return valid positions that the bishop can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "B" : "b";
    }
}
