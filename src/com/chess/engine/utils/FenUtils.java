package com.chess.engine.utils;

public class FenUtils {

    // The default starting position for a chess game
    public static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static String getGamePieces(String fen) {
        return fen.split(" ")[0];
    }

    public static String getPlayerTurn(String fen) {
        return fen.split(" ")[1];
    }

    public static String getCastlingAbility(String fen) {
        return fen.split(" ")[2];
    }

    public static String getEnPassantSquare(String fen) {
        return fen.split(" ")[3];
    }

    public static int getHalfMove(String fen) {
        return Integer.valueOf(fen.split(" ")[4]);
    }

    public static int getFullMove(String fen) {
        return Integer.valueOf(fen.split(" ")[5]);
    }

    public static boolean isValid(String fen) {
        return true;
    }
}
