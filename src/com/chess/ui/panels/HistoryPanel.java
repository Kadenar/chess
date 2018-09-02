package com.chess.ui.panels;


import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.pieces.Piece;
import com.chess.ui.UIConstants;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridLayout;

public class HistoryPanel extends JPanel{

    private JTable moveHistory;
    private CapturedPanel captures;
    private final Board board;

    public HistoryPanel(Board board) {
        super(new GridLayout(2,1));
        this.board = board;
        this.captures = new CapturedPanel();
        setPreferredSize(new Dimension(UIConstants.HISTORY_WIDTH, UIConstants.HISTORY_HEIGHT));
        JPanel movesPanel = new JPanel();
        TitledBorder border = new TitledBorder("Moves");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        movesPanel.setBorder(border);
        movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));

        // Set-up history text
        DefaultTableModel model= new DefaultTableModel();
        model.addColumn("#");
        model.addColumn("W");
        model.addColumn(("B"));
        moveHistory = new JTable(model);
        ((DefaultTableCellRenderer)moveHistory.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(moveHistory);
        //scrollPane.setPreferredSize(new Dimension(85, 40));

        // Add header and content
        movesPanel.add(scrollPane);
        this.add(movesPanel, 0);
        this.add(captures, 1);
    }

    /**
     * Update the history whenever a move is performed
     */
    public void updateHistory() {
        GameState currentState = board.getGameState();
        DefaultTableModel model = (DefaultTableModel) moveHistory.getModel();

        // History is updated after the move is performed
        if(currentState.getPlayerTurn().getColor().equals(Player.Color.WHITE)) {
            /*int rowCount = model.getRowCount();
            String whitesLastMove = "";
            if(rowCount > 0) {
                whitesLastMove = (String) model.getValueAt(model.getRowCount(), 1);
                model.removeRow(model.getRowCount());
            }*/
            // TODO fix this eventually
            model.addRow(new Object[] {currentState.getFullMoves(), "", board.getMoveHistory().getLatestMove()});
        } else {
            model.addRow(new Object[] {currentState.getFullMoves(), board.getMoveHistory().getLatestMove(), ""});
        }

        // Update our captures
        Piece capturedPiece = board.getMoveHistory().getLatestMove().getCapturedPiece();
        if(capturedPiece != null) {
            captures.addCaptured(capturedPiece);
        }
    }
}
