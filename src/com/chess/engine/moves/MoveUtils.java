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
import com.chess.engine.sound.SoundUtils;

import java.util.Collection;
import java.util.function.Predicate;

public class MoveUtils {

    private enum MoveType {
        CASTLE {
            @Override
            void playSound() {
                SoundUtils.playMoveSound("castle");
            }
        },
        CAPTURE {
            @Override
            void playSound() {
                SoundUtils.playMoveSound("capture2");
            }
        },
        REGULAR {
            @Override
            void playSound() {
                SoundUtils.playMoveSound("mov2");
            }
        },
        CHECK {
            @Override
            void playSound() {
                SoundUtils.playMoveSound("check1");
            }
        };

        abstract void playSound();

    }

    /**
     * Performs a test move
     * @param board the board to copy
     * @param originatingTile the tile to move from
     * @param targetTile the tile to move to
     * @return true if move can be performed, false if not
     */
    public static boolean performTestMove(Board board, Tile originatingTile, Tile targetTile) {
        Board testBoard = new Board(board);
        GameState state = testBoard.getGameState();
        Tile originTile = testBoard.getTileMap().get(originatingTile.getPosition().toString());
        Tile destinationTile = testBoard.getTileMap().get(targetTile.getPosition().toString());
        Piece draggedPiece = originTile.getPiece();
        Player currentPlayer = draggedPiece.getOwner();
        Position moveFromPosition = originTile.getPosition();
        Position targetPosition = destinationTile.getPosition();
        boolean isPawnMove = draggedPiece instanceof Pawn;
        boolean isKingMove = draggedPiece instanceof King;
        boolean isEnpassantCapture = targetPosition.equals(state.getEPSquare());

        // If we moved the king, update the king position and castling ability
        if(isKingMove) {
            testBoard.setKingPosition((King)draggedPiece, targetPosition);
            updateCastling(state, draggedPiece.getOwner().opposite(board));

            // If king moved > 1 square, move the rook
            if(BoardUtils.deltaCol(originTile, destinationTile) > 1) {
                boolean queenSideCastle = targetPosition.getColumn() < moveFromPosition.getColumn();
                Tile rookTile = testBoard.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? -2 : 1, 0).toString());
                Tile newRookTile = testBoard.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? 1 : -1, 0).toString());
                newRookTile.setPiece(rookTile.getPiece());
                rookTile.setPiece(null);
            }
        }

        // Add the captured piece to list captured and play capture sound
        if(targetTile.isOccupied()) {
            currentPlayer.capturePiece(currentPlayer.opposite(board), destinationTile.getPiece());
        }

        // If this was a pawn move and as an en passant capture, then remove the captured pawn
        if(isPawnMove && isEnpassantCapture) {

            // En passant position is multipled by -1 because dragged piece is opposite color of what we desire
            Position pawnCapturedPosition = targetPosition.getOffSetPosition(0,
                    -1 * ((Pawn) draggedPiece).getEnpassantDirection());

            // Set en passant tile to null
            testBoard.getTileMap().get(pawnCapturedPosition.toString()).setPiece(null);

        }

        // Add piece to dragged to tile and remove from originating tile
        destinationTile.setPiece(draggedPiece);
        originTile.setPiece(null);

        // Populate moves for current game state
        testBoard.getPlayers().values().forEach(player -> player.populateMoves(testBoard));

        // If the current player is in check by other player, then move is not valid
        return !isKingInCheck(testBoard, destinationTile.getPiece().getOwner(), currentPlayer);
    }


    /**
     * Updates the piece displayed on a given tile after checking that the piece can be moved to target tile
     * @param board the board to execute the move on
     * @param originatingTile the tile the move was executed from
     * @param targetTile the tile the move will be executed to
     * @return true if move was performed, false if not
     */
    public static boolean executeMove(Board board, Tile originatingTile, Tile targetTile) {

        // If we can't move the piece just exit
        if(!canMovePiece(board, originatingTile, targetTile)) {
            SoundUtils.playMoveSound("invalid");
            return false;
        }

        // Perform the piece move
        updateGameState(board, originatingTile, targetTile);

        return true;
    }

    /**
     * Determine whether we can move a piece from an originating tile to dragged to tile
     * @param board the board to check if the piece can be moved on
     * @param originatingTile the originating tile that contains the piece we want to move
     * @param draggedToTile the tile we want to move the piece to
     * @return true if we can move the piece, false if not
     */
    private static boolean canMovePiece(Board board, Tile originatingTile, Tile draggedToTile) {
        // If originating tile or piece are null..
        // Or if the dragged to tile is null or the same as original, just exit
        if(originatingTile == null || originatingTile.getPiece() == null
                || draggedToTile == null || draggedToTile.equals(originatingTile)) {
            return false;
        }

        Piece draggedPiece = originatingTile.getPiece();

        // Check if the originating piece being moved came from Player whose turn it is
        if(!draggedPiece.getOwner().equals(board.getGameState().getPlayerTurn())) {
            return false;
        }

        // If the destination tile is occupied by a piece of Player who is moving
        if(draggedToTile.isOccupied() && draggedToTile.getPiece().sameSide(draggedPiece)) {
            return false;
        }

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        return draggedPiece.getValidMoves(board).contains(new Move(draggedPiece, originatingTile, draggedToTile));
    }

    /**
     * Perform a piece move updating game state and user interface
     * @param board the board we are updating
     * @param tileToMoveFrom the tile to remove dragged piece from
     * @param tileToMoveTo the tile we dragged the piece to
     */
    private static void updateGameState(Board board, Tile tileToMoveFrom, Tile tileToMoveTo) {
        MoveType typeOfMove = null;

        // Get our current game state
        GameState state = board.getGameState();
        Piece draggedPiece = tileToMoveFrom.getPiece();
        Position moveFromPosition = tileToMoveFrom.getPosition();
        Position targetPosition = tileToMoveTo.getPosition();
        Player currentPlayer = draggedPiece.getOwner();
        boolean isPawnMove = draggedPiece instanceof Pawn;
        boolean isKingMove = draggedPiece instanceof King;
        boolean isEnpassantCapture = targetPosition.equals(state.getEPSquare());
        boolean isRegularCapture = tileToMoveTo.isOccupied();

        // This counter is reset after captures or pawn moves, and incremented otherwise
        state.setHalfMoves(isPawnMove || isRegularCapture ? 0 : state.getHalfMoves() + 1);
        // Update Player turn and full moves
        state.setPlayerTurn(currentPlayer.opposite(board));
        state.setFullMoves(state.getFullMoves() + 1);

        // If we moved the king, update the king position and castling ability
        if(isKingMove) {
            board.setKingPosition((King)draggedPiece, targetPosition);
            updateCastling(state, draggedPiece.getOwner().opposite(board));

            // If king moved > 1 square, move the rook
            if(BoardUtils.deltaCol(tileToMoveFrom, tileToMoveTo) > 1) {
                boolean queenSideCastle = targetPosition.getColumn() < moveFromPosition.getColumn();
                Tile rookTile = board.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? -2 : 1, 0).toString());
                Tile newRookTile = board.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? 1 : -1, 0).toString());
                newRookTile.setPiece(rookTile.getPiece());
                rookTile.setPiece(null);
                typeOfMove = MoveType.CASTLE;
            }
        }
        // If moving the rook, then castling is no longer allowed on that side
        else if(draggedPiece instanceof Rook) {
            // TODO -> If moving rook, determine which side and don't allow castling to that side anymore
            //updateCastling(state, draggedPiece.getOwner().opposite(board));
        }

        // Add the captured piece to list captured and play capture sound
        if(tileToMoveTo.isOccupied()) {
            currentPlayer.capturePiece(currentPlayer.opposite(board), tileToMoveTo.getPiece());
            typeOfMove = MoveType.CAPTURE;
        }

        // If this was a pawn move
        if(isPawnMove) {

            // Sets en-passant square if last move was pawn move that spanned 2 rows
            boolean didPawnMove2Spaces = BoardUtils.deltaRow(tileToMoveFrom, tileToMoveTo) == 2;
            state.setEnpassantSquare(didPawnMove2Spaces
                    ? moveFromPosition.getOffSetPosition(0, ((Pawn) draggedPiece).getEnpassantDirection())
                    : null);

            // If this was an en passant capture, then remove the captured pawn
            if(isEnpassantCapture) {
                // En passant position is multipled by -1 because dragged piece is opposite color of what we desire
                Position pawnCapturedPosition = targetPosition.getOffSetPosition(0,
                        -1 * ((Pawn) draggedPiece).getEnpassantDirection());

                // Set en passant tile to null
                board.getTileMap().get(pawnCapturedPosition.toString()).setPiece(null);
            }

        }

        // Add piece to dragged to tile and remove from originating tile
        tileToMoveTo.setPiece(draggedPiece);
        tileToMoveFrom.setPiece(null);

        // Populate moves for current game state
        board.getPlayers().values().forEach(player -> player.populateMoves(board));

        // If check move, play checking sound
        if(isKingInCheck(board, currentPlayer, currentPlayer.opposite(board))) {
            typeOfMove = MoveType.CHECK;
        }
        // Otherwise play standard move sound if no sound assigned yet
        else if(typeOfMove == null){
            typeOfMove = MoveType.REGULAR;
        }

        // Add our move to the move history
        board.getMoveHistory().addMove(currentPlayer, new Move(draggedPiece, tileToMoveFrom, tileToMoveTo));

        // Play corresponding sound based on type of move
        typeOfMove.playSound();

    }

    /**
     * Update the castling fen value for the current board state
     * @param player the opposing player
     */
    private static void updateCastling(GameState state, Player player) {
        StringBuilder castlingAbility = new StringBuilder();
        boolean canCastleKing = state.canCastleKingSide(player);
        boolean canCastleQueen = state.canCastleQueenSide(player);

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

    /**
     * Check whether a given tile is targeted by a specific player
     * @param targetingPlayer the player to check if they target a tile with a piece
     * @param destination the destination tile to consider
     * @return true if the tile is targeted, false if not
     */
    public static boolean isTileTargeted(Player targetingPlayer, Position destination) {
        // Get all valid moves for opposing player
        // And check if that piece has a move with same location as the destination tile
        Predicate<Move> movesMatch = move -> move.getDestination().getPosition().equals(destination);
        boolean isTarget = targetingPlayer.getMovesForPieces().values().stream()
                .flatMap(Collection::stream)
                .anyMatch(movesMatch);
        if(isTarget) {
            System.out.println("Player: " + targetingPlayer + " is targeting: " + destination);
        } else {
            System.out.println("Player: " + targetingPlayer + " is not targeting: " + destination);

        }
        return isTarget;
    }

    /**
     * Check whether a given Player is in check
     * @param board the board to check the king position on
     * @param playerToCheck the player to determine if they can deliver / are delivering check
     * @param ownerOfKing the owner of the king
     * @return true if in check, false if not
     */
    private static boolean isKingInCheck(Board board, Player playerToCheck, Player ownerOfKing) {
        Position kingPosition = ownerOfKing.isWhite() ? board.getWhiteKingPosition() : board.getBlackKingPosition();
        return isTileTargeted(playerToCheck, kingPosition);
    }
}
