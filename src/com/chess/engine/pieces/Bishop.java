package com.chess.engine.pieces;

import com.chess.engine.Move;
import com.chess.engine.board.Player;
import com.chess.engine.board.Tile;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Player color) {
        super(color, "bishop.png");
    }

    /**
     * A bishop can move in a diagonal direction of the same color tile it is on
     * @param currentPosition the current tile the bishop is located on
     * @return list of valid moves the bishop can make
     */
    @Override
    public List<Move> createPossibleMoves(Tile currentPosition) {
        List<Move> validPositions = new ArrayList<>();

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
