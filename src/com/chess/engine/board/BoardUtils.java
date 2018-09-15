package com.chess.engine.board;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;

public class BoardUtils {

    // This object can only reference one board at a time
    private BoardUtils() { /*Singleton*/ }

    /*
    * Return a position given a square index such as A8
    */
    static Position sqiToPosition(final String sqi) {
        if("-".equals(sqi)) {
            return null;
        }

        char colChar = sqi.charAt(0);
        int col = colChar - 97;
        char row = sqi.charAt(1);

        return new Position(Integer.parseInt(row + ""), col);
    }

    /**
     * Get a position with given row and column offset
     * @param origin the starting position
     * @param colOffset how many columns (left or right)
     * @param rowOffset how many rows (up or down)
     * @return the position with given offset
     */
    public static Position getOffSetPosition(Position origin, int colOffset, int rowOffset) {
        return new Position(origin.getRow() + rowOffset, origin.getColumn() + colOffset);
    }

    /*
    * Get difference in rows
    */
    public static int deltaRow(final Tile pos1, final Tile pos2) {
        return Math.abs(pos1.getPosition().getRow() - pos2.getPosition().getRow());
    }

    /*
     * Get difference in cols
     */
    public static int deltaCol(final Tile pos1, final Tile pos2) {
        return Math.abs(pos1.getPosition().getColumn() - pos2.getPosition().getColumn());
    }

    /**
     * Get the type of piece to create
     * @param ch the character for type of piece
     * @param owner the owner of the piece
     * @return the type of piece that is created, or null if not a valid type of piece
     */
    public static Piece getTypeOfPieceToCreate(final char ch, Player owner) {

        Piece piece = null;

        // Determine the type of piece
        switch(Character.toLowerCase(ch)) {
            case 'p':
                piece = new Pawn(owner);
                break;
            case 'b':
                piece = new Bishop(owner);
                break;
            case 'r':
                piece = new Rook(owner);
                break;
            case 'n':
                piece = new Knight(owner);
                break;
            case 'q':
                piece = new Queen(owner);
                break;
            case 'k':
                piece = new King(owner);
                break;
            default:
                break;
        }

        return piece;
    }
}