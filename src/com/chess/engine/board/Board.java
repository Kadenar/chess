package com.chess.engine.board;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.pieces.King;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board {

    // All tiles for the current board
    private Map<String, Tile> tileMap = new LinkedHashMap<>(64);

    // The players for the current board
    private Map<String, Player> players = new HashMap<>(2);

    // The current game state
    private GameState gameState;

    // This is set when loading board from FEN string
    private Position whiteKingPos;
    private Position blackKingPos;

    /**
     * Create a new board with representing the given fen string
     * @param fen the fen string to present on a chessboard
     */
    public Board(String fen) {
        this.players.put(Player.WHITE.toString(), Player.WHITE);
        this.players.put(Player.BLACK.toString(), Player.BLACK);
        this.gameState = new GameState(Player.WHITE);
        BoardUtils.updateBoardFromFen(this, fen);
    }

    /**
     * Clone the other board to create another instance
     * @param otherBoard the board to clone
     */
    public Board(Board otherBoard) {
        //this(FenUtils.getFen(otherBoard));
        // TODO remove this
        this.players = otherBoard.players;
        this.gameState = otherBoard.gameState;
        this.tileMap = otherBoard.tileMap;
        this.whiteKingPos = otherBoard.whiteKingPos;
        this.blackKingPos = otherBoard.blackKingPos;
    }

    // GETTERS
    public Map<String, Tile> getTileMap() { return this.tileMap; }
    public Map<String, Player> getPlayers() { return this.players; }
    public GameState getGameState() { return this.gameState; }

    /**
     * Update the position if the king piece with new position
     * @param kingPiece the king piece to update
     * @param newPosition the new position of the king
     */
    public void setKingPosition(King kingPiece, Position newPosition) {
        if(kingPiece.getOwner().isWhite()) {
            whiteKingPos = newPosition;
        } else {
            blackKingPos = newPosition;
        }
    }

    /**
     * Get the white king's current position
     * @return the positino of the white king
     */
    public Position getWhiteKingPosition() {
        return whiteKingPos;
    }

    /**
     * Get the black king's current position
     * @return the position of the black king
     */
    public Position getBlackKingPosition() {
        return blackKingPos;
    }
    /**
     * String representation of the board
     * 8 [r] [n] [b] [q] [k] [b] [n] [r]
     * 7 [p] [p] [p] [p] [p] [p] [p] [p]
     * 6 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 5 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 4 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 3 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
     * 2 [P] [P] [P] [P] [P] [P] [P] [P]
     * 1 [R] [N] [B] [Q] [K] [N] [B] [R]
     *    a   b   c   d   e   f   g   h
     * @return can be used to print out the board in a string representation
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        final int[] i = {0};
        getTileMap().values().forEach( tile -> {
            if(i[0] == 0 || i[0] % 8 == 0) {
                builder.append(7 - i[0] / 8 + 1).append(" ");
            }

            builder.append(String.format("%3s", tile.toString()));

            if ((i[0] + 1) % 8 == 0) {
                builder.append("\n");
            }
            i[0]++;
        });

        builder.append("   a  b  c  d  e  f  g  h");
        return builder.toString();
    }
}
