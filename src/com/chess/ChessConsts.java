package com.chess;

public final class ChessConsts {

    private ChessConsts() { /*Static construct*/ }

    public final static int NUM_TILES = 64;
    public static final int NUM_TILE = NUM_TILES / 8;
    public static final int NUM_PLAYERS = 2;
    public static final int MAX_MOVE_LOCS = 27;
    public static final int MAX_PIECES = 24;

    public final static int WINDOW_WIDTH = 1000;
    public final static int WINDOW_HEIGHT = 760;

    public final static int HISTORY_WIDTH = WINDOW_WIDTH - 750;
    public final static int HISTORY_HEIGHT = WINDOW_HEIGHT - 130;

    public final static int BOARD_WIDTH = WINDOW_WIDTH - 275;
    public final static int BOARD_HEIGHT = WINDOW_HEIGHT - 78;
}