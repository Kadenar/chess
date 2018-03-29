package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "bishop.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        List<Position> validPositions = new ArrayList<>();

        // Up 1, over 1
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.UP, true));

        // Down 1, over 1
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, MoveUtils.Direction.DOWN, true));

        // Return valid positions that the bishop can move to
        return validPositions;
    }

    /**
     * Add all valid coordinate positions for a given offset based on the current position
     * @param currentPos the current position
     * @param xOffSet the x offset (should always be 1 or -1)
     * @param yOffSet the y offset (should always be 1 or -1)
     */
    private List<Position> addPositionsForDiag(Position currentPos, int xOffSet, int yOffSet) {
        List<Position> positionsForDiag = new ArrayList<>();

        Position offSetPos = currentPos.getOffSetPosition(xOffSet, yOffSet);
        while(offSetPos.isValidCoord()) {
            positionsForDiag.add(offSetPos);
            offSetPos = offSetPos.getOffSetPosition(xOffSet, yOffSet);
        }

        return positionsForDiag;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "B" : "b";
    }
}
