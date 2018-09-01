package com.chess.engine.board;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.FenUtils.FenException;
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

    /**
     * Updates reference to game board and recreates it with given fen
     * @param board the game board to be created
     * @param fen the fen string to update the board with
     */
    public static void updateBoardFromFen(Board board, String fen) {
        // Clear each player's pieces / captured pieces
        board.getPlayers().values().forEach(player -> {
            player.getPieces().clear();
            player.getCapturedPieces().clear();
        });

        // Clear tiles on the board
        board.getTileMap().clear();

        // Load in the default position
        try {
            FenUtils.loadFen(board, fen);
        } catch (FenException e) {
            System.err.println(e.getMessage());
        }
    }

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

    /*
    * Construct a game piece at the given position
    */
    static Piece constructPiece(final Board board, final char ch) {
        Piece piece = null;

        // Determine the color this piece belongs to
        Player color = Character.isUpperCase(ch)
                ? board.getPlayers().get(Player.Color.WHITE) : board.getPlayers().get(Player.Color.BLACK);

        // Determine the type of piece
        switch(Character.toLowerCase(ch)) {
            case 'p':
                piece = new Pawn(color);
                break;
            case 'b':
                piece = new Bishop(color);
                break;
            case 'r':
                piece = new Rook(color);
                break;
            case 'n':
                piece = new Knight(color);
                break;
            case 'q':
                piece = new Queen(color);
                break;
            case 'k':
                piece = new King(color);
                break;
            default:
                break;
        }

        // Add the piece for that player
        color.addPiece(piece);

        // return the piece
        return piece;
    }
}