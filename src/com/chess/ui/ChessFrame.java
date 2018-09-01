package com.chess.ui;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;
import com.chess.engine.sound.SoundUtils;
import com.chess.ui.headers.FileHeaders;
import com.chess.ui.headers.RankHeaders;
import com.chess.ui.menus.DebugOptionsMenu;
import com.chess.ui.menus.GameOptionsMenu;
import com.chess.ui.panels.HistoryPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class ChessFrame extends JFrame {

    public final static int WINDOW_WIDTH = 750;
    public final static int WINDOW_HEIGHT = 750;

    private final Board board;
    private final RankHeaders rankHeaders;
    private final FileHeaders fileHeaders;
    private final HistoryPanel historyPanel;

    public ChessFrame(Board board) {
        super("Chess");
        this.board = board;
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
        this.historyPanel = new HistoryPanel(board);
        this.rankHeaders = new RankHeaders();
        this.fileHeaders = new FileHeaders();
        addHeadersAndPanels();

        // Display the frame and play opening sound
        this.setVisible(true);
        board.setPreferredSize(board.getLayeredPane().getSize());

        // Play start game sound after frame is visible
        SoundUtils.playMoveSound("startGame");

        // Debugging for the tile locations
        if(GameSettings.getInstance().isEnableDebugging()) {
            board.getTileMap().values().forEach(
                    tile -> tile.add(new JLabel("<html> x=" + tile.getLocation().x + "<br/> y="
                            + tile.getLocation().y + "</html>")));
        }
    }



    /**
     * Add the ranks and file headers as well as history / captured pieces and board
     */
    private void addHeadersAndPanels() {
        // Set content to use border layout
        getContentPane().setLayout(new BorderLayout());

        // Add file and rank headers, history and board
        getContentPane().add(rankHeaders, BorderLayout.WEST);
        getContentPane().add(fileHeaders, BorderLayout.SOUTH);
        getContentPane().add(historyPanel, BorderLayout.EAST);
        getContentPane().add(board.getLayeredPane(), BorderLayout.CENTER);
    }

    /**
     * Get our board panel
     * @return the board to reload
     */
    public Board getBoardPanel() {
        return this.board;
    }

    /**
     * Get our history panel
     * @return the history of moves
     */
    public HistoryPanel getHistoryPanel() { return this.historyPanel; }
}
