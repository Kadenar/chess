package com.chess.ui;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;
import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class GameOptionsMenu extends JMenuBar {

    private ChessFrame owningFrame;
    private JMenu gameOptions;

    public GameOptionsMenu(ChessFrame owningFrame) {
        super();
        this.owningFrame = owningFrame;
        addGameOptions();

        // TODO Add more menus here later
    }

    private void addGameOptions() {
        gameOptions = new JMenu("Game options");

        // Add new game button
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.setMnemonic(KeyEvent.VK_N);
        newGame.addActionListener(e -> resetGameState());
        gameOptions.add(newGame);

        // Highlight valid moves
        JCheckBoxMenuItem highlighting = new JCheckBoxMenuItem("Highlight valid moves?");
        highlighting.setState(GameSettings.getInstance().isEnableHighlighting());
        highlighting.setMnemonic(KeyEvent.VK_H);
        highlighting.addItemListener(e -> enableHighlighting());
        gameOptions.add(highlighting);


        // Print current FEN
        JMenuItem printFen = new JMenuItem("Print Fen");
        printFen.setMnemonic(KeyEvent.VK_P);
        printFen.addActionListener(e -> printFen());
        gameOptions.add(printFen);

        // Quit the game
        JMenuItem quit = new JMenuItem("Quit");
        quit.setMnemonic(KeyEvent.VK_Q);
        quit.addActionListener(e -> System.exit(0));
        gameOptions.add(quit);

        // Add menu to the menu bar
        add(gameOptions);
    }

    /**
     * Reset the game state (on click of new game button)
     */
    private void resetGameState() {

        // Remove all objects from the frame
        owningFrame.getBoardPanel().getLayeredPane().removeAll();

        // Recreate the board object with default position
        BoardUtils.getInstance().updateBoardFromFen(owningFrame.getBoardPanel().getBoard(), FenUtils.DEFAULT_POSITION);

        owningFrame.getBoardPanel().initBoardUI();
        owningFrame.revalidate();
        owningFrame.repaint();
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableHighlighting() {
        GameSettings settings = GameSettings.getInstance();
        settings.setEnableHighlighting(!settings.isEnableHighlighting());
    }

    /**
     * Print the current board state as a FEN string
     */
    private void printFen() {
        Board board = BoardUtils.getInstance().getBoard();
        System.out.println(FenUtils.getFen(board));
        System.out.println(board.toString());
    }
}
