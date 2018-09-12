package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;

import java.util.HashSet;
import java.util.Set;

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
    Set<Move> generateMoves(Board board, Tile currentPosition) {
        Set<Move> validPositions = new HashSet<>();

        // Vertical movement
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition, Direction.UP, false));
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition, Direction.DOWN, false));

        // Horizontal movement
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition, Direction.RIGHT, false));
        validPositions.addAll(addPositionsForDirection(board, this, currentPosition, Direction.LEFT, false));

        // Return valid positions that the rook can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "R" : "r";
    }
}
