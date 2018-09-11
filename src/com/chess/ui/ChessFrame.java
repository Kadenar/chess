package com.chess.ui;

import com.chess.engine.board.Board;
import com.chess.engine.sound.SoundUtils;
import com.chess.ui.headers.FileHeaders;
import com.chess.ui.headers.RankHeaders;
import com.chess.ui.menus.DebugOptionsMenu;
import com.chess.ui.menus.GameOptionsMenu;
import com.chess.ui.panels.GameStatePanel;
import com.chess.ui.panels.MoveHistoryPanel;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class ChessFrame extends JFrame {

    private final Board board;
    private final RankHeaders rankHeaders;
    private final FileHeaders fileHeaders;
    private final GameStatePanel historyPanel;

    public ChessFrame(Board board) {
        super("Chess");
        this.board = board;
        this.setMinimumSize(new Dimension(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT));
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);

        // Add game options menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GameOptionsMenu());
        menuBar.add(new DebugOptionsMenu());
        setJMenuBar(menuBar);

        // Add all headers and panels to the frame
        this.historyPanel = new GameStatePanel(board);
        this.rankHeaders = new RankHeaders();
        this.fileHeaders = new FileHeaders();
        addHeadersAndPanels();

        // Display the frame and play opening sound
        this.setVisible(true);

        // Play start game sound after frame is visible
        SoundUtils.playMoveSound("startGame");
    }

    /**
     * Add the ranks and file headers as well as history / captured pieces and board
     */
    private void addHeadersAndPanels() {
        // Set content to use border layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(rankHeaders, BorderLayout.WEST);
        getContentPane().add(fileHeaders, BorderLayout.SOUTH);
        getContentPane().add(historyPanel, BorderLayout.EAST);
        getContentPane().add(board.getLayeredPane(), BorderLayout.CENTER);
    }

    /**
     * Get our board panel
     * @return the board to reload
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Get our history panel
     * @return the history of moves
     */
    public GameStatePanel getHistoryPanel() { return this.historyPanel; }

}
