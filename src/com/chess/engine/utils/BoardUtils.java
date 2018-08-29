package com.chess.engine.utils;

import com.chess.engine.board.Board;
import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.*;
import com.chess.engine.utils.FenUtils.FenException;

import java.util.Map;

public class BoardUtils {

    private static final BoardUtils INSTANCE = getInstance();
    private Board board;

    public static BoardUtils getInstance() {
        if(INSTANCE == null) {
            return new BoardUtils();
        }

        return INSTANCE;
    }

    // This object can only reference one board at a time
    private BoardUtils() { /*Singleton*/ }

    public Board getBoard() {
        return this.board;
    }

    /**
     * Updates reference to game board and recreates it with given fen
     * @param board the game board to be created
     * @param fen the fen string to update the board with
     */
    public void updateBoardFromFen(Board board, String fen) {
        this.board = board;

        // Clear each player's pieces
        for (Map.Entry<String, Player> entry : board.getPlayers().entrySet()) {
            entry.getValue().getPieces().clear();
        }

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
    public static Position sqiToPosition(final String sqi) {
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
    protected static Piece constructPiece(final char ch) {
        Piece piece = null;

        // Determine the color this piece belongs to
        Player color = Character.isUpperCase(ch) ? Player.WHITE : Player.BLACK;

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