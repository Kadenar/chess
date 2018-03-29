package com.chess.engine.utils;

import com.chess.engine.board.Board;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Tile.EmptyTile;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.ui.PieceUI;
import com.chess.ui.TileUI;

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

    /*
    * Method specifically used for Knights for movement
    */
    public static List<Position> addPositionsForKnight(Piece piece) {
        List<Position> positions = new ArrayList<>();

        // Over 1 up and down 2
        positions.addAll(addPositionsForOffset(piece, 1, -2));
        positions.addAll(addPositionsForOffset(piece, 1, 2));
        positions.addAll(addPositionsForOffset(piece, -1, 2));
        positions.addAll(addPositionsForOffset(piece, -1, -2));

        // Over 2 up and down 1
        positions.addAll(addPositionsForOffset(piece, 2, -1));
        positions.addAll(addPositionsForOffset(piece, 2, 1));
        positions.addAll(addPositionsForOffset(piece, -2, 1));
        positions.addAll(addPositionsForOffset(piece, -2, -1));

        return positions;
    }

    /*
     * Movement for the pawn
     */
    public static List<Position> addPositionsForPawn(Piece piece, Direction d) {
        List<Position> validPositions = new ArrayList<>();

        // Only allow this to be called for pawns
        if(!(piece instanceof Pawn)) return validPositions;

        int yOffSet = 0;
        if(d == Direction.UP) {
            yOffSet = 1;
        } else if(d == Direction.DOWN) {
            yOffSet = -1;
        }

        // First check if the pawn can move in the forward direction
        validPositions.addAll(addPositionsForOffset(piece, 0, yOffSet));

        // Next check if the pawn can move in the diagonal direction
        validPositions.addAll(MoveUtils.addPositionsForDirection(piece, d, true));

        return validPositions;
    }


    /*
     * Movement for Bishop, Rook, Queen
     */
    public static List<Position> addPositionsForDirection(Piece piece, Direction d, boolean diag) {
        List<Position> positions = new ArrayList<>();
        if(diag) {
            if(d == Direction.UP) {
                positions.addAll(addPositionsForDiagonal(piece, 1, 1));
                positions.addAll(addPositionsForDiagonal(piece, -1, 1));
            } else if(d == Direction.DOWN) {
                positions.addAll(addPositionsForDiagonal(piece, 1, -1));
                positions.addAll(addPositionsForDiagonal(piece, -1, -1));
            }
        } else if(d == Direction.UP) {
            positions.addAll(addPositionsForVertical(piece, -1));
        } else if(d == Direction.DOWN) {
            positions.addAll(addPositionsForVertical(piece, 1));
        } else if(d == Direction.LEFT) {
            positions.addAll(addPositionsForHorizontal(piece, -1));
        } else if(d == Direction.RIGHT) {
            positions.addAll(addPositionsForHorizontal(piece, 1));
        }

        return positions;
    }

    private static List<Position> addPositionsForOffset(Piece piece, int xOffSet, int yOffSet) {
        List<Position> positionsForOffset = new ArrayList<>();
        Position currentPos = piece.getPosition();
        Position offSetPos = currentPos.getOffSetPosition(xOffSet, yOffSet);
        Map<String, Tile> tiles = BoardUtils.getInstance().getBoard().getTileMap();
        int tilesCounted = 0;

        // While we have a valid coordinate and haven't checked the max spaces allowed by this piece
        while(offSetPos.isValidCoord() && tilesCounted < piece.getMaxSpacesMoved()) {
            Tile offSetTile = tiles.get(offSetPos.toString());
            boolean offSetIsOccupied = offSetTile.isOccupied();

            // If the offset tile is occupied by another piece
            if(offSetIsOccupied) {

                // If the piece being moved is a pawn and the direction is forward and not diagonal
                if (piece instanceof Pawn && xOffSet == 0) {
                    break;
                }
                // Break if the owner of the current tile is the same as owner of the piece being moved
                else if(piece.getOwner().isSameSide(offSetTile.getPiece().getOwner())) {
                    break;
                }

            } else { // If the offset tile is not occupied by another piece

                // If not occupied and we are a pawn, don't allow diagonal movement
                if(piece instanceof Pawn && (xOffSet != 0 && yOffSet != 0)) {
                    break;
                }

            }

            // Add our position if it was not occupied or was an opposing piece
            positionsForOffset.add(offSetPos);

            // Break if the tile was occupied as we can't go past an occupied tile
            if(offSetIsOccupied) {
                break;
            }

            // Get our next position based on our offset
            offSetPos = offSetPos.getOffSetPosition(xOffSet, yOffSet);
            tilesCounted++;
        }

        return positionsForOffset;
    }

    /*
    * Add positions for a diagonal in given x and y offset direction
     */
    private static List<Position> addPositionsForDiagonal(Piece piece, int xOffSet, int yOffSet) {
        return addPositionsForOffset(piece, xOffSet, yOffSet);
    }

    /*
    * Add positions for a vertical in given y offset direction
    */
    private static List<Position> addPositionsForVertical(Piece piece, int yOffSet) {
        return addPositionsForOffset(piece, 0, yOffSet);
    }

    /*
    * Add positions for a horizontal in given x offset direction
    */
    private static List<Position> addPositionsForHorizontal(Piece piece, int xOffSet) {
        return addPositionsForOffset(piece, xOffSet, 0);
    }

    /**
     * Updates the piece displayed on a given tile
     * @param tileToUpdate the tile to be updated
     * @param uiPiece the piece to add (null if we want to remove any previous piece)
     */
    public static void executeMove(Board board, TileUI tileToUpdate, PieceUI uiPiece) {

        // If no tile provided, break out
        if(tileToUpdate == null) {
            return;
        }

        // Play our sound move
        //TODO get more sounds / play different sounds depending on the kind of move made
        // i.e - check, checkmate, castles, captures piece, regular move
        SoundUtils.playMoveSound();

        // Remove the old piece from the UI
        if(tileToUpdate.getComponents().length > 0) {
            tileToUpdate.remove(0);
        }

        // Need to remove the piece from the backend as well and replace with an empty tile
        Position updatePos = tileToUpdate.getTile().getPosition();
        String updatePosString = updatePos.toString();
        Map<String, Tile> tiles = board.getTileMap();

        // If we replacing tile with nothing...
        if(uiPiece == null) {

            // Create a new empty tile and replace entry in map
            EmptyTile newEmpty = new EmptyTile(updatePos);
            tiles.put(updatePosString, newEmpty);

            // Update the tile ui to reference new empty tile
            tileToUpdate.setTile(newEmpty);

            // Update the tile ui to have no piece
            tileToUpdate.setPieceUI(null);

        } else { // Otherwise...

            // Add the new piece as a component
            tileToUpdate.add(uiPiece);

            // Update PieceUI pointer for the tile
            tileToUpdate.setPieceUI(uiPiece);

            Piece piece = uiPiece.getPiece();
            piece.setPosition(updatePos);
            piece.setHasMoved(true);


            // If the tile was not previously occupied...
            if(!tiles.get(updatePosString).isOccupied()) {

                // Create a new occupied tile
                Tile.OccupiedTile occupied = new Tile.OccupiedTile(updatePos, piece);

                // add the tile to our map
                tiles.put(updatePosString, occupied);

                // Update our
                tileToUpdate.setTile(occupied);

            } else { // Otherwise...

                // Update the piece on the tile in our board
                tiles.get(updatePosString).setPiece(piece);

                // Update the actual piece object itself
                tileToUpdate.getTile().setPiece(piece);
            }
        }
    }
}
