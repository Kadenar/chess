package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;
import com.chess.engine.board.FenUtils;
import com.chess.ui.ChessFrame;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class GameOptionsMenu extends JMenu {

    public GameOptionsMenu() {
        super("Game options");
        populateMenu();
    }

    /**
     * Adds standard game options
     */
    private void populateMenu() {
        // Add new game option
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> resetGameState());
        add(newGame);

        // Highlight valid moves
        JCheckBoxMenuItem highlighting = new JCheckBoxMenuItem("Highlight valid moves?");
        highlighting.setState(GameSettings.getInstance().isEnableHighlighting());
        highlighting.addItemListener(e -> enableHighlighting());
        add(highlighting);

        // Quit the game
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));
        add(quit);
    }

    /**
     * Reset the game state (on click of new game button)
     */
    private void resetGameState() {
        // Recreate the board object with default position
        ChessFrame frame = (ChessFrame) SwingUtilities.getRoot(this);
        Board board = frame.getBoard();
        board.updateBoardFromFen(FenUtils.DEFAULT_POSITION);
        frame.getHistoryPanel().reset();
        board.displayBoard();
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableHighlighting() {
        GameSettings settings = GameSettings.getInstance();
        settings.setEnableHighlighting(!settings.isEnableHighlighting());
        ChessFrame frame = (ChessFrame) SwingUtilities.getRoot(this);
        frame.getBoard().displayBoard();
    }

}
