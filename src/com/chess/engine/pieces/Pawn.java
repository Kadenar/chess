package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.utils.MoveUtils;
import com.chess.engine.utils.MoveUtils.Direction;

import java.util.List;

public class Pawn extends Piece {

    public Pawn(Player color) {
        super(color, "pawn.png");
    }

    // Determine direction for enpassant
    public int getEnpassantDirection() {
        if(getOwner().isSameSide(Player.WHITE)) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public List<Position> createPossibleMoves(Position currentPosition) {
        // Vertical movement
        Direction dir  = getOwner().isWhite() ? Direction.UP : Direction.DOWN;

        // Return valid positions that the pawn can move to
        return MoveUtils.addPositionsForPawn(this, currentPosition, dir);
    }

    @Override
    public int getMaxSpacesMoved() {
        return hasMoved() ? 1 : 2;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "P" : "p";
    }
}
