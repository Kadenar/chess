package com.chess.engine.moves;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.Collection;
import java.util.function.Predicate;

public class MoveUtils {

    /**
     * Performs a test move to determine whether it is valid to actually do that move
     * @param board the {@code Board} to copy and test a move against
     * @param originatingTile the {@code Tile} to move from
     * @param targetTile the {@code Tile} to move to
     * @return {@code true} if move can be performed, {@code false} if move cannot be performed
     */
    public static boolean executeTestMove(Board board, Tile originatingTile, Tile targetTile) {
        Board testBoard = new Board(board);
        Tile originTile = testBoard.getTileMap().get(originatingTile.getPosition());
        Piece draggedPiece = originTile.getPiece();
        Player currentPlayer = draggedPiece.getOwner();
        Tile destinationTile = testBoard.getTileMap().get(targetTile.getPosition());
        Player opposingPlayer = currentPlayer.opposite(testBoard);

        // Perform a test move
        Move move = new Move(draggedPiece, originTile, destinationTile.getPiece(), destinationTile);

        // Perform the actual piece move
        updateBoard(testBoard, move, true);

        // Determine whether the test move was valid (current player cannot be in check by opposing player)
        // If the current player is in check by other player, then move is not valid
        return isKingInCheck(testBoard, opposingPlayer, currentPlayer) == null;
    }

    /**
     * Updates the piece displayed on a given tile after checking that the piece can be moved to target tile
     * @param board the {@code Board} to execute the move on
     * @param originatingTile the {@code Tile} the move was executed from
     * @param targetTile the {@code Tile} the move will be executed to
     * @return {@code true} if move was performed, {@code false} if move was not performed
     */
    public static boolean executeActualMove(Board board, Tile originatingTile, Tile targetTile, boolean testMove) {

        // If we can't move the piece just exit
        Move move = canMovePiece(board, originatingTile, targetTile);
        if(move == null) {
            MoveType.INVALID.playSound();
            return false;
        }

        // Perform the actual piece move
        updateBoard(board, move, testMove);

        return true;
    }

    /**
     * Updates the game board by executing the move
     * @param board the current {@code Board}
     * @param move the {@code Move} to perform
     * @param isTestBoard {@code true} if performing a test scenario, {@code false} if this is an actual move
     */
    private static void updateBoard(Board board, Move move, boolean isTestBoard) {

        // TODO -> There has to be a better way to do this than caching this here
        boolean isEP = move.getMovedPiece() instanceof Pawn && board.getGameState().getEPSquare() != null;

        // Update the game state information for the board
        updateGameState(board, move);

        // The type of move we performed (Standard, Capture, Check, or Castle)
        move.execute(board, isEP, isTestBoard);
    }

    /**
     * Determine whether we can move a piece from an originating tile to dragged to tile
     * @param board the {@code Board} to check if the piece can be moved on
     * @param originatingTile the originating {@code Tile} that contains the piece we want to move
     * @param draggedToTile the {@code Tile} we want to move the piece to
     * @return {@code true} if we can move the piece, {@code false} if we can not move the piece
     */
    private static Move canMovePiece(Board board, Tile originatingTile, Tile draggedToTile) {
        // If originating tile or piece are null..
        // Or if the dragged to tile is null or the same as original, just exit
        if(originatingTile == null || originatingTile.getPiece() == null
        || draggedToTile == null   || draggedToTile.equals(originatingTile)) {
            return null;
        }

        Piece draggedPiece = originatingTile.getPiece();
        Piece capturedPiece = draggedToTile.getPiece();

        // Check if the originating piece being moved came from Player whose turn it is
        // Or If the destination tile is occupied by a piece of Player who is moving
        if(!draggedPiece.getOwner().equals(board.getGameState().getPlayerTurn())
         || draggedToTile.isOccupied() && capturedPiece.sameSide(draggedPiece)) {
            return null;
        }

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        Move moveToPerform = new Move(draggedPiece, originatingTile, capturedPiece, draggedToTile);
        if(board.getValidMovesForPiece(board.getGameState().getFullMoves(), draggedPiece).contains(moveToPerform)) {
            return moveToPerform;
        }

        return null;
    }

