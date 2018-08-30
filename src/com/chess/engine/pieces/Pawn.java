package com.chess.engine.pieces;

import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;
import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.MovePositions;

import java.util.List;

public class Pawn extends Piece {

    public Pawn(Player color) {
        super(color, "pawn.png");
    }

    /**
     * Return the direction for enpassant depending on player color
     * @return 1 or -1 depending on player color
     */
    public int getEnpassantDirection() {
        return getOwner().equals(Player.WHITE) ? 1 : -1;
    }

    /**
     * A pawn can only move forward
     * - If first move, it can move 2 tiles, otherwise only 1 at a time
     * - It can move diagonally to capture a piece
     * @param currentPosition the current tile the king is located on
     * @return list of valid moves the king can make
     */
    @Override
    public List<Move> generateValidMoves(Board board, Tile currentPosition) {
        // Vertical movement based on color
        Direction dir  = getOwner().isWhite() ? Direction.UP : Direction.DOWN;

        // Return valid positions that the pawn can move to
        return MovePositions.addPositionsForPawn(board, this, currentPosition, dir);
    }

    /**
     * The maximum number of spaces a pawn can move
     * @return 2 is the max spaces a pawn can move (assuming on home row)
     */
    @Override
    public int getMaxSpacesMoved() {
        return 2;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "P" : "p";
    }
}
