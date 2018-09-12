package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;

import java.util.HashSet;
import java.util.Set;

public class Queen extends Piece {

    public Queen(Player color) {
        super(color, "queen.png");
    }

    /**
     * A queen can move in all directions
     * @param currentPosition the current tile the queen is located on
     * @return list of valid moves the queen can make
     */
    @Override
    Set<Move> generateMoves(Board board, Tile currentPosition) {
        Set<Move> validPositions = new HashSet<>();

        // Diagonal movement
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition,
                                                                Direction.UP, true));
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition,
                                                                Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition,
                                                                Direction.UP, false));
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition,
                                                                Direction.DOWN, false));

        // Horizontal movement
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition,
                                                                Direction.RIGHT, false));
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition,
                                                                Direction.LEFT, false));

        // Return valid positions that the queen can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "Q" : "q";
    }
}
