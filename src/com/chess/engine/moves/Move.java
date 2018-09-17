package com.chess.engine.moves;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

public class Move {

    private final Piece moved;
    private final Piece captured;
    private final Tile fromTile;
    private final Tile toTile;

    public Move(final Move move) {
        this(move.moved, move.fromTile, move.captured, move.toTile);
    }
    public Move(final Piece pieceMoved, final Tile from, final Piece pieceCaptured, final Tile to) {
        this.moved = pieceMoved;
        this.fromTile = from;
        this.captured = pieceCaptured;
        this.toTile = to;
    }

    /**
     * Get the piece that is being moved
     * @return the piece being moved
     */
    public Piece getMovedPiece() { return this.moved; }

    /**
     * Get the piece that was captured from this move
     * @return the piece that was captured, or null if there was no capture
     */
    public Piece getCapturedPiece() { return this.captured; }

    /**
     * Get the origin tile the move comes from
     * @return the Tile to move from
     */
    public Tile getOrigin() { return this.fromTile; }

    /**
     * Get destination tile the move goes to
     * @return the Tile to move to
     */
    public Tile getDestination() {
        return this.toTile;
    }

    /**
     * Determine whether the given move is enpassant move
     * @return {@code true} if moved piece was a pawn and moved 2 squares, {@code false} if not
     */
    public boolean isEnpassantMove() {
        return getMovedPiece() instanceof Pawn && BoardUtils.deltaRow(getOrigin(), getDestination()) == 2;
    }

    /**
     * Did this move capture a piece?
     * @return {@code true}
     */
    public boolean isCapture() {
        return getCapturedPiece() != null;
    }

    /**
     * Is this an enpassant capture?
     * @param enpassantLoc the current enpassant tile position
     * @return {@code true} if enpassant capture, {@code false} otherwise
     */
    private boolean isEnpassantCapture(Position enpassantLoc) {
        return getMovedPiece() instanceof Pawn && getDestination().getPosition().equals(enpassantLoc);
    }

    /**
     * Determine whether the given move is a pawn promoting
     * @return {@code true} if moved piece was a pawn and promotion square, {@code false} otherwise
     */
    public boolean isPromotion() {
        return getMovedPiece() instanceof Pawn
                && getDestination().getPosition().isPromotionSquare(getMovedPiece().getOwner());
    }

    /**
     * Determine whether the given move is a king castling
     * @return {@code true}  if moved piece is a king and castling, {@code false} otherwise
     */
    private boolean isKingCastle() {
        return getMovedPiece() instanceof King && BoardUtils.deltaCol(getOrigin(), getDestination()) > 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromTile.hashCode(), toTile.hashCode());
    }

    /**
     * Check whether a given move is the same as another
     * @param other the other move to compare against
     * @return if the origin and destination tiles are the same
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Move)) return false;
        Move mov = (Move) other;
        return this.fromTile.equals(mov.fromTile) && this.toTile.equals(mov.toTile);
    }

    /**
     * String representation of the destination position
     * @return the destination tile's position as a string
     */
    @Override
    public String toString() {
        return getMovedPiece() + ": "
                + getOrigin().getPosition().toString()
                + " -> " + getDestination().getPosition().toString();
    }

    /**
     * Execute given move on a board
     * @param board the {@code Board} to perform the move on
     * @param isTestBoard {@code true} if this is a test board,
     *                    {@code false} if it is the actual baord
     */
    public void execute(Board board, boolean isTestBoard) {
        MoveType typeOfMove = null;
        Map<Position, Tile> tileMap = board.getTileMap();
        Position targetPosition = getDestination().getPosition();
        Position moveFromPosition = getOrigin().getPosition();
        Piece movedPiece = getMovedPiece();
        Player currentPlayer = movedPiece.getOwner();
        Player opposingPlayer = currentPlayer.opposite(board);

        Piece capturedPiece = getCapturedPiece();
        boolean isEnpassantCapture = isEnpassantCapture(board.getGameState().getEPSquare());

        // If moving the king
        if (movedPiece instanceof King) {

            // Update the king position
            board.setKingPosition(movedPiece.getOwner(), getDestination().getPosition());

            // If king moved > 1 square, move the corresponding rook as well
            if (isKingCastle()) {
                typeOfMove = MoveType.CASTLE;

                // Determine whether it was a king or queen side castle
                boolean queenSideCastle = targetPosition.getColumn() < moveFromPosition.getColumn();

                // Get the current rook position
                int columnOffSet = queenSideCastle ? -2 : 1;
                Tile rookTile = tileMap.get(BoardUtils.getOffSetPosition(targetPosition, columnOffSet, 0));

                // Get the rook's new position
                columnOffSet = queenSideCastle ? 1 : -1;
                Tile newRookTile = tileMap.get(BoardUtils.getOffSetPosition(targetPosition, columnOffSet, 0));

                // Move the rook to new position
                newRookTile.setPiece(rookTile.getPiece());
                rookTile.setPiece(null);
            }
        }

        // Add the captured piece to list captured and play capture sound
        if (isCapture()) {
            typeOfMove = MoveType.CAPTURE;
            currentPlayer.capturePiece(opposingPlayer, capturedPiece);
        }
        // If this was an en passant capture, then remove the captured pawn
        else if (isEnpassantCapture) {
            typeOfMove = MoveType.CAPTURE;

            // Determine the location of the pawn to be captured
            int rowOffset = ((Pawn) movedPiece).getEnpassantDirection() * -1;
            Tile capturedPawnTile = tileMap.get(BoardUtils.getOffSetPosition(targetPosition, 0, rowOffset));

            // Capture the pawn
            capturedPiece = capturedPawnTile.getPiece();
            currentPlayer.capturePiece(opposingPlayer, capturedPiece);
            capturedPawnTile.setPiece(null);
        }

        // Add piece to dragged to tile and remove from originating tile
        if(isPromotion()) {
            getDestination().setPiece(new Queen(currentPlayer));
            currentPlayer.getPieces().remove(movedPiece);
            // TODO on promotion, the pawn doesn't get removed from the UI
        } else {
            getDestination().setPiece(movedPiece);
        }

        // Remove piece from originating tile
        getOrigin().setPiece(null);

        // Add our move to the move history
        board.getMoveHistory().update(this);

        // Need to clear previous set of moves for player who just moved
        // Update each player's moves for next turn
        int fullMoves = board.getGameState().getFullMoves();

        // Clear moves if white, since black moves on the same turn
        if(currentPlayer.isWhite()) {
            board.getMovesForTurn(fullMoves, opposingPlayer).clear();
            board.getMovesForTurn(fullMoves, currentPlayer).clear();
        }

        // Regenerate moves for players (current player first)
        board.generateMovesForPlayer(currentPlayer, fullMoves);
        board.generateMovesForPlayer(opposingPlayer, fullMoves);

        // If current player checked opponent, play checking sound
        if(MoveUtils.isKingInCheck(board, currentPlayer, opposingPlayer) != null) {
            typeOfMove = MoveType.CHECK;
        }
        // Otherwise play standard move sound if no sound assigned yet
        else if(typeOfMove == null) {
            typeOfMove = MoveType.REGULAR;
        }

        // Play corresponding sound based on type of move if not performing test move
        if(!isTestBoard) {
            typeOfMove.playSound();
        }
    }
}
