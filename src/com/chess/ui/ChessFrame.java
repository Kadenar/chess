package com.chess.ui;

import com.chess.engine.board.Board;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {

    final static int WINDOW_WIDTH = 750;
    final static int WINDOW_HEIGHT = 750;

    private BoardPanel boardPanel;

    public ChessFrame(Board board) {
        super("Chess");
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.boardPanel = new BoardPanel(board);

        addMenuBar();

        // Add all headers and panels to the frame
        addHeadersAndPanels();
        this.setVisible(true);
    }

    private void addMenuBar() {
        setJMenuBar(new GameOptionsMenu(this));
    }

    /**
     * Add the ranks and file headers as well as history / captured pieces and board
     */
    private void addHeadersAndPanels() {
        getContentPane().setLayout(new BorderLayout());

        // Add file and rank headers, history and board
        getContentPane().add(new RankHeaders(), BorderLayout.WEST);
        getContentPane().add(new FileHeaders(), BorderLayout.SOUTH);
        getContentPane().add(new HistoryPanel(), BorderLayout.EAST);
        getContentPane().add(boardPanel.getLayeredPane(), BorderLayout.CENTER);
    }

    /**
     * Get our board panel
     * @return the board to reload
     */
    BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
