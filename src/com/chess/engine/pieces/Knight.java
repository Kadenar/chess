package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MovePositions;

import java.util.Set;

public class Knight extends Piece {

    public Knight(Player color) {
        super(color, "knight.png");
    }

    /**
     * A knight can move in an L pattern:
     * - up two and over 1
     * - down 2 and over 1
     * - up 1 and over 2
     * - down 1 and over 2
     * @param currentPosition the current tile the knight is located on
     * @return list of valid moves the knight can make
     */
    @Override
    public Set<Move> generateMoves(Board board, Tile currentPosition) {
        // Return valid positions that the knight can move to
        return MovePositions.addPositionsForKnight(board, this, currentPosition);
    }

    /**
     * The maximum number of times to check offsets for a knight
     * @return 1
     */
    @Override
    public int getMaxSpacesMoved() {
        return 1;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "N" : "n";
    }
}
