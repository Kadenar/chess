package com.chess.engine.utils;

import com.chess.engine.Move;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
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

    // Add our possible castling positions
    public static List<Move> addCastlingPositions(Tile currentTile) {
        List<Move> validPositions = new ArrayList<>();
        // Castling king side
        int kingColumn = currentTile.getPosition().getColumn();
        for(int i = kingColumn+1; i < 8; i++) {
            Position offSetPosition = new Position(currentTile.getPosition().getRow(), i);
            Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();

            // If the tile is occupied...
            Tile offsetTile = tiles.get(offSetPosition.toString());
            if(offsetTile.isOccupied()) {

                // If the piece is a rook and has not moved
                Piece occupyingPiece = offsetTile.getPiece();
                if(occupyingPiece instanceof Rook && !occupyingPiece.hasMoved()) {
                    Tile kingCastleLoc = tiles.get(new Position(currentTile.getPosition().getRow(), i-1).toString());
                    validPositions.add(new Move(currentTile, kingCastleLoc));
                } else {
                    break;
                }
            }
        }
        // Castling queen side
        for(int i = kingColumn-1; i >= 0; i--) {
            Position offSetPosition = new Position(currentTile.getPosition().getRow(), i);
            Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();

            // If the tile is occupied...
            Tile offsetTile = tiles.get(offSetPosition.toString());
            if(offsetTile.isOccupied()) {

                // If the piece is a rook and has not moved
                Piece occupyingPiece = offsetTile.getPiece();
                if(occupyingPiece instanceof Rook && !occupyingPiece.hasMoved()) {
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

        int yOffSet = 0;
        if(dir == Direction.UP) {
            yOffSet = 1;
        } else if(dir == Direction.DOWN) {
            yOffSet = -1;
        }

        // First check if the pawn can move in the forward direction
        validPositions.addAll(addPositionsForOffset(piece, currentTile, 0, yOffSet));

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
            int yOffSet = dir == Direction.UP ? 1 : -1;
            positions.addAll(addPositionsForDiagonal(piece, currentTile, 1, yOffSet));
            positions.addAll(addPositionsForDiagonal(piece, currentTile, -1, yOffSet));
        } else if(dir == Direction.UP) {
            positions.addAll(addPositionsForVertical(piece, currentTile, -1));
        } else if(dir == Direction.DOWN) {
            positions.addAll(addPositionsForVertical(piece, currentTile, 1));
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
     * @param xOffSet the x-offset
     * @param yOffSet the y-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForDiagonal(Piece piece, Tile currentTile,
                                                      int xOffSet, int yOffSet) {
        return addPositionsForOffset(piece, currentTile, xOffSet, yOffSet);
    }

    /**
     * Add positions for a vertical in given y offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param yOffSet the y-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForVertical(Piece piece, Tile currentTile, int yOffSet) {
        return addPositionsForOffset(piece, currentTile, 0, yOffSet);
    }

    /**
     * Add positions for a horizontal in given x offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param xOffSet the x-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForHorizontal(Piece piece, Tile currentTile, int xOffSet) {
        return addPositionsForOffset(piece, currentTile, xOffSet, 0);
    }

    /**
     * Add positions for a given offset
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param xOffSet the x-offset
     * @param yOffSet the y-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForOffset(Piece piece, Tile currentTile,
                                                    int xOffSet, int yOffSet) {
        List<Move> positionsSet = new ArrayList<>();
        Position offSetPos = currentTile.getPosition().getOffSetPosition(xOffSet, yOffSet);
        Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();
        int tilesCounted = 0;

        // While we have a valid coordinate and haven't checked the max spaces allowed by this piece
        while(offSetPos.isValidCoord() && tilesCounted < piece.getMaxSpacesMoved()) {
            Tile offSetTile = tiles.get(offSetPos.toString());
            boolean offSetIsOccupied = offSetTile.isOccupied();

            // If the offset tile is occupied by another piece
            if(offSetIsOccupied) {

                // Don't allow a pawn to be moved to another pawn's location unless it is a diagonal move
                // Don't allow a pawn to move diagonally if the pawn on that tile is the same owner
                if ((piece instanceof Pawn && xOffSet == 0) || piece.equals(offSetTile.getPiece())) {
                    break;
                }

            }
            // If the offset tile is not occupied by another piece
            else {

                // If not occupied and we are a pawn, don't allow diagonal movement unless en-passant
                if(piece instanceof Pawn && (xOffSet != 0 && yOffSet != 0)
                        && !offSetTile.getPosition().equals(GameState.getInstance().getEPSquare())) {
                    break;
                }

            }

            // Add our position if it was not occupied or was an opposing piece
            positionsSet.add(new Move(currentTile, offSetTile));

            // Break if the tile was occupied as we can't go past an occupied tile
            if(offSetIsOccupied) {
                break;
            }

            // Get our next position based on our offset
            offSetPos = offSetPos.getOffSetPosition(xOffSet, yOffSet);
            tilesCounted++;
        }

        return positionsSet;
    }

    // Determine whether we can move a given piece from 1 tile to another
    private static boolean canMovePiece(Tile originatingTile, Tile draggedToTile) {
        // If originating tile or piece are null..
        // Or if the dragged to tile is null or the same as original, just exit
        if(originatingTile == null || originatingTile.getPiece() == null
        || draggedToTile == null || draggedToTile.equals(originatingTile)) {
            return false;
        }

        Piece draggedPiece = originatingTile.getPiece();

        // Check if the originating piece being moved came from player whose turn it is
        if(!draggedPiece.isSameSide(GameState.getInstance().getPlayerTurn())) {
            return false;
        }

        // If the destination tile is occupied by a piece of player who is moving
        if(draggedToTile.isOccupied()
        && draggedToTile.getPiece().isSameSide(draggedPiece)) {
            return false;
        }

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        return draggedPiece.getMoves(originatingTile).contains(new Move(originatingTile, draggedToTile));
    }

    /**
     * Updates the piece displayed on a given tile
     * This assumes that the move is valid
     * @param originatingTile the tile the move was executed from
     * @param targetTile the tile the move will be executed to
     */
    public static boolean executeMove(Board board, Tile originatingTile, Tile targetTile) {

        // If we can't move the piece just exit
        if(!canMovePiece(originatingTile, targetTile)) {
            return false;
        }

        // Play our sound move
        // TODO get more sounds / play different sounds depending on the kind of move made
        // TODO i.e - check, checkmate, castles, captures piece, regular move
        SoundUtils.playMoveSound();

        // Remove dragged piece from previous tile
        if(originatingTile.getComponents().length > 0) {
            originatingTile.remove(0);
        }

        // Get our game state
        GameState state = GameState.getInstance();

        // Add the captured piece to list captured
        if(targetTile.isOccupied()) {
            state.addCapturedPiece(targetTile.getPiece());
        }

        // Perform the piece move
        Piece draggedPiece = originatingTile.getPiece();
        performPieceMove(state, board, targetTile, draggedPiece);
        originatingTile.setPiece(null);

        // Sets en-passant square if last move was pawn move that spanned 2 rows
        if(draggedPiece instanceof Pawn && BoardUtils.deltaRow(originatingTile, targetTile) == 2) {
            Position enpassantPosition = originatingTile.getPosition()
                    .getOffSetPosition(0, ((Pawn) draggedPiece).getEnpassantDirection());
            state.setEnpassantSquare(enpassantPosition);
        } else {
            state.setEnpassantSquare(null);
        }

        // Update player turn and full moves
        state.setPlayerTurn(state.getPlayerTurn().opposite());
        state.setFullMoves(state.getFullMoves() + 1);
        // TODO state.setCastlingAbility("");

        // Print out the fen after each move
        System.out.println(FenUtils.getFen(board));
        return true;
    }

    /**
     * Perform a piece capture
     * @param state the game state to update with captured piece
     * @param board the board we are updating
     * @param tileToUpdate the tile we dragged the piece to
     * @param draggedPiece the dragged piece
     */
    private static void performPieceMove(GameState state, Board board, Tile tileToUpdate, Piece draggedPiece) {

        // Mark out piece as having been moved
        draggedPiece.setHasMoved(true);

        // Set our half move
        boolean isPawnMove = draggedPiece instanceof Pawn;
        Position targetPosition = tileToUpdate.getPosition();
        boolean isEnpassantCapture = targetPosition.equals(state.getEPSquare());
        boolean isRegularCapture = tileToUpdate.isOccupied();

        // This counter is reset after captures or pawn moves, and incremented otherwise
        state.setHalfMoves(isPawnMove || isRegularCapture ? 0 : state.getHalfMoves() + 1);

        // Attempt to capture piece, or just do standard move
        capturePiece(board, tileToUpdate, draggedPiece, isPawnMove && isEnpassantCapture);

        // Add captured piece to content panel
        // TODO - boardUI.getCapturedPanel().addCaptured(capturedPiece);
    }

    // Capture piece on a tile and place new piece there while handling enpassant
    private static void capturePiece(Board board, Tile tileToCaptureOn, Piece newPiece, boolean enpassantCapture) {

        // Add our new piece
        if(!enpassantCapture) {
            // Remove piece that is being replaced by new piece
            if(tileToCaptureOn.getComponents().length > 0) {
                tileToCaptureOn.remove(0);
            }
        } else {
            // Enpassant position is multipled by -1 because dragged piece is opposite color of what we desire
            Position enpassantPosition = tileToCaptureOn.getPosition()
                    .getOffSetPosition(0, -1 * ((Pawn) newPiece).getEnpassantDirection());
            Tile enpassantTile = board.getTileMap().get(enpassantPosition.toString());

            // Remove piece that is being replaced by new piece
            if(enpassantTile.getComponents().length > 0) {
                enpassantTile.remove(0);
            }
            enpassantTile.setPiece(null);
        }

        // Add piece to dragged to tile always
        tileToCaptureOn.setPiece(newPiece);

    }
}
