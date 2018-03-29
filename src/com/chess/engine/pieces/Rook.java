package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
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
        List<Position> validPositions = new ArrayList<>();

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.DOWN, false));

        // Horizontal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.RIGHT, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.LEFT, false));

        // Return valid positions that the rook can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "R" : "r";
    }
}
