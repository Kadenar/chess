package com.chess.engine.moves;

import java.util.ArrayList;
import java.util.List;

public class MoveHistory {

    private Move lastMove;
    private List<Move> allMoves;

    public MoveHistory() {
        this.allMoves = new ArrayList<>();
    }

    /**
     * Add a move to the move history
     * @param move the move performed
     */
    void addMove(Move move) {
        this.lastMove = move;
        this.allMoves.add(move);
    }

    /**
     * Get the most recent move that was performed
     * @return the last move performed
     */
    public Move getLatestMove() {
        return this.lastMove;
    }

    /**
     * Reset all moves
     */
    public void reset() {
        this.allMoves.clear();
    }
}
