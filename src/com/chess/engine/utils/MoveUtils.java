package com.chess.engine.utils;

import com.chess.engine.Move;
import com.chess.engine.board.*;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveUtils {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
    }

    /**
     * Method specifically used for Knights for movement
     * @param piece the knight to be moved
     * @param currentTile the current position of the knight
     * @return the positions that are valid to be moved to
     */
    public static List<Move> addPositionsForKnight(Piece piece, Tile currentTile) {
        List<Move> positions = new ArrayList<>();

        // Over 1 up and down 2
        positions.addAll(addPositionsForOffset(piece, currentTile, 1, -2));
        positions.addAll(addPositionsForOffset(piece, currentTile, 1, 2));
        positions.addAll(addPositionsForOffset(piece, currentTile, -1, 2));
        positions.addAll(addPositionsForOffset(piece, currentTile, -1, -2));

        // Over 2 up and down 1
        positions.addAll(addPositionsForOffset(piece, currentTile, 2, -1));
        positions.addAll(addPositionsForOffset(piece, currentTile, 2, 1));
        positions.addAll(addPositionsForOffset(piece, currentTile, -2, 1));
        positions.addAll(addPositionsForOffset(piece, currentTile, -2, -1));

        return positions;
    }

    /**
     * King side castle location (assumes that you can king side castle)
     * @param currentTile the current tile of the king
     * @return king side castle location
     */
    public static List<Move> addKingSideCastlePosition(Tile currentTile) {
        List<Move> validPositions = new ArrayList<>();
        int kingColumn = currentTile.getPosition().getColumn();
        for(int i = kingColumn+1; i < 8; i++) {
            Position offSetPosition = new Position(currentTile.getPosition().getRow(), i);
            Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();

            // If the tile is occupied...
            Tile offsetTile = tiles.get(offSetPosition.toString());
            if(offsetTile.isOccupied()) {

                // If the piece is a rook and has not moved
                Piece occupyingPiece = offsetTile.getPiece();
                if(occupyingPiece instanceof Rook) {
                    Tile kingCastleLoc = tiles.get(new Position(currentTile.getPosition().getRow(), i-1).toString());
                    validPositions.add(new Move(currentTile, kingCastleLoc));
                } else {
                    break;
                }
            }
        }
        return validPositions;
    }

    /**
     * Add queen side castle position for the king (assumes that you can queen side castle)
     * @param currentTile the current tile of the king
     * @return queen side castle location
     */
    public static List<Move> addQueenSideCastlePosition(Tile currentTile) {
        List<Move> validPositions = new ArrayList<>();
        int kingColumn = currentTile.getPosition().getColumn();
        for(int i = kingColumn-1; i >= 0; i--) {
            Position offSetPosition = new Position(currentTile.getPosition().getRow(), i);
            Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();

            // If the tile is occupied...
            Tile offsetTile = tiles.get(offSetPosition.toString());
            if(offsetTile.isOccupied()) {

                // If the piece is a rook and has not moved
                Piece occupyingPiece = offsetTile.getPiece();
                if(occupyingPiece instanceof Rook) {
                    Tile kingCastleLoc = tiles.get(new Position(currentTile.getPosition().getRow(), i+2).toString());
                    validPositions.add(new Move(currentTile, kingCastleLoc));
                } else {
                    break;
                }
            }
        }
        return validPositions;
    }

        /**
         * Movement for the pawn
         * @param piece the pawn piece
         * @param currentTile the current position of the pawn
         * @param dir the direction
         * @return the positions that are valid to be moved to
         */
    public static List<Move> addPositionsForPawn(Piece piece, Tile currentTile, Direction dir) {
        List<Move> validPositions = new ArrayList<>();

        // Only allow this to be called for pawns
        if(!(piece instanceof Pawn)) return validPositions;

        int rowOffSet = 0;
        if(dir == Direction.UP) {
            rowOffSet = 1;
        } else if(dir == Direction.DOWN) {
            rowOffSet = -1;
        }

        // First check if the pawn can move in the forward direction
        validPositions.addAll(addPositionsForOffset(piece, currentTile, 0, rowOffSet));

        // Next check if the pawn can move in the diagonal direction
        validPositions.addAll(MoveUtils.addPositionsForDirection(piece, currentTile, dir, true));

        return validPositions;
    }

    /**
     * Movement for Bishop, Rook, Queen
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param dir the direction of movement (up, down, left, right)
     * @param isDiagonal whether diagonal movement
     * @return the positions that are valid to be moved to
     */
    public static List<Move> addPositionsForDirection(Piece piece, Tile currentTile,
                                                      Direction dir, boolean isDiagonal) {
        List<Move> positions = new ArrayList<>();
        if(isDiagonal) {
            int rowOffset = dir == Direction.UP ? 1 : -1;
            positions.addAll(addPositionsForDiagonal(piece, currentTile, 1, rowOffset));
            positions.addAll(addPositionsForDiagonal(piece, currentTile, -1, rowOffset));
        } else if(dir == Direction.UP) {
            positions.addAll(addPositionsForVertical(piece, currentTile, 1));
        } else if(dir == Direction.DOWN) {
            positions.addAll(addPositionsForVertical(piece, currentTile, -1));
        } else if(dir == Direction.LEFT) {
            positions.addAll(addPositionsForHorizontal(piece, currentTile, -1));
        } else if(dir == Direction.RIGHT) {
            positions.addAll(addPositionsForHorizontal(piece, currentTile, 1));
        }

        return positions;
    }

    /**
     * Add positions for a diagonal in given x and y offset direction
     * @param piece the piece to determine positions for
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right)
     * @param rowOffset the row offset (up and down)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForDiagonal(Piece piece, Tile currentTile,
                                                      int colOffset, int rowOffset) {
        return addPositionsForOffset(piece, currentTile, colOffset, rowOffset);
    }

    /**
     * Add positions for a vertical in given y offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param rowOffset the row offset (up and down)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForVertical(Piece piece, Tile currentTile, int rowOffset) {
        return addPositionsForOffset(piece, currentTile, 0, rowOffset);
    }

    /**
     * Add positions for a horizontal in given x offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForHorizontal(Piece piece, Tile currentTile, int colOffset) {
        return addPositionsForOffset(piece, currentTile, colOffset, 0);
    }

    /**
     * Add positions for a given offset
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right movement)
     * @param rowOffSet the row offset (up and down movement)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForOffset(Piece piece, Tile currentTile,
                                                    int colOffset, int rowOffSet) {
        List<Move> positionsSet = new ArrayList<>();
        Position currentPosition = currentTile.getPosition();
        Position offSetPos = currentPosition.getOffSetPosition(colOffset, rowOffSet);
        Board currentBoard = BoardUtils.getInstance().getBoard();
        Map<String, Tile> tiles = currentBoard.getTileMap();
        boolean isPawn = piece instanceof Pawn;
        int maxSpacesMoved = piece.getMaxSpacesMoved();

        // Set max spaces to 1 for pawns not on home row
        if(isPawn && (currentPosition.getRow() != 1 && currentPosition.getRow() != 6)) {
            maxSpacesMoved = 1;
        }

        // While we haven't checked the max spaces allowed by this piece
        for(int tilesCounted = 0; tilesCounted < maxSpacesMoved; tilesCounted++) {

            // Ensure offset position is a valid coordinate
            if(!offSetPos.isValidCoord()) {
                break;
            }

            // Get offset tile and check whether it is occupied
            Tile offSetTile = tiles.get(offSetPos.toString());
            boolean offSetTileIsOccupied = offSetTile.isOccupied();

            // If the offset tile is occupied by another piece
            if(offSetTileIsOccupied) {

                // Don't allow a pawn to be moved to another pawn's location unless it is a diagonal move
                // Don't allow a pawn to move diagonally if the pawn on that tile is the same owner
                if ((isPawn && colOffset == 0) || piece.sameSide(offSetTile.getPiece())) {
                    break;
                }

            }
            // If the offset tile is not occupied by another piece
            else {

                // If we are a pawn, don't allow diagonal movement unless en-passant
                if(isPawn && colOffset != 0 && rowOffSet != 0 && !offSetPos.equals(GameState.getInstance().getEPSquare())) {
                    break;
                }

            }

            // TODO Add check if move would put king in check


            // Add our position if it was not occupied or was an opposing piece and does not cause check
            positionsSet.add(new Move(currentTile, offSetTile));

            // Break if the tile was occupied as we can't go past an occupied tile
            if(offSetTileIsOccupied) {
                break;
            }

            // Get our next position based on our offset
            offSetPos = offSetPos.getOffSetPosition(colOffset, rowOffSet);
        }

        return positionsSet;
    }

    /**
     * Determine whether we can move a piece from an originating tile to dragged to tile
     * @param originatingTile the originating tile that contains the piece we want to move
     * @param draggedToTile the tile we want to move the piece to
     * @return true if we can move the piece, false if not
     */
    private static boolean canMovePiece(Tile originatingTile, Tile draggedToTile) {
        // If originating tile or piece are null..
        // Or if the dragged to tile is null or the same as original, just exit
        if(originatingTile == null || originatingTile.getPiece() == null
        || draggedToTile == null || draggedToTile.equals(originatingTile)) {
            return false;
        }

        Piece draggedPiece = originatingTile.getPiece();

        // Check if the originating piece being moved came from player whose turn it is
        if(!draggedPiece.getOwner().equals(GameState.getInstance().getPlayerTurn())) {
            return false;
        }

        // If the destination tile is occupied by a piece of player who is moving
        if(draggedToTile.isOccupied() && draggedToTile.getPiece().sameSide(draggedPiece)) {
            return false;
        }

        // TODO - Check if move would put the king in check

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        return draggedPiece.getMoves(originatingTile).contains(new Move(originatingTile, draggedToTile));
    }

    /**
     * Updates the piece displayed on a given tile after checking that the piece can be moved to target tile
     * @param originatingTile the tile the move was executed from
     * @param targetTile the tile the move will be executed to
     */
    public static boolean executeMove(Board board, Tile originatingTile, Tile targetTile) {

        // If we can't move the piece just exit
        if(!canMovePiece(originatingTile, targetTile)) {
            SoundUtils.playMoveSound("invalid");
            return false;
        }

        // Perform the piece move
        updateGameState(board, originatingTile, targetTile);

        // Print out the fen after each move and whether in check
        System.out.println("White King loc: " + GameState.getInstance().getWhiteKingPosition());
        System.out.println("Black King loc: " + GameState.getInstance().getBlackKingPosition());
        System.out.println("White in check? : " + isKingInCheck(Player.WHITE));
        System.out.println("Black in check? : " + isKingInCheck(Player.BLACK));

        return true;
    }

    /**
     * Check whether a given player is in check
     * @param playerToCheck the player to check
     * @return true if in check, false if not
     */
    private static boolean isKingInCheck(Player playerToCheck) {
        Position kingPosition;
        if(playerToCheck.isWhite()) {
            kingPosition = GameState.getInstance().getWhiteKingPosition();
        } else {
            kingPosition = GameState.getInstance().getBlackKingPosition();
        }

        // For all tiles on our board
        Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();
        for(Map.Entry<String, Tile> tile : tiles.entrySet()) {
            Tile currentTile = tile.getValue();

            // If the tile is occupied
            if(currentTile.isOccupied()) {

                // And the occupying piece is of the opposing color
                Piece pieceOnTile = currentTile.getPiece();
                if(pieceOnTile.getOwner().equals(playerToCheck.opposite())) {

                    // Get all valid moves for each piece
                    List<Move> validMoves = pieceOnTile.getMoves(currentTile);
                    for (Move move : validMoves) {

                        // If any of the moves destination matches our king position, then we are in check
                        if (move.getDestination().getPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Perform a piece move updating game state and user interface
     * @param board the board we are updating
     * @param tileToMoveFrom the tile to remove dragged piece from
     * @param tileToUpdate the tile we dragged the piece to
     */
    private static void updateGameState(Board board, Tile tileToMoveFrom, Tile tileToUpdate) {

        // Get our current game state
        GameState state = GameState.getInstance();

        Piece draggedPiece = tileToMoveFrom.getPiece();
        Position targetPosition = tileToUpdate.getPosition();
        boolean isPawnMove = draggedPiece instanceof Pawn;
        boolean isKingMove = draggedPiece instanceof King;
        boolean isEnpassantCapture = targetPosition.equals(state.getEPSquare());
        boolean isRegularCapture = tileToUpdate.isOccupied();

        // Update the user interface
        updatePiecesOnBoard(board, tileToMoveFrom, tileToUpdate, isPawnMove && isEnpassantCapture);

        // This counter is reset after captures or pawn moves, and incremented otherwise
        state.setHalfMoves(isPawnMove || isRegularCapture ? 0 : state.getHalfMoves() + 1);

        // If we moved the king, update the king position
        if(isKingMove) {
            state.setKingPosition((King)draggedPiece, tileToUpdate.getPosition());
        }

        // If king moved > 1 square, update castling and play different sound
        if(isKingMove && BoardUtils.deltaCol(tileToMoveFrom, tileToUpdate) > 1) {
            updateCastling(draggedPiece.getOwner().opposite());
            SoundUtils.playMoveSound("castle");
        } else {
            // Add the captured piece to list captured and play capture sound
            if(tileToUpdate.isOccupied()) {
                state.addCapturedPiece(tileToUpdate.getPiece());
                SoundUtils.playMoveSound("capture2");
            } else {
                // If check move, play checking sound
                if(isKingInCheck(draggedPiece.getOwner().opposite())) {
                    SoundUtils.playMoveSound("check1");
                }
                // Otherwise play standard move sound
                else {
                    SoundUtils.playMoveSound("mov2");
                }
            }
        }

        // Sets en-passant square if last move was pawn move that spanned 2 rows
        if(isPawnMove && BoardUtils.deltaRow(tileToMoveFrom, tileToUpdate) == 2) {
            Position enpassantPosition = tileToMoveFrom.getPosition()
                    .getOffSetPosition(0, ((Pawn) draggedPiece).getEnpassantDirection());
            state.setEnpassantSquare(enpassantPosition);
        } else {
            state.setEnpassantSquare(null);
        }

        // Update player turn and full moves
        state.setPlayerTurn(state.getPlayerTurn().opposite());
        state.setFullMoves(state.getFullMoves() + 1);
    }

    /**
     * Update the castling fen value for the current board state
     * @param player the opposing player
     */
    private static void updateCastling(Player player) {
        StringBuilder castlingAbility = new StringBuilder();
        GameState state = GameState.getInstance();
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
}
