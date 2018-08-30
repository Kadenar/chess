package com.chess.engine.pieces;

import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;
import com.chess.engine.board.Board;
import com.chess.engine.Player;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.MovePositions;
import com.chess.engine.moves.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Player color) {
        super(color, "king.png");
    }

    /**
     * A king can move in all directions 1 square unless castling
     * @param currentTile the current tile the king is located on
     * @return list of valid moves the king can make
     */
    @Override
    public List<Move> generateValidMoves(Board board, Tile currentTile) {
        List<Move> validPositions = new ArrayList<>();

        // Diagonal movement
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentTile,
                Direction.UP, true));
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentTile,
                Direction.DOWN, true));

        // Vertical movement
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentTile,
                Direction.UP, false));
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentTile,
                Direction.DOWN, false));

        // Right
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentTile,
                Direction.RIGHT, false));

        // Left
        validPositions.addAll(MovePositions.addPositionsForDirection(board, this, currentTile,
                Direction.LEFT, false));

        // castling ability king side
        if(board.getGameState().canCastleKingSide(getOwner())) {
            validPositions.addAll(MovePositions.addKingSideCastlePosition(board, this, currentTile.getPosition()));
        }

        // castling ability queen side
        if(board.getGameState().canCastleQueenSide(getOwner())) {
            validPositions.addAll(MovePositions.addQueenSideCastlePosition(board, this, currentTile.getPosition()));
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
