package com.chess.engine.pieces;

import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;
import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.MovePositions;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(Player color) {
        super(color, "rook.png");
    }

    /**
     * A rook can move vertically and horizontally
     * @param currentPosition the current tile the rook is located on
     * @return list of valid moves the rook can make
     */
    @Override
    public List<Move> generateValidMoves(Board board, Tile currentPosition) {
        List<Move> validPositions = new ArrayList<>();

        // Vertical movement
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.UP, false));
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.DOWN, false));

        // Horizontal movement
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.RIGHT, false));
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.LEFT, false));

        // Return valid positions that the rook can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "R" : "r";
    }
}
