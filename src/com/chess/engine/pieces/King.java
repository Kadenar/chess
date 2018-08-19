package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Player color) {
        super(color, "king.png");
    }

    @Override
    public List<Position> createPossibleMoves(Position currentPosition) {
        List<Position> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.UP, true));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.DOWN, false));

        // Right
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.RIGHT, false));

        // Left
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.LEFT, false));

        // castling ability
        //TODO need to add in ability for the king to castle with either rook
        if(!hasMoved()) {

        }

        // Return valid positions that the king can move to
        return validPositions;
    }

    @Override
    public int getMaxSpacesMoved() {
        return 1;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "K" : "k";
    }
}
