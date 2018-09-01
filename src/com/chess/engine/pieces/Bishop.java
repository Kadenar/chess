package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MovePositions;

import java.util.HashSet;
import java.util.Set;

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
    public Set<Move> generateMoves(Board board, Tile currentPosition) {
        Set<Move> validPositions = new HashSet<>();

        // Up 1, over 1
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.UP, true));

        // Down 1, over 1
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentPosition,
                                                                Direction.DOWN, true));

        // Return valid positions that the bishop can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "B" : "b";
    }
}
