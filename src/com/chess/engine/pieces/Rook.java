package com.chess.engine.pieces;

import com.chess.engine.Move;
import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(Player color) {
        super(color, "rook.png");
    }

    @Override
    public List<Move> createPossibleMoves(Tile currentPosition) {
        List<Move> validPositions = new ArrayList<>();

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.DOWN, false));

        // Horizontal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.RIGHT, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.LEFT, false));

        // Return valid positions that the rook can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "R" : "r";
    }
}
