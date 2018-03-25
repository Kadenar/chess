package com.chess.ui;

import com.chess.engine.board.Board;

import javax.swing.*;
import java.awt.*;

public class BoardGUI extends JFrame {

    private JPanel mainBoard;
    private Board board;
    private final static int WINDOW_WIDTH = 600;
    private final static int WINDOW_HEIGHT = 600;

    public BoardGUI(Board board) {
        super("Kadenar Chess");
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.board = board;
        initializeBoard();
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.add(mainBoard);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

    }

    public void initializeBoard() {
        this.mainBoard = new JPanel();
        mainBoard.setLayout(new BorderLayout());
        mainBoard.add(new FileHeaders(), BorderLayout.SOUTH);
        mainBoard.add(new RankHeaders(), BorderLayout.WEST);
        JPanel boardTiles = new JPanel();
        boardTiles.setLayout(new GridLayout(8,8));
        // Add all the tiles
        for(int i = 0; i < board.getTiles().size(); i++) {
            TileUI tile = new TileUI(board.getTiles().get(i));
            tile.setToolTipText("tile: " + i);
            boardTiles.add(tile, i);
        }

        mainBoard.add(boardTiles, BorderLayout.CENTER);
    }
}
