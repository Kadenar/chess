package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.board.Board;
import com.chess.engine.Player;
import com.chess.engine.board.FenUtils;

import javax.swing.*;
import java.util.Map;

class DebugOptionsMenu extends JMenu {

    private Board board;

    DebugOptionsMenu(Board board) {
        super("Debug options");
        this.board = board;
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
        System.out.println(FenUtils.getFen(board));
        System.out.println(board.toString());
    }

    /**
     * Print the current game state in terms of pieces
     */
    private void printPieces() {
        Map<String, Player> players = board.getPlayers();
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
        System.out.println(board.getGameState().getPlayerTurn().isWhite() ? Player.WHITE.getAllValidMoves() : Player.BLACK.getAllValidMoves());
    }
}
