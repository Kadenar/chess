package com.chess.engine.pieces;

import com.chess.engine.Move;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Player;
import com.chess.engine.board.Tile;
import com.chess.engine.utils.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Player color) {
        super(color, "king.png");
    }

    /**
     * A king can move in all directions 1 square unless castling
     * @param currentPosition the current tile the king is located on
     * @return list of valid moves the king can make
     */
    @Override
    public List<Move> createPossibleMoves(Tile currentPosition) {
        List<Move> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.UP, true));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.UP, false));
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.DOWN, false));

        // Right
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.RIGHT, false));

        // Left
        validPositions.addAll(MoveUtils.addPositionsForDirection(this, currentPosition,
                                                                MoveUtils.Direction.LEFT, false));

        // castling ability king side
        if(GameState.getInstance().canCastleKingSide(getOwner())) {
            validPositions.addAll(MoveUtils.addKingSideCastlePosition(currentPosition));
        }

        // castling ability queen side
        if(GameState.getInstance().canCastleQueenSide(getOwner())) {
            validPositions.addAll(MoveUtils.addQueenSideCastlePosition(currentPosition));
        }

        // Return valid positions that the king can move to
        return validPositions;
    }

    /**
     * A king can only move 1 square at a time
     * @return 1
     */
    @Override
    public int getMaxSpacesMoved() {
        return 1;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "K" : "k";
    }
}
