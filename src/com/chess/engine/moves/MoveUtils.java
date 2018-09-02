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
     * Performs a test move to determine whether it is valid to actually do that move
     * @param board the board to copy
     * @param originatingTile the tile to move from
     * @param targetTile the tile to move to
     * @return true if move can be performed, false if not
     */
    public static boolean performTestMove(Board board, Tile originatingTile, Tile targetTile) {
        Board testBoard = new Board(board);
        Tile originTile = testBoard.getTileMap().get(originatingTile.getPosition().toString());
        Piece draggedPiece = originTile.getPiece();
        Player currentPlayer = draggedPiece.getOwner();
        Tile destinationTile = testBoard.getTileMap().get(targetTile.getPosition().toString());
        Player opposingPlayer = currentPlayer.opposite(testBoard);

        // Perform a test move
        updateGameState(testBoard, originTile, destinationTile, true);

        // If the current player is in check by other player, then move is not valid
        return isKingInCheck(testBoard, opposingPlayer, currentPlayer) == null;
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

        // Perform the actual piece move
        updateGameState(board, originatingTile, targetTile, false);

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
        || draggedToTile == null   || draggedToTile.equals(originatingTile)) {
            return false;
        }

        Piece draggedPiece = originatingTile.getPiece();

        // Check if the originating piece being moved came from Player whose turn it is
        if(!draggedPiece.getOwner().equals(board.getGameState().getPlayerTurn())) {
            return false;
        }

        Piece capturedPiece = draggedToTile.getPiece();

        // If the destination tile is occupied by a piece of Player who is moving
        if(draggedToTile.isOccupied() && capturedPiece.sameSide(draggedPiece)) {
            return false;
        }

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        return draggedPiece.getValidMoves(board).contains(new Move(draggedPiece, originatingTile, capturedPiece, draggedToTile));
    }

    /**
     * Perform a piece move updating game state and user interface
     * @param board the board we are updating
     * @param tileToMoveFrom the tile to remove dragged piece from
     * @param tileToMoveTo the tile we dragged the piece to
     * @param testMove is this just a test move?
     */
    private static void updateGameState(Board board, Tile tileToMoveFrom, Tile tileToMoveTo, boolean testMove) {
        MoveType typeOfMove = null;

        // Get our current game state
        GameState state = board.getGameState();
        Piece draggedPiece = tileToMoveFrom.getPiece();
        Player currentPlayer = draggedPiece.getOwner();
        Position moveFromPosition = tileToMoveFrom.getPosition();
        Piece capturedPiece = tileToMoveTo.getPiece();
        Player opposingPlayer = currentPlayer.opposite(board);
        Position targetPosition = tileToMoveTo.getPosition();
        boolean isPawnMove = draggedPiece instanceof Pawn;
        boolean isKingMove = draggedPiece instanceof King;
        boolean isEnpassantCapture = targetPosition.equals(state.getEPSquare());
        boolean isRegularCapture = tileToMoveTo.isOccupied();

        /*
         * Update our game state variables
         */

        // This counter is reset after captures or pawn moves, and incremented otherwise
        state.setHalfMoves(isPawnMove || isRegularCapture ? 0 : state.getHalfMoves() + 1);
        // Update Player turn and full moves
        state.setPlayerTurn(opposingPlayer);
        state.setFullMoves(state.getFullMoves() + 1);

        // Sets en passant square if last move was pawn move that spanned 2 rows
        if (isPawnMove && BoardUtils.deltaRow(tileToMoveFrom, tileToMoveTo) == 2) {
            // En passant position is multiplied by -1 because dragged piece is opposite color of what we desire
            int rowOffset = currentPlayer.isWhite() ? ((Pawn) draggedPiece).getEnpassantDirection() : ((Pawn) draggedPiece).getEnpassantDirection() * -1;
            Position enpassantSquare = BoardUtils.getOffSetPosition(targetPosition,0, rowOffset);
            board.getGameState().setEnpassantSquare(enpassantSquare);
        } else {
            board.getGameState().setEnpassantSquare(null);
        }

        // If moving the rook or king, then castling is no longer allowed
        if(isKingMove || draggedPiece instanceof Rook) {
            updateCastling(state, opposingPlayer);
            // TODO -> If moving rook, determine which side and don't allow castling to that side anymore
        }

        /*
         * Update the actual board state / pieces
         */

        // If we moved the king
        if(isKingMove) {

            // Update the king position
            board.setKingPosition((King)draggedPiece, targetPosition);

            // If king moved > 1 square, move the corresponding rook as well
            if(BoardUtils.deltaCol(tileToMoveFrom, tileToMoveTo) > 1) {
                boolean queenSideCastle = targetPosition.getColumn() < moveFromPosition.getColumn();
                Position rookPos = BoardUtils.getOffSetPosition(targetPosition, queenSideCastle ? -2 : 1, 0);
                Tile rookTile = board.getTileMap().get(rookPos.toString());
                Position newRookPos = BoardUtils.getOffSetPosition(targetPosition, queenSideCastle ? 1 : -1, 0);
                Tile newRookTile = board.getTileMap().get(newRookPos.toString());
                newRookTile.setPiece(rookTile.getPiece());
                rookTile.setPiece(null);
                typeOfMove = MoveType.CASTLE;
            }
        }

        // Add the captured piece to list captured and play capture sound
        if(tileToMoveTo.isOccupied()) {
            currentPlayer.capturePiece(opposingPlayer, capturedPiece);
            typeOfMove = MoveType.CAPTURE;
        }
        // If this was an en passant capture, then remove the captured pawn
        else if(isPawnMove && isEnpassantCapture) {
            int rowOffset = currentPlayer.isWhite() ? ((Pawn) draggedPiece).getEnpassantDirection() : ((Pawn) draggedPiece).getEnpassantDirection() * -1;
            Position pawnCapturedPosition = BoardUtils.getOffSetPosition(targetPosition,0, rowOffset);
            Tile capturedPawnTile = board.getTileMap().get(pawnCapturedPosition.toString());
            capturedPiece = capturedPawnTile.getPiece();
            currentPlayer.capturePiece(opposingPlayer, capturedPiece);
            capturedPawnTile.setPiece(null);
            typeOfMove = MoveType.CAPTURE;
        }

        // Add piece to dragged to tile and remove from originating tile
        tileToMoveTo.setPiece(draggedPiece);
        tileToMoveFrom.setPiece(null);

        // Add our move to the move history
        board.getMoveHistory().addMove(new Move(draggedPiece, tileToMoveFrom, capturedPiece, tileToMoveTo));

        // Populate moves for current game state
        board.getPlayers().values().forEach(player -> player.populateMoves(board));

        // If check move, play checking sound
        if(isKingInCheck(board, currentPlayer, opposingPlayer) != null) {
            typeOfMove = MoveType.CHECK;
        }
        // Otherwise play standard move sound if no sound assigned yet
        else if(typeOfMove == null) {
            typeOfMove = MoveType.REGULAR;
        }

        // Play corresponding sound based on type of move if not performing test move
        if(!testMove) {
            typeOfMove.playSound();
        }

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
    public static Piece isTileTargeted(Player targetingPlayer, Position destination) {
        // Get all valid moves for opposing player
        // And check if that piece has a move with same location as the destination tile
        Predicate<Move> movesMatch = move -> move.getDestination().getPosition().equals(destination);
        return targetingPlayer.getMovesForPieces().values().stream()
                .flatMap(Collection::stream)
                .filter(movesMatch)
                .map(Move::getMovedPiece).findFirst().orElse(null);
    }

    /**
     * Check whether a given Player is in check
     * @param board the board to check the king position on
     * @param playerToCheck the player to determine if they can deliver / are delivering check
     * @param ownerOfKing the owner of the king
     * @return true if in check, false if not
     */
    private static Piece isKingInCheck(Board board, Player playerToCheck, Player ownerOfKing) {
        Position kingPosition = ownerOfKing.isWhite() ? board.getWhiteKingPosition() : board.getBlackKingPosition();
        return isTileTargeted(playerToCheck, kingPosition);
    }
}
