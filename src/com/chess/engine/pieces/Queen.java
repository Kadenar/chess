package com.chess.engine.pieces;

import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;
import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.MovePositions;

import java.util.ArrayList;
import java.util.List;

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
    public List<Move> generateValidMoves(Board board, Tile currentPosition) {
        List<Move> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.UP, true));
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.UP, false));
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.DOWN, false));

        // Right
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.RIGHT, false));

        // Left
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.LEFT, false));

        // Return valid positions that the queen can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "Q" : "q";
    }
}
