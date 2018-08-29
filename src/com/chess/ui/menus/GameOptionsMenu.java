package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;
import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;
import com.chess.ui.ChessFrame;

import javax.swing.*;
import java.awt.event.KeyEvent;

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
        newGame.setMnemonic(KeyEvent.VK_N);
        newGame.addActionListener(e -> resetGameState());
        add(newGame);

        // Highlight valid moves
        JCheckBoxMenuItem highlighting = new JCheckBoxMenuItem("Highlight valid moves?");
        highlighting.setState(GameSettings.getInstance().isEnableHighlighting());
        highlighting.setMnemonic(KeyEvent.VK_H);
        highlighting.addItemListener(e -> enableHighlighting());
        add(highlighting);

        // Quit the game
        JMenuItem quit = new JMenuItem("Quit");
        quit.setMnemonic(KeyEvent.VK_Q);
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
        BoardUtils.getInstance().updateBoardFromFen(board, FenUtils.DEFAULT_POSITION);
        owningFrame.getBoardPanel().reloadBoard(board);
        owningFrame.getBoardPanel().getLayeredPane().revalidate();
        owningFrame.getBoardPanel().getLayeredPane().repaint();
        owningFrame.getBoardPanel().getLayeredPane().setVisible(true);
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableHighlighting() {
        GameSettings settings = GameSettings.getInstance();
        settings.setEnableHighlighting(!settings.isEnableHighlighting());
    }

}
