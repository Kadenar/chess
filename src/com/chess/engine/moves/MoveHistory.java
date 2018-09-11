package com.chess.engine.moves;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MoveHistory {

    // Last move that was performed
    private Move lastMove;

    // All moves that have been performed
    private final List<Move> allMoves;

    // The board move history is for
    private final Board board;

    public MoveHistory(Board board) {
        this.board = board;
        this.lastMove = null;
        this.allMoves = new ArrayList<>();
    }

    public MoveHistory(MoveHistory other) {
        this.board = other.board;
        this.lastMove = other.lastMove;
        this.allMoves = other.allMoves;
    }

    /**
     * Add a move to the move history
     * @param move the {@code Move} performed
     */
    void addMove(Move move) {
        this.lastMove = move;
        this.allMoves.add(move);
    }

    /**
     * Get a list of moves based on filter condition
     * @return get a {@code Stream<Move>} based on filtering condition
     */
    public Stream<Move> getMoves(Predicate<Move> filter) { return this.allMoves.stream().filter(filter); }

    /**
     * Get the most recent move that was performed
     * @return the last {@code Move} performed
     */
    public Move getLatestMove() {
        return this.lastMove;
    }

    /**
     * Remove the latest move
     */
    public void undo() {
        allMoves.remove(getLatestMove());
        // TODO
    }

    /**
     * Redo the most recent move
     */
    public void redo() {
       // TODO
    }

    /**
     * Reset all moves
     */
    public void reset() {
        this.allMoves.clear();
    }

    /**
     * Get notation of the latest move
     * @return the {@code String} notation entry for history
     */
    public String getNotationEntry(Move move) {

        if(move == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        Piece movedPiece = move.getMovedPiece();
        Piece takenPiece = move.getCapturedPiece();
        Tile movedFromTile = move.getOrigin();
        Position movedFrom = movedFromTile.getPosition();
        Tile movedToTile = move.getDestination();
        Position movedTo = movedToTile.getPosition();

        // If last move was a pawn move
        if(movedPiece instanceof Pawn) {
            // If the pawn took a piece, append just the column the pawn came from
            if(takenPiece != null) {
                builder.append(movedFrom.toString(), 0, 1);
                builder.append("x");
            }
            builder.append(movedTo);
        }
        // If last move was a king castle
        else if(movedPiece instanceof King && BoardUtils.deltaCol(movedFromTile, movedToTile) > 1) {
            if(movedTo.getColumn() > movedFrom.getColumn()) {
                builder.append("O-O");
            } else {
                builder.append("O-O-O");
            }
        }
        // If last move was not a pawn move or king castling
        else {
            builder.append(movedPiece.toString().toUpperCase());

            if(takenPiece != null) {
                builder.append("x");
            }
            builder.append(movedTo);
        }

        Player movingPlayer = movedPiece.getOwner();
        if(MoveUtils.isTileTargeted(movingPlayer, board.getKingPosition(movingPlayer.opposite(board))) != null) {
            builder.append("+");
        }

        return builder.toString();
    }
}
