package com.chess.engine.utils;

import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

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
     * @param currentPosition the current position of the knight
     * @return the positions that are valid to be moved to
     */
    public static List<Position> addPositionsForKnight(Piece piece, Position currentPosition) {
        List<Position> positions = new ArrayList<>();

        // Over 1 up and down 2
        positions.addAll(addPositionsForOffset(piece, currentPosition, 1, -2));
        positions.addAll(addPositionsForOffset(piece, currentPosition, 1, 2));
        positions.addAll(addPositionsForOffset(piece, currentPosition, -1, 2));
        positions.addAll(addPositionsForOffset(piece, currentPosition, -1, -2));

        // Over 2 up and down 1
        positions.addAll(addPositionsForOffset(piece, currentPosition, 2, -1));
        positions.addAll(addPositionsForOffset(piece, currentPosition, 2, 1));
        positions.addAll(addPositionsForOffset(piece, currentPosition, -2, 1));
        positions.addAll(addPositionsForOffset(piece, currentPosition, -2, -1));

        return positions;
    }

    /**
     * Movement for the pawn
     * @param piece the pawn piece
     * @param currentPosition the current position of the pawn
     * @param dir the direction
     * @return the positions that are valid to be moved to
     */
    public static List<Position> addPositionsForPawn(Piece piece, Position currentPosition, Direction dir) {
        List<Position> validPositions = new ArrayList<>();

        // Only allow this to be called for pawns
        if(!(piece instanceof Pawn)) return validPositions;

        int yOffSet = 0;
        if(dir == Direction.UP) {
            yOffSet = 1;
        } else if(dir == Direction.DOWN) {
            yOffSet = -1;
        }

        // First check if the pawn can move in the forward direction
        validPositions.addAll(addPositionsForOffset(piece, currentPosition, 0, yOffSet));

        // Next check if the pawn can move in the diagonal direction
        validPositions.addAll(MoveUtils.addPositionsForDirection(piece, currentPosition, dir, true));

        return validPositions;
    }

    /**
     * Movement for Bishop, Rook, Queen
     * @param piece the piece
     * @param currentPosition the current position of the piece
     * @param dir the direction of movement (up, down, left, right)
     * @param isDiagonal whether diagonal movement
     * @return the positions that are valid to be moved to
     */
    public static List<Position> addPositionsForDirection(Piece piece, Position currentPosition,
                                                         Direction dir, boolean isDiagonal) {
        List<Position> positions = new ArrayList<>();
        if(isDiagonal) {
            int yOffSet = dir == Direction.UP ? 1 : -1;
            positions.addAll(addPositionsForDiagonal(piece, currentPosition, 1, yOffSet));
            positions.addAll(addPositionsForDiagonal(piece, currentPosition, -1, yOffSet));
        } else if(dir == Direction.UP) {
            positions.addAll(addPositionsForVertical(piece, currentPosition, -1));
        } else if(dir == Direction.DOWN) {
            positions.addAll(addPositionsForVertical(piece, currentPosition, 1));
        } else if(dir == Direction.LEFT) {
            positions.addAll(addPositionsForHorizontal(piece, currentPosition, -1));
        } else if(dir == Direction.RIGHT) {
            positions.addAll(addPositionsForHorizontal(piece, currentPosition, 1));
        }

        return positions;
    }

    /**
     * Add positions for a diagonal in given x and y offset direction
     * @param piece the piece to determine positions for
     * @param currentPosition the current position of the piece
     * @param xOffSet the x-offset
     * @param yOffSet the y-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Position> addPositionsForDiagonal(Piece piece, Position currentPosition,
                                                         int xOffSet, int yOffSet) {
        return addPositionsForOffset(piece, currentPosition, xOffSet, yOffSet);
    }

    /**
     * Add positions for a vertical in given y offset direction
     * @param piece the piece
     * @param currentPosition the current position of the piece
     * @param yOffSet the y-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Position> addPositionsForVertical(Piece piece, Position currentPosition, int yOffSet) {
        return addPositionsForOffset(piece, currentPosition, 0, yOffSet);
    }

    /**
     * Add positions for a horizontal in given x offset direction
     * @param piece the piece
     * @param currentPosition the current position of the piece
     * @param xOffSet the x-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Position> addPositionsForHorizontal(Piece piece, Position currentPosition, int xOffSet) {
        return addPositionsForOffset(piece, currentPosition, xOffSet, 0);
    }

    /**
     * Add positions for a given offset
     * @param piece the piece
     * @param currentPosition the current position of the piece
     * @param xOffSet the x-offset
     * @param yOffSet the y-offset
     * @return the positions that are valid to be moved to
     */
    private static List<Position> addPositionsForOffset(Piece piece, Position currentPosition, int xOffSet, int yOffSet) {
        List<Position> positionsSet = new ArrayList<>();
        Position offSetPos = currentPosition.getOffSetPosition(xOffSet, yOffSet);
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
                if ((piece instanceof Pawn && xOffSet == 0)
                        || (piece.getOwner().isSameSide(offSetTile.getPiece().getOwner()))) {
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
            positionsSet.add(offSetPos);

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
        || draggedToTile == null || draggedToTile.isSameTile(originatingTile)) {
            return false;
        }

        Piece draggedPiece = originatingTile.getPiece();

        // Check if the originating piece being moved came from player whose turn it is
        if(!GameState.getInstance().getPlayerTurn().isSameSide(draggedPiece.getOwner())) {
            return false;
        }

        // If the destination tile is occupied by a piece of player who is moving
        if(draggedToTile.isOccupied()
        && draggedToTile.getPiece().getOwner().isSameSide(draggedPiece.getOwner())) {
            return false;
        }

        // Check whether the given piece on the originating tile has the dragged to tile as a valid tile
        return draggedPiece.getMoves(originatingTile.getPosition()).contains(draggedToTile.getPosition());
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

        // If the moved piece was a pawn to captured EP square
        if(isPawnMove && isEnpassantCapture) {
            // Get the location of the pawn and remove it from the tile
            Position enpassantLoc = state.getEPSquare().getOffSetPosition(-1 * ((Pawn)draggedPiece).getEnpassantDirection(), 0);
            System.out.println("enpassant loc: " + enpassantLoc.toString());
            capturePiece(board, board.getTileMap().get(enpassantLoc.toString()), draggedPiece);
        }
        // Perform capture of piece
        else if(isRegularCapture) {
            capturePiece(board, tileToUpdate, draggedPiece);
        }

        tileToUpdate.setPiece(draggedPiece);

        // Add captured piece to content panel
        // TODO - boardUI.getCapturedPanel().addCaptured(capturedPiece);
    }

    private static void capturePiece(Board board, Tile tileToCaptureOn, Piece newPiece) {
        // Remove piece that is being replaced by new piece
        if(tileToCaptureOn.getComponents().length > 0) {
            tileToCaptureOn.remove(0);
        }

        // Add our new piece
        Position capturePosition = tileToCaptureOn.getPosition();
        tileToCaptureOn.setPiece(newPiece);
        // TODO -> remove this print out
        System.out.println(board.getTileMap().get(capturePosition.toString()).getPiece());
    }
}
