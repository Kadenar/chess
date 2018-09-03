package com.chess.ui.panels;


import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.board.GameState;
import com.chess.engine.moves.Move;
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
import javax.swing.table.TableCellRenderer;
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
        model.addColumn("White");
        model.addColumn("Black");
        moveHistory = new JTable(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public boolean getRowSelectionAllowed() {
                return false;
            }
        };
        moveHistory.setCellSelectionEnabled(false);
        TableCellRenderer historyHeader = moveHistory.getTableHeader().getDefaultRenderer();
        ((DefaultTableCellRenderer)historyHeader).setHorizontalAlignment(JLabel.CENTER);
        moveHistory.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(moveHistory);

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

        // Update our captures
        Move latestMove = board.getMoveHistory().getLatestMove();
        if(latestMove != null) {

            // History is updated after the move is performed so if current player is white, then it was black who just moved
            if(currentState.getPlayerTurn().getColor().equals(Player.Color.WHITE)) {
                if(model.getRowCount() == 0) {
                    model.addRow(new Object[] {currentState.getFullMoves() + ".", "", board.getMoveHistory().getNotationEntry(latestMove)});
                } else {
                    model.setValueAt(board.getMoveHistory().getNotationEntry(latestMove), model.getRowCount() - 1, 2);
                }
            } else {
                model.addRow(new Object[] {currentState.getFullMoves() + ".", board.getMoveHistory().getNotationEntry(latestMove), ""});
            }

            Piece capturedPiece = latestMove.getCapturedPiece();
            if (capturedPiece != null) {
                captures.addCaptured(capturedPiece);
            }
        }

    }

    /**
     * Reset our move history
     */
    public void reset() {

        // Clean up moves
        // TODO -> Disable this to test PGN moves
        board.getMoveHistory().reset();

        // Remove all rows from move history
        DefaultTableModel model = (DefaultTableModel) moveHistory.getModel();
        for(int i = model.getRowCount()-1; i >= 0; i--) {
            model.removeRow(i);
        }

        // Clean up captured pieces
        captures.reset();
        captures.removeAll();
    }


}
