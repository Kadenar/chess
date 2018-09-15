package com.chess.ui.menus;

import com.chess.engine.GameSettings;
import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.FenUtils;
import com.chess.engine.board.PGNUtils;
import com.chess.engine.moves.Move;
import com.chess.ui.ChessFrame;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.util.Set;

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
        enableDebugging.setState(GameSettings.INSTANCE.isEnableDebugging());
        enableDebugging.addItemListener(e -> enableDebugging());
        add(enableDebugging);

        // Print out current FEN
        JMenuItem printFen = new JMenuItem("Print Fen");
        printFen.addActionListener(e -> printFen());
        add(printFen);

        // Print PGN
        JMenuItem printPGN = new JMenuItem("Print PGN");
        printPGN.addActionListener(e -> printPGN());
        add(printPGN);

        // Parse PGN
        JMenuItem parsePGN = new JMenuItem("Parse PGN");
        parsePGN.addActionListener(e -> parsePGN());
        add(parsePGN);

        // Print all pieces state
        JMenuItem printPieces = new JMenuItem("Print pieces");
        printPieces.addActionListener(e -> printPieces());
        add(printPieces);

        JMenuItem printMoves = new JMenuItem("Print moves");
        printMoves.addActionListener(e -> printMoves());
        add(printMoves);

        // Print all moves
        JMenuItem printValidMoves = new JMenuItem("Print valid moves");
        printValidMoves.addActionListener(e -> printValidMoves());
        add(printValidMoves);
    }

    /**
     * Toggle whether to enable highlighting
     */
    private void enableDebugging() {
        GameSettings settings = GameSettings.INSTANCE;
        settings.setEnableDebugging(!settings.isEnableDebugging());
        getBoard().displayBoard();
    }

    /**
     * Print the current board state as a FEN string
     */
    private void printFen() {
        Board board = getBoard();
        System.out.println(FenUtils.getFen(board));
        System.out.println(board);
    }

    /**
     * Print out the PGN from move history
     */
    private void printPGN() {
        System.out.println(PGNUtils.getPGN(getBoard().getMoveHistory()));
    }

    private void parsePGN() {
        PGNUtils.parsePGN("pgn/sample.pgn");
    }

    /**
     * Print the current game state in terms of pieces
     */
    private void printPieces() {
        getBoard().getPlayers().values().forEach(player -> {
            System.out.println(player + " pieces:");
            System.out.println("Controlled: " + player.getPieces());
            System.out.println("Captured: " + player.getCapturedPieces());
        });
    }

    /**
     * Print out all game moves available currently (only those that are valid)
     */
    private void printMoves() {
        Board board = getBoard();
        int fullMoves = board.getGameState().getFullMoves();
        Player player = board.getGameState().getPlayerTurn();
        System.out.println("All moves for: " + player);
        player.getPieces().forEach(piece -> {
            Set<Move> movesForPiece = board.getMovesForPiece(fullMoves, piece);
            if(!movesForPiece.isEmpty()) {
                System.out.println(movesForPiece);
            }
        });
    }

    /**
     * Print out all game moves available currently (only those that are valid)
     */
    private void printValidMoves() {
        Board board = getBoard();
        int fullMoves = board.getGameState().getFullMoves();
        Player player = board.getGameState().getPlayerTurn();
        System.out.println("Valid moves for: " + player);
        player.getPieces().forEach(piece -> {
            Set<Move> movesForPiece = board.getValidMovesForPiece(fullMoves, piece);
            if(!movesForPiece.isEmpty()) {
                System.out.println(movesForPiece);
            }
        });
    }

    private Board getBoard() {
        ChessFrame frame = (ChessFrame) SwingUtilities.getRoot(this);
        return frame.getBoard();
    }
}
