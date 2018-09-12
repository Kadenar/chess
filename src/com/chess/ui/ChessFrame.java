package com.chess.ui;

import com.chess.ChessConsts;
import com.chess.engine.board.Board;
import com.chess.engine.moves.MoveType;
import com.chess.ui.menus.DebugOptionsMenu;
import com.chess.ui.menus.GameOptionsMenu;
import com.chess.ui.panels.BoardPanel;
import com.chess.ui.panels.GameStatePanel;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {

    private final Board board;
    private final BoardPanel boardPanel;
    private final GameStatePanel historyPanel;

    public ChessFrame(Board board) {
        super("Chess");
        this.board = board;
        this.setMinimumSize(new Dimension(ChessConsts.WINDOW_WIDTH, ChessConsts.WINDOW_HEIGHT));
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(ChessConsts.WINDOW_WIDTH, ChessConsts.WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);

        // Add game options menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GameOptionsMenu());
        menuBar.add(new DebugOptionsMenu());
        setJMenuBar(menuBar);

        // Add all headers and panels to the frame
        this.historyPanel = new GameStatePanel(board);
        this.boardPanel = new BoardPanel(board);
        addHeadersAndPanels();

        // Display the frame and play opening sound
        this.setVisible(true);

        // Play start game sound after frame is visible
        MoveType.GAME_START.playSound();
    }

    /**
     * Add the ranks and file headers as well as history / captured pieces and board
     */
    private void addHeadersAndPanels() {
        // Set content to use border layout
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;

        // location / sizing for board
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        getContentPane().add(boardPanel, constraints);

        // location / sizing for history
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        getContentPane().add(historyPanel, constraints);
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