    /**
     * Update the game state after performing a given move
     * @param board the {@code Board} we are updating
     * @param move the {@code Move} being performed and to update game state with
     */
    private static void updateGameState(Board board, Move move) {
        // Get our current game state
        GameState gameState = board.getGameState();
        Piece draggedPiece = move.getMovedPiece();
        Player currentPlayer = draggedPiece.getOwner();
        Player opposingPlayer = currentPlayer.opposite(board);

        // This counter is reset after captures or pawn moves, and incremented otherwise
        gameState.setHalfMoves(draggedPiece instanceof Pawn || move.isCapture() ? 0 : gameState.getHalfMoves() + 1);

        // Update Player turn and full moves
        gameState.setPlayerTurn(opposingPlayer);

        // If moving player was black, increment the move counter
        int prevFullMoves = gameState.getFullMoves();
        if (!currentPlayer.isWhite()) {
            gameState.setFullMoves(prevFullMoves + 1);
        }

        // Sets en passant square if last move was pawn move that spanned 2 rows
        if (move.isEnpassantMove()) {
            // En passant position is multiplied by -1 because dragged piece is opposite color of what we desire
            int rowOffset = ((Pawn) draggedPiece).getEnpassantDirection() * -1;
            gameState.setEnpassantSquare(BoardUtils.getOffSetPosition(move.getDestination().getPosition(), 0, rowOffset));
        }
        // Clear en passant square
        else {
            gameState.setEnpassantSquare(null);
        }

        // If moving the rook or king, then castling is no longer allowed
        if (draggedPiece instanceof King || draggedPiece instanceof Rook) {
            // TODO -> If moving rook, determine which side and don't allow castling to that side anymore
            updateCastling(gameState, opposingPlayer);
        }
    }

    /**
     * Check whether a given tile is targeted by a specific player
     * @param board the {@code Board} to check on
     * @param targetingPlayer the {@code Player} to check if they target a tile with a piece
     * @param destination the destination {@code Tile} to consider
     * @return {@code true} if the tile is targeted, {@code false} if not
     */
    public static Piece isTileTargeted(Board board, Player targetingPlayer, Position destination) {
        // Get all moves for opposing player
        // And check if that piece has a move with same location as the destination tile
        Predicate<Move> movesMatch = move -> move.getDestination().getPosition().equals(destination);
        return board.getMovesForTurn(board.getGameState().getFullMoves(), targetingPlayer)
                .values().stream().flatMap(Collection::stream)
                .filter(movesMatch).map(Move::getMovedPiece).findFirst().orElse(null);
    }

    /**
     * Check whether a given {@code Player} is in check
     * @param board the {@code Board} to check the king position on
     * @param targetingPlayer the {@code Player} to determine if they can deliver / are delivering check
     * @param ownerOfKing the {@code Player} who owns the king
     * @return {@code true} if in owner of king is in check, {@code false} if not
     */
    public static Piece isKingInCheck(Board board, Player targetingPlayer, Player ownerOfKing) {
        return isTileTargeted(board, targetingPlayer, board.getKingPosition(ownerOfKing));
    }

    /**
     * Update the castling fen value for the current board state
     * @param state the current {@code GameState}
     * @param player the opposing {@code Player}
     */
    private static void updateCastling(GameState state, Player player) {
        StringBuilder castlingAbility = new StringBuilder();
        boolean canCastleKing = state.canCastleKingSide(player);
        boolean canCastleQueen = state.canCastleQueenSide(player);
        // TODO make this suck less
        if(!canCastleKing && !canCastleQueen) {
            castlingAbility.append("-");
        } else {
            if(canCastleKing) {
                castlingAbility.append(player.isWhite() ? "K" : "k");
            }
            if(canCastleQueen) {
                castlingAbility.append(player.isWhite() ? "Q" : "q");
            }
        }

        // Update game state's castling ability
        state.setCastlingAbility(castlingAbility.toString());
    }
}
