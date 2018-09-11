package com.chess.engine.moves;

import com.chess.engine.GameSettings;
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
        Tile originTile = testBoard.getTileMap().get(originatingTile.getPosition());
        Piece draggedPiece = originTile.getPiece();
        Player currentPlayer = draggedPiece.getOwner();
        Tile destinationTile = testBoard.getTileMap().get(targetTile.getPosition());
        Player opposingPlayer = currentPlayer.opposite(testBoard);

        // Perform a test move
        updateGameState(testBoard, originTile, destinationTile, true);

        // Determine whether the test move was valid
        boolean isValid = isKingInCheck(testBoard, opposingPlayer, currentPlayer) == null;

        // Print out the test board after updating it if we are debugging
        if(GameSettings.INSTANCE.isEnableDebugging()) {
            System.out.println("Current board state:");
            System.out.println(board);
            System.out.println("------------------");
            Move moveToPerform = new Move(draggedPiece, originTile, null, destinationTile);
            System.out.println("Checking if we can perform move: " + moveToPerform);
            System.out.println(testBoard);

            if(isValid) {
                System.out.println("Can perform move!");
            } else {
                System.out.println("Can NOT perform move!");
            }
        }

        // If the current player is in check by other player, then move is not valid
        return isValid;
    }

    /**
     * Updates the piece displayed on a given tile after checking that the piece can be moved to target tile
     * @param board the board to execute the move on
     * @param originatingTile the tile the move was executed from
     * @param targetTile the tile the move will be executed to
     * @return true if move was performed, false if not
     */
    public static boolean executeMove(Board board, Tile originatingTile, Tile targetTile, boolean testMove) {

        // If we can't move the piece just exit
        if(!canMovePiece(board, originatingTile, targetTile)) {
            SoundUtils.playMoveSound("invalid");
            return false;
        }

        // Perform the actual piece move
        updateGameState(board, originatingTile, targetTile, testMove);

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
        Move moveToPerform = new Move(draggedPiece, originatingTile, capturedPiece, draggedToTile);
        return draggedPiece.getValidMoves(board).contains(moveToPerform);
    }

    /**
     * Perform a piece move updating game state and user interface
     * @param board the board we are updating
     * @param tileToMoveFrom the tile to remove dragged piece from
     * @param tileToMoveTo the tile we dragged the piece to
     * @param testMove is this just a test move?
     */
    private static void updateGameState(Board board, Tile tileToMoveFrom, Tile tileToMoveTo, boolean testMove) {

        // The type of move we performed (Standard, Capture, Check, or Castle)
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

        // If moving player was black, increment the move counter
        if(!currentPlayer.isWhite()) {
            state.setFullMoves(state.getFullMoves() + 1);
        }

        // Sets en passant square if last move was pawn move that spanned 2 rows
        if (isPawnMove && BoardUtils.deltaRow(tileToMoveFrom, tileToMoveTo) == 2) {
            // En passant position is multiplied by -1 because dragged piece is opposite color of what we desire
            int rowOffset = ((Pawn) draggedPiece).getEnpassantDirection() * -1;
            Position enpassantSquare = BoardUtils.getOffSetPosition(targetPosition,0, rowOffset);
            board.getGameState().setEnpassantSquare(enpassantSquare);
        }
        // Clear en passant square
        else {
            board.getGameState().setEnpassantSquare(null);
        }

        // If moving the rook or king, then castling is no longer allowed
        if(isKingMove || draggedPiece instanceof Rook) {
            // TODO -> If moving rook, determine which side and don't allow castling to that side anymore
            updateCastling(state, opposingPlayer);
        }

        /*
         * Update the actual board state / pieces
         */

        // If we moved the king
        if(isKingMove) {

            // Update the king position
            board.setKingPosition((King) draggedPiece, targetPosition);

            // If king moved > 1 square, move the corresponding rook as well
            if(BoardUtils.deltaCol(tileToMoveFrom, tileToMoveTo) > 1) {
                typeOfMove = MoveType.CASTLE;

                // Determine whether it was a king or queen side castle
                boolean queenSideCastle = targetPosition.getColumn() < moveFromPosition.getColumn();

                // Get the current rook position
                int columnOffSet = queenSideCastle ? -2 : 1;
                Position rookPos = BoardUtils.getOffSetPosition(targetPosition, columnOffSet, 0);
                Tile rookTile = board.getTileMap().get(rookPos);

                // Get the rook's new position
                columnOffSet = queenSideCastle ? 1 : -1;
                Position newRookPos = BoardUtils.getOffSetPosition(targetPosition, columnOffSet, 0);
                Tile newRookTile = board.getTileMap().get(newRookPos);

                // Move the rook
                newRookTile.setPiece(rookTile.getPiece());
                rookTile.setPiece(null);
            }
        }

        // Add the captured piece to list captured and play capture sound
        if(tileToMoveTo.isOccupied()) {
            typeOfMove = MoveType.CAPTURE;
            currentPlayer.capturePiece(opposingPlayer, capturedPiece);
        }
        // If this was an en passant capture, then remove the captured pawn
        else if(isPawnMove && isEnpassantCapture) {
            typeOfMove = MoveType.CAPTURE;

            // Determine the location of the pawn to be captured
            int rowOffset = ((Pawn) draggedPiece).getEnpassantDirection() * -1;
            Position pawnCapturedPosition = BoardUtils.getOffSetPosition(targetPosition,0, rowOffset);
            Tile capturedPawnTile = board.getTileMap().get(pawnCapturedPosition);

            // Capture the pawn
            capturedPiece = capturedPawnTile.getPiece();
            currentPlayer.capturePiece(opposingPlayer, capturedPiece);
            capturedPawnTile.setPiece(null);
        }

        // Add piece to dragged to tile and remove from originating tile
        tileToMoveTo.setPiece(draggedPiece);
        tileToMoveFrom.setPiece(null);

        // Add our move to the move history
        board.getMoveHistory().addMove(new Move(draggedPiece, tileToMoveFrom, capturedPiece, tileToMoveTo));

        // Populate moves for current game state
        board.getPlayers().values().forEach(player -> {

            // Clear out valid moves for the piece after it moves
            for (Piece piece : player.getPieces()) {
                piece.clearValidMoves();
            }

            player.populateMoves(board);
        });

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
        // Get all moves for opposing player
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
