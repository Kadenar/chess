package com.chess.engine.pieces;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.moves.Direction;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MoveUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends Piece {

    public King(Player color) {
        super(color, "king.png");
    }

    /**
     * A king can move in all directions 1 square unless castling
     * @param currentTile the current tile the king is located on
     * @return list of valid moves the king can make
     */
    @Override
    public Set<Move> generateMoves(Board board, Tile currentTile) {
        Set<Move> validPositions = new HashSet<>();

        // Vertical movement
        validPositions.addAll(addPositionsForDirection(board, this, currentTile, Direction.UP, false));
        validPositions.addAll(addPositionsForDirection(board, this, currentTile, Direction.DOWN, false));

        // Diagonal movement
        validPositions.addAll(addPositionsForDirection(board, this, currentTile, Direction.UP, true));
        validPositions.addAll(addPositionsForDirection(board, this, currentTile, Direction.DOWN, true));

        // Horizontal movement
        validPositions.addAll(addPositionsForDirection(board, this, currentTile, Direction.RIGHT, false));
        validPositions.addAll(addPositionsForDirection(board, this, currentTile, Direction.LEFT, false));

        // castling king side
        // TODO -> Might be able to remove this when rule
        if (board.getGameState().canCastleKingSide(getOwner())) {
            Move kingCastle = addKingSideCastlePosition(board);
            if (kingCastle != null) {
                validPositions.add(kingCastle);
            }
        }

        // castling queen side
        // TODO -> Might be able to remove this when rule
        if (board.getGameState().canCastleQueenSide(getOwner())) {
            Move queenCastle = addQueenSideCastlePosition(board);
            if (queenCastle != null) {
                validPositions.add(queenCastle);
            }
        }

        // Return valid positions that the king can move to
        return validPositions;
    }

    /**
     * King side castle location (assumes that you can king side castle)
     * @param board the current board
     * @return king side castle location
     */
    private Move addKingSideCastlePosition(Board board) {
        System.out.println("Checking king side castle for: " + getOwner());
        return checkCastlingDirection(board, Direction.RIGHT);
    }

    /**
     * Add queen side castle position for the king (assumes that you can queen side castle)
     * @param board the current board
     * @return queen side castle location
     */
    private Move addQueenSideCastlePosition(Board board) {
        System.out.println("Checking queen side castle for: " + getOwner());
        return checkCastlingDirection(board, Direction.LEFT);
    }

    /**
     * Check a given end position for castling (either king side or queen side)
     * @param board the current board state
     * @param direction the direction to go in
     * @return the castling move if it is valid
     */
    private Move checkCastlingDirection(Board board, Direction direction) {

        Player opponent = getOwner().opposite(board);

        // If the king position is targeted or the king has previously moved, castling is not allowed
        Position kingPosition = board.getKingPosition(getOwner());
        boolean kingMoved = board.getMoveHistory()
                            .getMoves(move -> move.getMovedPiece().equals(this))
                            .findFirst().orElse(null) != null;
        if(kingMoved || MoveUtils.isTileTargeted(opponent, kingPosition) != null) {
            return null;
        }

        // Get offset position for rook
        boolean isLeft = direction == Direction.LEFT;
        Position rookPosition = isLeft
                ? BoardUtils.getOffSetPosition(kingPosition, -4, 0)
                : BoardUtils.getOffSetPosition(kingPosition, 3, 0);

        // Get our tiles
        Map<Position, Tile> tiles = board.getTileMap();

        // If the expected rook tile is not occupied by a rook, castling not allowed
        Tile rookTile = tiles.get(rookPosition);
        if(!rookTile.isOccupied() || !(rookTile.getPiece() instanceof Rook)) {
            return null;
        }

        // Only getting tiles between the king (not inclusive) and location where king will move to (inclusive)
        Position targetCastlePos = BoardUtils.getOffSetPosition(kingPosition, isLeft ? -2 : 2, 0);
        Predicate<Map.Entry<Position, Tile>> betweenKingTarget = entry -> {
            Position toTest = entry.getKey();
            int toTestCol = toTest.getColumn(); // Column we are testing
            int castleCol = targetCastlePos.getColumn(); // Column we are castling to
            int kingCol = kingPosition.getColumn(); // Column of the king
            return isLeft ? toTestCol >= castleCol && toTestCol < kingCol : toTestCol <= castleCol && toTestCol > kingCol;
        };

        // Tiles must be unoccupied and not targeted
        Predicate<Map.Entry<Position, Tile>> notOccupiedOrTargeted = entry ->
                !entry.getValue().isOccupied() && MoveUtils.isTileTargeted(opponent, entry.getKey()) == null;

        // If all conditions pass, then add the move
        Tile kingTile = tiles.get(kingPosition);
        if(tiles.entrySet().stream()
            .filter(entry -> entry.getKey().getRow() == kingPosition.getRow()) // Must be on same row as king
            .filter(betweenKingTarget) // Must be between king and target position
            .allMatch(notOccupiedOrTargeted)) { // Nor can they be targeted
            return new Move(this, kingTile, null, tiles.get(targetCastlePos));
        }

        return null;
    }

    /**
     * A king can only move 1 square at a time unless castling
     * @return 1
     */
    @Override
    public int getMaxSpacesMoved() {
        return 1;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "K" : "k";
    }
}
