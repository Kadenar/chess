package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.PlayerColor;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {

    public Pawn(Player color) {
        super(color, "pawn.png");
    }

    /**
     * Return the direction for enpassant depending on Player color
     * @return 1 or -1 depending on Player color
     */
    public int getEnpassantDirection() {
        return getOwner().getColor().equals(PlayerColor.WHITE) ? 1 : -1;
    }

    /**
     * A pawn can only move forward
     * - If first move, it can move 2 tiles, otherwise only 1 at a time
     * - It can move diagonally to capture a piece
     * @param currentTile the current tile the king is located on
     * @return list of valid moves the king can make
     */
    @Override
    Set<Move> generateMoves(Board board, Tile currentTile) {
        Set<Move> validPositions = new HashSet<>();

        // Vertical movement based on color
        Direction dir  = getOwner().isWhite() ? Direction.UP : Direction.DOWN;

        int rowOffSet;
        if(dir == Direction.UP) {
            rowOffSet = 1;
        } else {
            rowOffSet = -1;
        }

        // First check if the pawn can move in the forward direction
        validPositions.addAll(addPositionsForOffset(board, currentTile, 0, rowOffSet));

        // Next check if the pawn can move in the diagonal direction
        validPositions.addAll(addPositionsForDirection(board, currentTile, dir, true));

        return validPositions;
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
