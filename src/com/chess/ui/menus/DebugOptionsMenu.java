package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.FenUtils;
import com.chess.ui.ChessFrame;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.util.Map;

public class DebugOptionsMenu extends JMenu {

    public DebugOptionsMenu() {
        super("Debug options");
        populateMenu();
    }

    /**
     * Adds standard debug options
     */
    private void populateMenu() {

        // Enable or disable debugging
        JCheckBoxMenuItem enableDebugging = new JCheckBoxMenuItem("Enable debugging?");
        enableDebugging.setState(GameSettings.getInstance().isEnableDebugging());
        enableDebugging.addItemListener(e -> enableDebugging());
        add(enableDebugging);

        // Print out current FEN
        JMenuItem printFen = new JMenuItem("Print Fen");
        printFen.addActionListener(e -> printFen());
        add(printFen);

        // Print all pieces state
        JMenuItem printPieces = new JMenuItem("Print pieces");
        printPieces.addActionListener(e -> printPieces());
        add(printPieces);

        // Print all moves
        JMenuItem printGameMoves = new JMenuItem("Print valid moves");
        printGameMoves.addActionListener(e -> printGameMoves());
        add(printGameMoves);
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableDebugging() {
        GameSettings settings = GameSettings.getInstance();
        settings.setEnableDebugging(!settings.isEnableDebugging());
    }

    /**
     * Print the current board state as a FEN string
     */
    private void printFen() {
        Board board = getBoard();
        System.out.println(FenUtils.getFen(board));
        System.out.println(board.toString());
    }

    /**
     * Print the current game state in terms of pieces
     */
    private void printPieces() {
        Map<Player.Color, Player> players = getBoard().getPlayers();
        for(Player player : players.values()) {
            System.out.println(player + " pieces:");
            System.out.println("Controlled: " + player.getPieces());
            System.out.println("Captured: " + player.getCapturedPieces());
        }
    }

    /**
     * Print out all game moves available currently
     */
    private void printGameMoves() {
        System.out.println(getBoard().getGameState().getPlayerTurn().getMovesForPieces());
    }

    private Board getBoard() {
        ChessFrame frame = (ChessFrame) SwingUtilities.getRoot(this);
        return frame.getBoard();
    }
}
