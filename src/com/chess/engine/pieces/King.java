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
import java.util.Map;
import java.util.Set;

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

        // Only check castling positions if the king is not targeted
        if(MoveUtils.isTileTargeted(getOwner().opposite(board), currentTile.getPosition()) == null) {
            // castling king side
            if (board.getGameState().canCastleKingSide(getOwner())) {
                Move kingCastle = addKingSideCastlePosition(board, currentTile.getPosition());
                if (kingCastle != null) {
                    validPositions.add(kingCastle);
                }
            }

            // castling queen side
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
        return checkCastlingDirection(board, currentPosition, BoardUtils.getOffSetPosition(currentPosition, 3, 0));
    }

    /**
     * Add queen side castle position for the king (assumes that you can queen side castle)
     * @param board the current board
     * @param currentPosition the current position of the king
     * @return queen side castle location
     */
    private Move addQueenSideCastlePosition(Board board, Position currentPosition) {
        // TODO -> Don't need to check for offset tile being in check for queen side next to rook because king doesn't move there
        // TODO -> B1 / B8 are ok to be targeted
        return checkCastlingDirection(board, currentPosition, BoardUtils.getOffSetPosition(currentPosition, -4, 0));
    }

    /**
     * Check a given end position for castling (either king side or queen side)
     * @param board the current board state
     * @param piece the piece to move
     * @param endPosition the end position
     * @return the castling move if it is valid
     */
    private Move checkCastlingDirection(Board board, Position kingPosition, Position endPosition) {

        Move castlingMove = null;
        Map<Position, Tile> tiles = board.getTileMap();
        int kingColumn = kingPosition.getColumn();
        int increment = kingColumn > endPosition.getColumn() ? -1 : 1;
        for(int i = kingColumn + increment; i >= 0 && i < 8; i = i + increment) {
            Position offSetPosition = BoardUtils.getOffSetPosition(kingPosition, i - kingColumn, 0);

            // If the tile is occupied...
            Tile offsetTile = tiles.get(offSetPosition);
            if(offsetTile.isOccupied()) {
                // If the piece is a rook and has not moved
                Piece pieceAtOffset = offsetTile.getPiece();
                if(pieceAtOffset instanceof Rook
                        /*&& board.getMoveHistory()
                        .getMoves(e -> e.getMovedPiece().getOwner().equals(piece.getOwner()))
                        .anyMatch(move -> move.getMovedPiece().equals(pieceAtOffset))*/) {
                    // TODO Disabled until can figure out how to get it working
                    Tile currentTile = board.getTileMap().get(kingPosition);
                    Tile kingCastleLoc = tiles.get(BoardUtils.getOffSetPosition(kingPosition, increment * 2, 0));
                    castlingMove = new Move(this, currentTile, pieceAtOffset, kingCastleLoc);
                }

                // If we reached an occupied tile, we are done
                break;
            }

            // If the tile is targeted, castling is not allowed
            if(MoveUtils.isTileTargeted(getOwner().opposite(board), offSetPosition) != null) {
                break;
            }
        }

        return castlingMove;
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
