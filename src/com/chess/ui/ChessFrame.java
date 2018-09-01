package com.chess.ui;

import com.chess.engine.board.Board;
import com.chess.engine.sound.SoundUtils;
import com.chess.ui.headers.FileHeaders;
import com.chess.ui.headers.RankHeaders;
import com.chess.ui.menus.DebugOptionsMenu;
import com.chess.ui.menus.GameOptionsMenu;
import com.chess.ui.panels.BoardPanel;
import com.chess.ui.panels.HistoryPanel;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class ChessFrame extends JFrame {

    public final static int WINDOW_WIDTH = 750;
    public final static int WINDOW_HEIGHT = 750;

    private BoardPanel boardPanel;
    private HistoryPanel historyPanel;

    public ChessFrame(Board board) {
        super("Chess");
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);

        // Add game options menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GameOptionsMenu());
        menuBar.add(new DebugOptionsMenu());
        setJMenuBar(menuBar);

        // Add all headers and panels to the frame
        addHeadersAndPanels(board);

        // Display the frame and play opening sound
        this.setVisible(true);
        boardPanel.setSize(boardPanel.getLayeredPane().getSize());
        SoundUtils.playMoveSound("startGame");
    }



    /**
     * Add the ranks and file headers as well as history / captured pieces and board
     */
    private void addHeadersAndPanels(Board board) {
        getContentPane().setLayout(new BorderLayout());

        // Initialize 8x8 grid containing representation of our board
        this.boardPanel = new BoardPanel(board);
        this.historyPanel = new HistoryPanel(board);

        // Add file and rank headers, history and board
        getContentPane().add(new RankHeaders(), BorderLayout.WEST);
        getContentPane().add(new FileHeaders(), BorderLayout.SOUTH);
        getContentPane().add(historyPanel, BorderLayout.EAST);
        getContentPane().add(boardPanel.getLayeredPane(), BorderLayout.CENTER);
    }

    /**
     * Get our board panel
     * @return the board to reload
     */
    public BoardPanel getBoardPanel() {
        return this.boardPanel;
    }
    public HistoryPanel getHistoryPanel() { return this.historyPanel; }
}
