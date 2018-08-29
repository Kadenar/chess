package com.chess.engine;

import com.chess.engine.board.Player;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.BoardUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardMoves {
    private Map<Piece, List<Move>> whiteValidMoves;
    private Map<Piece, List<Move>> blackValidMoves;
    private static final BoardMoves INSTANCE = getInstance();

    public static BoardMoves getInstance() {
        if(INSTANCE == null) {
            return new BoardMoves();
        }

        return INSTANCE;
    }

    // This object can only reference one game at a time
    private BoardMoves() {
        whiteValidMoves = new HashMap<>();
        blackValidMoves = new HashMap<>();
    }

    /**
     * Populate all possible moves given the current game state
     * NOTE - This should be called whenever performing a move or initializing the game
     */
    public final void populateMoves() {
        // Clear out valid moves
        whiteValidMoves.clear();
        blackValidMoves.clear();

        Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();
        for(Map.Entry<String, Tile> entry : tiles.entrySet()) {
            Tile currentTile = entry.getValue();
            // If the tile is occupied
            if(currentTile.isOccupied()) {
                // Get all valid moves for each piece
                Piece pieceOnTile = currentTile.getPiece();

                // Populate moves for corresponding player for each piece
                if(pieceOnTile.getOwner().isWhite()) {
                    whiteValidMoves.put(pieceOnTile, pieceOnTile.createPossibleMoves(currentTile));
                } else {
                    blackValidMoves.put(pieceOnTile, pieceOnTile.createPossibleMoves(currentTile));
                }
            }
        }
    }

    /**
     * Get all of the valid moves for white
     * @return all valid moves for white player
     */
    public final Map<Piece, List<Move>> getWhiteValidMoves() {
        return this.whiteValidMoves;
    }

    /**
     * Get all of the valid moves for black
     * @return all valid moves for black player
     */
    public final Map<Piece, List<Move>> getBlackValidMoves() {
        return this.blackValidMoves;
    }
}
