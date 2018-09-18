package com.chess.ui;

import com.chess.ChessConsts;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardPanel;
import com.chess.engine.moves.MoveHistory;
import com.chess.engine.moves.MoveType;
import com.chess.ui.menus.DebugOptionsMenu;
import com.chess.ui.menus.GameOptionsMenu;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class ChessFrame extends JFrame {

    private final BoardPanel boardPanel;
    private final MoveHistory history;

    public ChessFrame(Board board) {
        super("Chess");
        this.setMinimumSize(new Dimension(ChessConsts.WINDOW_WIDTH, ChessConsts.WINDOW_HEIGHT));
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(ChessConsts.WINDOW_WIDTH, ChessConsts.WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);

        // Initialize board and panel as well as history
        this.boardPanel = new BoardPanel(board);
        this.history = board.getMoveHistory();

        // Add game options menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GameOptionsMenu(board));
        menuBar.add(new DebugOptionsMenu(board));
        setJMenuBar(menuBar);

        // Add all headers and panels to the frame
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
        getContentPane().add(history, constraints);
    }
}
