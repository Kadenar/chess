package com.chess.engine.moves;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.chess.engine.sound.SoundUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public static boolean performTestMove(Board board, Tile originatingTile, Tile targetTile) {
        Board testBoard = new Board(board);
        Tile mockOriginatingTile = testBoard.getTileMap().get(originatingTile.toString());
        Tile mockTargetTile = testBoard.getTileMap().get(targetTile.toString());

        if(!canMovePiece(testBoard, mockOriginatingTile, mockTargetTile)) {
            return false;
        }

        Piece draggedPiece = mockOriginatingTile.getPiece();
        Position targetPosition = mockTargetTile.getPosition();
        Player currentPlayer = draggedPiece.getOwner();
        boolean isKingMove = draggedPiece instanceof King;
        GameState state = testBoard.getGameState();

        // If we moved the king, update the king position
        if(isKingMove) {
            testBoard.setKingPosition((King)draggedPiece, mockTargetTile.getPosition());
            updateCastling(state, draggedPiece.getOwner().opposite());
        }

        // TODO -> If rook moved, then castling is no longer allowed for that side

        // If king moved > 1 square, update castling and play different sound
        if(isKingMove && BoardUtils.deltaCol(mockOriginatingTile, mockTargetTile) > 1) {
            // Queen side castle or king side castle?
            boolean queenSideCastle = targetPosition.getColumn() < originatingTile.getPosition().getColumn();
            Tile rookTile = testBoard.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? -2 : 1, 0).toString());
            Tile newRookTile = testBoard.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? 1 : -1, 0).toString());
            newRookTile.setPiece(rookTile.getPiece());
            rookTile.setPiece(null);
        }
        // Add the captured piece to list captured
        else if(targetTile.isOccupied()) {
            currentPlayer.capturePiece(currentPlayer.opposite(), mockTargetTile.getPiece());
        }

        // Add piece to dragged to tile always
        mockOriginatingTile.setPiece(null);
        mockTargetTile.setPiece(draggedPiece);

        // If check move, play checking sound
        if(isKingInCheck(testBoard, draggedPiece.getOwner().opposite())) {
            return false;
        }

        return true;
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

        // Check if the originating piece being moved came from player whose turn it is
        if(!draggedPiece.getOwner().equals(board.getGameState().getPlayerTurn())) {
            return false;
        }

        // If the destination tile is occupied by a piece of player who is moving
        if(draggedToTile.isOccupied() && draggedToTile.getPiece().sameSide(draggedPiece)) {
            return false;
        }

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        return draggedPiece.getMoves().contains(new Move(originatingTile, draggedToTile));
    }

    /**
     * Perform a piece move updating game state and user interface
     * @param board the board we are updating
     * @param tileToMoveFrom the tile to remove dragged piece from
     * @param tileToUpdate the tile we dragged the piece to
     */
    private static void updateGameState(Board board, Tile tileToMoveFrom, Tile tileToUpdate) {
        MoveType typeOfMove = null;

        // Get our current game state
        GameState state = board.getGameState();

        Piece draggedPiece = tileToMoveFrom.getPiece();
        Position targetPosition = tileToUpdate.getPosition();
        Player currentPlayer = draggedPiece.getOwner();
        boolean isPawnMove = draggedPiece instanceof Pawn;
        boolean isKingMove = draggedPiece instanceof King;
        boolean isEnpassantCapture = targetPosition.equals(state.getEPSquare());
        boolean isRegularCapture = tileToUpdate.isOccupied();

        // This counter is reset after captures or pawn moves, and incremented otherwise
        state.setHalfMoves(isPawnMove || isRegularCapture ? 0 : state.getHalfMoves() + 1);

        // If we moved the king, update the king position
        if(isKingMove) {
            board.setKingPosition((King)draggedPiece, tileToUpdate.getPosition());
            updateCastling(state, draggedPiece.getOwner().opposite());
        }
        // TODO -> If moving rook, determine which side and don't allow castling to that side anymore
        else if(draggedPiece instanceof Rook) {
            updateCastling(state, draggedPiece.getOwner().opposite());
        }


        // If king moved > 1 square, update castling and play different sound
        if(isKingMove && BoardUtils.deltaCol(tileToMoveFrom, tileToUpdate) > 1) {
            // Queen side castle or king side castle?
            boolean queenSideCastle = targetPosition.getColumn() < tileToMoveFrom.getPosition().getColumn();
            Tile rookTile = board.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? -2 : 1, 0).toString());
            Tile newRookTile = board.getTileMap().get(targetPosition.getOffSetPosition(queenSideCastle ? 1 : -1, 0).toString());
            newRookTile.setPiece(rookTile.getPiece());
            rookTile.setPiece(null);
            typeOfMove = MoveType.CASTLE;
        }
        // Add the captured piece to list captured and play capture sound
        else if(tileToUpdate.isOccupied()) {
            currentPlayer.capturePiece(currentPlayer.opposite(), tileToUpdate.getPiece());
            typeOfMove = MoveType.CAPTURE;
        }

        // Sets en-passant square if last move was pawn move that spanned 2 rows
        boolean didPawnMove2Spaces = isPawnMove && BoardUtils.deltaRow(tileToMoveFrom, tileToUpdate) == 2;
        state.setEnpassantSquare(didPawnMove2Spaces
                ? tileToMoveFrom.getPosition().getOffSetPosition(0, ((Pawn) draggedPiece).getEnpassantDirection())
                : null);

        // Update player turn and full moves
        state.setPlayerTurn(currentPlayer.opposite());
        state.setFullMoves(state.getFullMoves() + 1);

        // Update the user interface
        updatePiecesOnBoard(board, tileToMoveFrom, tileToUpdate, isPawnMove && isEnpassantCapture);

        // Populate moves for current game state
        Player.WHITE.populateMoves(board);
        Player.BLACK.populateMoves(board);


        // If check move, play checking sound
        if (isKingInCheck(board, draggedPiece.getOwner().opposite())) {
            typeOfMove = MoveType.CHECK;
        }
        // Otherwise play standard move sound if no sound assigned yet
        else if(typeOfMove == null){
            typeOfMove = MoveType.REGULAR;
        }

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
     * Update piece locations in the UI
     * @param board the board being played on
     * @param tileToMoveFrom the tile that we move the piece from
     * @param tileToCaptureOn the tile that we perform the capture on and replace pieces
     * @param enpassantCapture whether this was an enpassant capture
     */
    private static void updatePiecesOnBoard(Board board, Tile tileToMoveFrom, Tile tileToCaptureOn,
                                            boolean enpassantCapture) {
        Piece draggedPiece = tileToMoveFrom.getPiece();

        // If this was an Enpassant capture
        if(enpassantCapture) {

            // Enpassant position is multipled by -1 because dragged piece is opposite color of what we desire
            Position enpassantPosition = tileToCaptureOn.getPosition()
                    .getOffSetPosition(0, -1 * ((Pawn) draggedPiece).getEnpassantDirection());
            Tile enpassantTile = board.getTileMap().get(enpassantPosition.toString());

            // Remove piece that is being replaced by new piece
            if(enpassantTile.getComponents().length > 0) {
                enpassantTile.remove(0);
            }

            // Set the new piece
            enpassantTile.setPiece(null);

        }
        // If this was a standard capture
        else {
            // Remove piece that is being replaced by new piece
            if(tileToCaptureOn.getComponents().length > 0) {
                tileToCaptureOn.remove(0);
            }
        }

        // Add captured piece to content panel
        // TODO - boardUI.getCapturedPanel().addCaptured(tileToCaptureOn.getPiece());

        // Add piece to dragged to tile always
        tileToCaptureOn.setPiece(draggedPiece);

        // Remove dragged piece from previous tile
        if(tileToMoveFrom.getComponents().length > 0) {
            tileToMoveFrom.remove(0);
        }
        tileToMoveFrom.setPiece(null);
    }

    /**
     * Check whether a given player is in check
     * @param board the board to check
     * @param playerToCheck the player to check
     * @return true if in check, false if not
     */
    private static boolean isKingInCheck(Board board, Player playerToCheck) {
        Position kingPosition = playerToCheck.isWhite() ? board.getWhiteKingPosition() :  board.getBlackKingPosition();
        return isTileTargeted(playerToCheck, kingPosition);
    }

    /**
     * Check whether a given tile is targeted
     * @param playerToCheck the player to check
     * @param destination the destination tile to consider
     * @return true if the tile is targeted, false if not
     */
    public static boolean isTileTargeted(Player playerToCheck, Position destination) {
        // Get all valid moves for opposing player
        // And check if that piece has a move with same location as the destination tile
        Map<Piece, List<Move>> opposingPlayerValidMoves = playerToCheck.opposite().getAllValidMoves();
        return opposingPlayerValidMoves.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(e -> e.getDestination().getPosition().equals(destination));
    }
}
