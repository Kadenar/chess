package com.chess.engine.utils;

import com.chess.engine.board.Board;
import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
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
    public void updateBoardWithFen(Board board, String fen) {
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Converts coordinates to square index.
     *
     *@param col the column (file)
     *@param row the row (rank)
     *@return the square index
     */
    public static final int coorToSqi(int col, int row) {
        return row * 8 + col;
    }

    /**
     * Extract the column of a square index.
     *
     *@param sqi the square index
     *@return the column
     */
    public static final int sqiToCol(int sqi) {
        return sqi % 8;
    }

    /*
    * Construct a game piece at the given position
    */
    protected Piece constructPiece(char ch, Position position) {
        Piece piece = null;

        // Determine the color this piece belongs to
        Player color = Character.isUpperCase(ch) ? Player.WHITE : Player.BLACK;

        // Determine the type of piece
        switch(Character.toLowerCase(ch)) {
            case 'p':
                piece = new Pawn(color, position);
                break;
            case 'b':
                piece = new Bishop(color, position);
                break;
            case 'r':
                piece = new Rook(color, position);
                break;
            case 'n':
                piece = new Knight(color, position);
                break;
            case 'q':
                piece = new Queen(color, position);
                break;
            case 'k':
                piece = new King(color, position);
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