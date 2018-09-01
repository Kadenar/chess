package com.chess.engine.moves;

import com.chess.engine.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveHistory {

    private Move lastMove;
    private List<Move> allMoves;
    private Map<Player, List<Move>> movesByPlayers;

    public MoveHistory() {
        allMoves = new ArrayList<>();
        movesByPlayers = new HashMap<>();
    }

    /**
     * Add a move to the move history
     * @param Player the current Player moving
     * @param move the move performed
     */
    public void addMove(Player player, Move move) {
        lastMove = move;
        allMoves.add(move);
        getMoveHistoryForPlayer(player).add(move);
    }

    public Move getLatestMove() {
        return lastMove;
    }

    /**
     * Get all moves performed in a given game
     * @return list of all of the moves performed
     */
    public List<Move> getAllMoves() {
        return this.allMoves;
    }

    /**
     * Get move history for a given player
     * @param Player the player
     * @return the list of moves performed by this player
     */
    public List<Move> getMoveHistoryForPlayer(Player player) {
        return movesByPlayers.getOrDefault(player, new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(Move move : getAllMoves()) {
            builder.append(i).append(". ") // append move number
                    .append(move.getMovedPiece()) // append the piece we moved
                    .append(" ").append(move.getDestination().getPosition()); // append the location we moved too
            builder.append(" "); // add a space between each move
            i++;
        }

        return builder.toString();
    }

}
