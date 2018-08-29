package com.chess.ui.menus;

import com.chess.engine.BoardMoves;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Player;
import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.Map;

class DebugOptionsMenu extends JMenu {

    DebugOptionsMenu() {
        super("Debug options");
        populateMenu();
    }

    /**
     * Adds standard debug options
     */
    private void populateMenu() {
        // Print current FEN
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
     * Print the current board state as a FEN string
     */
    private void printFen() {
        Board board = BoardUtils.getInstance().getBoard();
        System.out.println(FenUtils.getFen(board));
        System.out.println(board.toString());
    }

    /**
     * Print the current game state in terms of pieces
     */
    private void printPieces() {
        Map<String, Player> players = BoardUtils.getInstance().getBoard().getPlayers();
        for(Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            System.out.println(player + " pieces:");
            System.out.println("Controlled: " + player.getPieces());
            System.out.println("Captured: " + player.getCapturedPieces());
        }
    }

    /**
     * Print out all game moves available currently
     */
    private void printGameMoves() {
        System.out.println(GameState.getInstance().getPlayerTurn().isWhite() ? BoardMoves.getInstance().getWhiteValidMoves() : BoardMoves.getInstance().getBlackValidMoves());
    }
}
