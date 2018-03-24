package com.chess.engine.utils;

import com.chess.engine.board.Board;
import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.pieces.*;

import java.util.Map;

public class BoardUtils {

    private static final BoardUtils INSTANCE = getInstance();

    public static BoardUtils getInstance() {
        if(INSTANCE == null) {
            return new BoardUtils();
        }

        return INSTANCE;
    }

    private BoardUtils() {
        // Do not allow instantiation elsewhere
    }

    public void updateBoardWithFen(Board board, String fen) {
        // Clear each player's pieces
        for (Map.Entry<String, Player> entry : board.getPlayers().entrySet()) {
            entry.getValue().getPieces().clear();
        }

        // Clear tiles on the board
        board.getTiles().clear();

        // Load in the default position
        try {
            board.loadFen(fen);
        } catch(Exception e) {
            System.out.println("Error encountered parsing fen");
        }
    }

    public Piece getPiece(char ch, Position position) {
        Piece piece = null;

        // Determine the color this piece belongs to
        Player color = Character.isUpperCase(ch) ? Player.BLACK : Player.WHITE;

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

        // return the piece
        return piece;
    }
}
