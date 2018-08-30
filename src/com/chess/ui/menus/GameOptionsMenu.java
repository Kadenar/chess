package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.FenUtils;
import com.chess.ui.ChessFrame;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

class GameOptionsMenu extends JMenu {

    private ChessFrame owningFrame;

    GameOptionsMenu(ChessFrame owningFrame) {
        super("Game options");
        this.owningFrame = owningFrame;
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

        // Remove all objects from the frame
        owningFrame.getBoardPanel().getLayeredPane().removeAll();

        // Recreate the board object with default position
        Board board = owningFrame.getBoardPanel().getBoard();
        BoardUtils.updateBoardFromFen(board, FenUtils.DEFAULT_POSITION);
        owningFrame.getBoardPanel().reloadBoard(board);
        owningFrame.getBoardPanel().getLayeredPane().revalidate();
        owningFrame.getBoardPanel().getLayeredPane().repaint();
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableHighlighting() {
        GameSettings settings = GameSettings.getInstance();
        settings.setEnableHighlighting(!settings.isEnableHighlighting());
    }

}
