package com.chess;

public final class ChessConsts {

    private ChessConsts() { /*Static construct*/ }

    public final static int NUM_TILES = 64;
    public static final int NUM_TILE = NUM_TILES / 8;
    public static final int NUM_PLAYERS = 2;
    public static final int MAX_MOVE_LOCS = 27;
    public static final int MAX_PIECES = 24;

    public final static int WINDOW_WIDTH = 1000;
    public final static int WINDOW_HEIGHT = 860;

    public final static int HISTORY_WIDTH = 250;
    public final static int HISTORY_HEIGHT = 730;

    public final static int BOARD_WIDTH = 700;
    public final static int BOARD_HEIGHT = 760;

    public final static int FILE_WIDTH = 750 / 8;
    public final static int FILE_HEIGHT = WINDOW_HEIGHT;

    public final static int RANK_WIDTH = 20;
    public final static int RANK_HEIGHT = WINDOW_HEIGHT / 8;
}