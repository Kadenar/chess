package com.chess.engine.moves;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovePositions {

    /**
     * Method specifically used for Knights for movement
     * @param piece the knight to be moved
     * @param currentTile the current position of the knight
     * @return the positions that are valid to be moved to
     */
    public static List<Move> addPositionsForKnight(Board board, Piece piece, Tile currentTile) {
        List<Move> positions = new ArrayList<>();

        // Over 1 up and down 2
        positions.addAll(addPositionsForOffset(board, piece, currentTile, 1, -2));
        positions.addAll(addPositionsForOffset(board, piece, currentTile, 1, 2));
        positions.addAll(addPositionsForOffset(board, piece, currentTile, -1, 2));
        positions.addAll(addPositionsForOffset(board, piece, currentTile, -1, -2));

        // Over 2 up and down 1
        positions.addAll(addPositionsForOffset(board, piece, currentTile, 2, -1));
        positions.addAll(addPositionsForOffset(board, piece, currentTile, 2, 1));
        positions.addAll(addPositionsForOffset(board, piece, currentTile, -2, 1));
        positions.addAll(addPositionsForOffset(board, piece, currentTile, -2, -1));

        return positions;
    }

    /**
     * King side castle location (assumes that you can king side castle)
     * @param currentTile the current tile of the king
     * @return king side castle location
     */
    public static List<Move> addKingSideCastlePosition(Board board, Tile currentTile) {
        List<Move> validPositions = new ArrayList<>();
        int kingColumn = currentTile.getPosition().getColumn();
        for(int i = kingColumn+1; i < 8; i++) {
            Position offSetPosition = new Position(currentTile.getPosition().getRow(), i);
            Map<String, Tile> tiles = board.getTileMap();

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
    public static List<Move> addQueenSideCastlePosition(Board board, Tile currentTile) {
        List<Move> validPositions = new ArrayList<>();
        int kingColumn = currentTile.getPosition().getColumn();
        for(int i = kingColumn-1; i >= 0; i--) {
            Position offSetPosition = new Position(currentTile.getPosition().getRow(), i);
            Map<String, Tile> tiles = board.getTileMap();

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
    public static List<Move> addPositionsForPawn(Board board, Piece piece, Tile currentTile, Direction dir) {
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
        validPositions.addAll(addPositionsForOffset(board, piece, currentTile, 0, rowOffSet));

        // Next check if the pawn can move in the diagonal direction
        validPositions.addAll(addPositionsForDirection(board, piece, currentTile, dir, true));

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
    public static List<Move> addPositionsForDirection(Board board, Piece piece, Tile currentTile,
                                                      Direction dir, boolean isDiagonal) {
        List<Move> positions = new ArrayList<>();
        if(isDiagonal) {
            int rowOffset = dir == Direction.UP ? 1 : -1;
            positions.addAll(addPositionsForDiagonal(board, piece, currentTile, 1, rowOffset));
            positions.addAll(addPositionsForDiagonal(board, piece, currentTile, -1, rowOffset));
        } else if(dir == Direction.UP) {
            positions.addAll(addPositionsForVertical(board, piece, currentTile, 1));
        } else if(dir == Direction.DOWN) {
            positions.addAll(addPositionsForVertical(board, piece, currentTile, -1));
        } else if(dir == Direction.LEFT) {
            positions.addAll(addPositionsForHorizontal(board, piece, currentTile, -1));
        } else if(dir == Direction.RIGHT) {
            positions.addAll(addPositionsForHorizontal(board, piece, currentTile, 1));
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
    private static List<Move> addPositionsForDiagonal(Board board, Piece piece, Tile currentTile,
                                                      int colOffset, int rowOffset) {
        return addPositionsForOffset(board, piece, currentTile, colOffset, rowOffset);
    }

    /**
     * Add positions for a vertical in given y offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param rowOffset the row offset (up and down)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForVertical(Board board, Piece piece, Tile currentTile, int rowOffset) {
        return addPositionsForOffset(board, piece, currentTile, 0, rowOffset);
    }

    /**
     * Add positions for a horizontal in given x offset direction
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForHorizontal(Board board, Piece piece, Tile currentTile, int colOffset) {
        return addPositionsForOffset(board, piece, currentTile, colOffset, 0);
    }

    /**
     * Add positions for a given offset
     * @param piece the piece
     * @param currentTile the current position of the piece
     * @param colOffset the column offset (left and right movement)
     * @param rowOffSet the row offset (up and down movement)
     * @return the positions that are valid to be moved to
     */
    private static List<Move> addPositionsForOffset(Board board, Piece piece, Tile currentTile,
                                                    int colOffset, int rowOffSet) {
        List<Move> positionsSet = new ArrayList<>();
        Position currentPosition = currentTile.getPosition();
        Position offSetPos = currentPosition.getOffSetPosition(colOffset, rowOffSet);
        Map<String, Tile> tiles = board.getTileMap();
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
                if(isPawn && colOffset != 0 && rowOffSet != 0 && !offSetPos.equals(board.getGameState().getEPSquare())) {
                    break;
                }

            }

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
}
