package com.chess.ui;

import com.chess.engine.board.Board;
import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {

    final static int WINDOW_WIDTH = 750;
    final static int WINDOW_HEIGHT = 750;

    private BoardPanel boardPanel;

    public ChessFrame(Board board) {
        super("Chess");
        this.setMinimumSize(new Dimension(800, 700));
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.boardPanel = new BoardPanel(board);

        // Add all headers and panels to the frame
        addHeadersAndPanels();

        //this.boardPanel.setBounds(0, 0, WINDOW_WIDTH-20, WINDOW_HEIGHT-60);
        this.setVisible(true);
    }

    /**
     * Add the ranks and file headers as well as history / captured pieces and board
     */
    private void addHeadersAndPanels() {
        getContentPane().setLayout(new BorderLayout());

        // Add new game button
        Button newGame = new Button("New game");
        getContentPane().add(newGame, BorderLayout.NORTH);
        newGame.addActionListener(e -> resetGameState());

        // Add file and rank headers, history and board
        getContentPane().add(new RankHeaders(), BorderLayout.WEST);
        getContentPane().add(new FileHeaders(), BorderLayout.SOUTH);
        getContentPane().add(new HistoryPanel(), BorderLayout.EAST);
        getContentPane().add(boardPanel.getLayeredPane(), BorderLayout.CENTER);

    }

    /**
     * Reset the game state (on click of new game button)
     */
    private void resetGameState() {

        // Remove all objects from the frame
        boardPanel.getLayeredPane().removeAll();

        // Recreate the board object with default position
        BoardUtils.getInstance().updateBoardFromFen(boardPanel.getBoard(), FenUtils.DEFAULT_POSITION);

        boardPanel.initBoardUI();
        boardPanel.getLayeredPane().invalidate();
        boardPanel.getLayeredPane().repaint();
    }
}
