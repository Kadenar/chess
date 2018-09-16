package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class GameOptionsMenu extends JMenu {

    private final Board board;

    public GameOptionsMenu(Board board) {
        super("Game options");
        this.board = board;
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
        highlighting.setState(GameSettings.INSTANCE.isEnableHighlighting());
        highlighting.addItemListener(e -> enableHighlighting());
        add(highlighting);

        // Display coordinates
        JCheckBoxMenuItem displayCoords = new JCheckBoxMenuItem("Display tile coordinates?");
        displayCoords.setState(GameSettings.INSTANCE.isDisplayTilePositions());
        displayCoords.addItemListener(e -> enableCoordinateDisplay());
        add(displayCoords);

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
        board.reset();
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableHighlighting() {
        GameSettings settings = GameSettings.INSTANCE;
        settings.setEnableHighlighting(!settings.isEnableHighlighting());
        board.displayBoard();
    }

    /**
     * Toggle whether to display coordinate positions
     */
    private void enableCoordinateDisplay() {
        GameSettings settings = GameSettings.INSTANCE;
        settings.setDisplayTilePositions(!settings.isDisplayTilePositions());
        board.displayBoard();
    }

}
