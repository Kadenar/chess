package com.chess.ui.panels;

import com.chess.ChessConsts;
import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.moves.Move;
import com.chess.engine.pieces.Piece;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;

public class GameStatePanel extends JPanel {

    private final Board board;
    private final MoveHistoryPanel moveHistoryPanel;
    private final CapturedPiecesPanel capturedPiecesPanel;

    public GameStatePanel(Board board) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.board = board;
        this.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT));

        moveHistoryPanel = new MoveHistoryPanel(board);
        capturedPiecesPanel = new CapturedPiecesPanel();

        this.add(moveHistoryPanel);
        this.add(capturedPiecesPanel);
    }

    /**
     * Update the history whenever a move is performed
     */
    public void updateHistory() {
        GameState currentState = board.getGameState();
        DefaultTableModel model = (DefaultTableModel) moveHistoryPanel.getMoveHistoryTable().getModel();

        // Update our captures
        Move latestMove = board.getMoveHistory().getLatestMove();
        if(latestMove != null) {

            // History is updated after the move is performed so if current player is white, then it was black who just moved
            if(currentState.getPlayerTurn().getColor().equals(Player.Color.WHITE)) {
                int rowCount = model.getRowCount();
                if(rowCount == 0) {
                    model.addRow(new Object[] {currentState.getFullMoves() + ".", "", board.getMoveHistory().getNotationEntry(latestMove)});
                } else {
                    model.setValueAt(board.getMoveHistory().getNotationEntry(latestMove), rowCount - 1, 2);
                }
            } else {
                model.addRow(new Object[] {currentState.getFullMoves() + ".", board.getMoveHistory().getNotationEntry(latestMove), ""});
            }

            Piece capturedPiece = latestMove.getCapturedPiece();
            if (capturedPiece != null) {
                capturedPiecesPanel.addCaptured(capturedPiece);
            }
        }

        // Scroll to bottom of move history
        moveHistoryPanel.scrollToBottom();
    }

    /**
     * Reset our move history
     */
    public void reset() {

        // Clean up moves
        // TODO -> Disable this to test PGN moves
        board.getMoveHistory().reset();

        // Remove all rows from move history
        DefaultTableModel model = (DefaultTableModel) moveHistoryPanel.getMoveHistoryTable().getModel();
        for(int i = model.getRowCount()-1; i >= 0; i--) {
            model.removeRow(i);
        }

        // Clean up captured pieces
        capturedPiecesPanel.reset();
    }
}
