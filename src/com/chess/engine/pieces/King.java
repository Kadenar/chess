package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "king.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        List<Position> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.UP, true));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.DOWN, false));

        // Right
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.RIGHT, false));

        // Left
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.LEFT, false));

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
