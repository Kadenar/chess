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

        // Only check castling positions if the king is not targeted and hasn't moved
        List<Move> movesByKing = board.getMoveHistory().getMoves(move -> move.getMovedPiece().equals(this)).collect(Collectors.toList());
        if(movesByKing.size() == 0 && MoveUtils.isTileTargeted(getOwner().opposite(board), currentTile.getPosition()) == null) {

            // castling king side
            // TODO -> Might be able to remove this when rule
            if (board.getGameState().canCastleKingSide(getOwner())) {
                Move kingCastle = addKingSideCastlePosition(board, currentTile.getPosition());
                if (kingCastle != null) {
                    validPositions.add(kingCastle);
                }
            }

            // castling queen side
            // TODO -> Might be able to remove this when rule
            if (board.getGameState().canCastleQueenSide(getOwner())) {
                Move queenCastle = addQueenSideCastlePosition(board, currentTile.getPosition());
                if (queenCastle != null) {
                    validPositions.add(queenCastle);
                }
            }
        }

        // Return valid positions that the king can move to
        return validPositions;
    }

    /**
     * King side castle location (assumes that you can king side castle)
     * @param board the current board
     * @param currentPosition the current position of the king
     * @return king side castle location
     */
    private Move addKingSideCastlePosition(Board board, Position currentPosition) {
        return checkCastlingDirection(board, currentPosition, Direction.RIGHT);
    }

    /**
     * Add queen side castle position for the king (assumes that you can queen side castle)
     * @param board the current board
     * @param currentPosition the current position of the king
     * @return queen side castle location
     */
    private Move addQueenSideCastlePosition(Board board, Position currentPosition) {
        return checkCastlingDirection(board, currentPosition, Direction.LEFT);
    }

    /**
     * Check a given end position for castling (either king side or queen side)
     * @param board the current board state
     * @param direction the direction to go in
     * @return the castling move if it is valid
     */
    private Move checkCastlingDirection(Board board, Position kingPosition, Direction direction) {

        // If the king position is targeted or the king has previously moved, castling is not allowed
        List<Move> movesByKing = board.getMoveHistory().getMoves(move -> move.getMovedPiece().equals(this)).collect(Collectors.toList());
        if(movesByKing.size() > 0 || MoveUtils.isTileTargeted(getOwner().opposite(board), kingPosition) != null) {
            return null;
        }

        // Get offset position for rook
        Map<Position, Tile> tiles = board.getTileMap();
        boolean isLeft = direction == Direction.LEFT;
        Position rookPosition = isLeft
                ? BoardUtils.getOffSetPosition(kingPosition, -4, 0)
                : BoardUtils.getOffSetPosition(kingPosition, 3, 0);

        // If the expected rook tile is not occupied by a rook, castling not allowed
        Tile rookTile = board.getTileMap().get(rookPosition);
        if(!rookTile.isOccupied() || !(rookTile.getPiece() instanceof Rook)) {
            return null;
        }

        // Only getting tiles on the same row as the king
        Predicate<Map.Entry<Position, Tile>> sameRow = entry -> entry.getKey().getRow() == kingPosition.getRow();

        // Only getting tiles between the king and location where king will move to
        Position targetCastlePos = BoardUtils.getOffSetPosition(kingPosition, isLeft ? -2 : 2, 0);
        Tile targetCastleTile = board.getTileMap().get(targetCastlePos);
        Predicate<Map.Entry<Position, Tile>> betweenKingTarget = entry -> {
            Position toTest = entry.getKey();
            return isLeft
                    ? toTest.getColumn() >= targetCastlePos.getColumn() && toTest.getColumn() < kingPosition.getColumn()
                    : toTest.getColumn() <= targetCastlePos.getColumn() && toTest.getColumn() > kingPosition.getColumn();
        };

        // Tiles can not be occupied
        Predicate<Map.Entry<Position, Tile>> notOccupied = entry -> !entry.getValue().isOccupied();

        // Tiles cannot be targeted by opponent
        Predicate<Map.Entry<Position, Tile>> notTargeted = entry -> MoveUtils.isTileTargeted(getOwner().opposite(board), entry.getKey()) == null;

        // If all conditions pass, then add the move
        Tile kingTile = board.getTileMap().get(kingPosition);
        if(tiles.entrySet().stream()
                .filter(sameRow)
                .filter(betweenKingTarget)
                .filter(notOccupied)
                .filter(notTargeted).collect(Collectors.toList()).size() > 0) {
            return new Move(this, kingTile, null, targetCastleTile);
        }

        return null;

        /*
        int kingColumn = kingPosition.getColumn();
        int increment = isLeft ? -1 : 1;
        for(int i = kingColumn + increment; i >= 0 && i < 8; i = i + increment) {
            Position offSetPosition = BoardUtils.getOffSetPosition(kingPosition, i - kingColumn, 0);

            // If the tile is occupied...
            Tile offsetTile = tiles.get(offSetPosition);
            if(offsetTile.isOccupied()) {
                // If the piece is a rook and has not moved
                Piece pieceAtOffset = offsetTile.getPiece();
                if(pieceAtOffset instanceof Rook
                        && board.getMoveHistory()
                        .getMoves(e -> e.getMovedPiece().getOwner().equals(piece.getOwner()))
                        .anyMatch(move -> move.getMovedPiece().equals(pieceAtOffset))) {
                    // TODO Disabled until can figure out how to get it working
                    Tile currentTile = board.getTileMap().get(kingPosition);
                    Tile kingCastleLoc = tiles.get(BoardUtils.getOffSetPosition(kingPosition, increment * 2, 0));
                    castlingMove = new Move(this, currentTile, null, kingCastleLoc);
                }

                // If we reached an occupied tile, we are done
                break;
            }

            // If the tile is targeted, castling is not allowed
            if(MoveUtils.isTileTargeted(getOwner().opposite(board), offSetPosition) != null) {
                break;
            }
        }

        return castlingMove;*/
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
