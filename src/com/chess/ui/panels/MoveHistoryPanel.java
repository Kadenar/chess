package com.chess.ui.panels;

import com.chess.engine.board.Board;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MoveHistoryPanel extends JPanel{

    private JTable moveHistory;
    private JScrollPane scrollPane;
    private final Board board;

    MoveHistoryPanel(Board board) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.board =  board;

        TitledBorder border = new TitledBorder("Moves");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        this.setBorder(border);

        addUndoRedoButtons();
        addMoveHistoryTable();
    }

    /**
     * Add Undo and Redo buttons
     */
    private void addUndoRedoButtons() {
        JPanel undoRedo = new JPanel();

        // TODO -> Still need to actually implement undo
        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> board.undo());
        undoRedo.add(undo);

        // TODO -> Still need to actually implement redo
        JButton redo = new JButton("Redo");
        redo.addActionListener(e -> board.redo());
        undoRedo.add(redo);

        this.add(undoRedo);
    }

    /**
     * Add move history table
     */
    private void addMoveHistoryTable() {
        // Set-up history text
        DefaultTableModel model = new DefaultTableModel();
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
        scrollPane = new JScrollPane(moveHistory);

        // Add header and content
        this.add(scrollPane);
    }

    /**
     * Get reference to move history table
     * @return the move history table that is updated on each move
     */
    JTable getMoveHistoryTable() {
        return this.moveHistory;
    }

    /**
     * Scroll to the bottom of the move history
     */
    void scrollToBottom() {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }
}
