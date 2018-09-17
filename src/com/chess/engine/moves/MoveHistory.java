package com.chess.engine.moves;

import com.chess.ChessConsts;
import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.GameState;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MoveHistory extends JPanel {

    // The board move history is for
    private final Board board;

    // Last move that was performed
    private Move lastMove = null;

    // All moves that have been performed
    private final List<Move> allMoves;

    // Move history table / scrolling
    private final JTable moveHistory = new JTable(new DefaultTableModel()) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }

        @Override
        public boolean getRowSelectionAllowed() { return false; }
    };
    private final JScrollPane scrollPane = new JScrollPane(moveHistory);

    // Captured pieces
    private final JPanel blackPieces = new JPanel();
    private final JPanel whitePieces = new JPanel();

    /**
     * Creates a new instance for a given board
     * @param board the {@code Board} this move history represents
     */
    public MoveHistory(Board board) {
        super();

        // Backend information
        this.board = board;
        this.allMoves = new ArrayList<>();

        // UI components for Move History
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT));
        addMoveHistoryPanel();
        addCapturedPiecesPanel();
    }

    /**
     * Create move history instance with same board, and moves as the other
     * @param other the {@code MoveHistory} to copy
     */
    public MoveHistory(MoveHistory other) {
        this.board = other.board;
        this.lastMove = other.lastMove;
        this.allMoves = other.allMoves;
    }

    public Move getLastMove() {
        return this.lastMove;
    }
    /**
     * Get a list of moves based on filter condition
     * @return get a {@code Stream<Move>} based on filtering condition
     */
    public Stream<Move> getMoves(Predicate<Move> filter) { return this.allMoves.stream().filter(filter); }

    /**
     * Add undo / redo to history panel
     * Add move history table to panel
     * Add panel to main panel
     */
    private void addMoveHistoryPanel() {

        // Set border for move history
        JPanel moveHistoryPanel = new JPanel();
        moveHistoryPanel.setLayout(new BoxLayout(moveHistoryPanel, BoxLayout.Y_AXIS));
        TitledBorder border = new TitledBorder("Moves");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        moveHistoryPanel.setBorder(border);

        // Add undo / redo buttons
        addUndoRedoButtons(moveHistoryPanel);

        // Add move history
        addMoveHistoryTable(moveHistoryPanel);

        // Add move history to main panel
        this.add(moveHistoryPanel);
    }
    /**
     * Add Undo and Redo buttons
     */
    private void addUndoRedoButtons(JPanel moveHistoryPanel) {
        JPanel undoRedo = new JPanel();

        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> board.undo());
        undoRedo.add(undo);

        JButton redo = new JButton("Redo");
        redo.addActionListener(e -> board.redo());
        undoRedo.add(redo);

        // Add buttons to move history panel
        moveHistoryPanel.add(undoRedo);
    }

    /**
     * Add move history table
     */
    private void addMoveHistoryTable(JPanel moveHistoryPanel) {
        // Set-up history text
        DefaultTableModel model = (DefaultTableModel) this.moveHistory.getModel();
        model.addColumn("#");
        model.addColumn("White");
        model.addColumn("Black");
        moveHistory.setCellSelectionEnabled(false);
        TableCellRenderer historyHeader = moveHistory.getTableHeader().getDefaultRenderer();
        ((DefaultTableCellRenderer)historyHeader).setHorizontalAlignment(JLabel.CENTER);
        moveHistory.getTableHeader().setReorderingAllowed(false);

        // Add header and content
        moveHistoryPanel.add(scrollPane);
    }

    /**
     * Add captured pieces panel
     */
    private void addCapturedPiecesPanel() {
        // Setup captured pieces panel
        JPanel capturedPiecesPanel = new JPanel();
        capturedPiecesPanel.setLayout(new BoxLayout(capturedPiecesPanel, BoxLayout.Y_AXIS));
        TitledBorder border = new TitledBorder("Captured");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        capturedPiecesPanel.setBorder(border);

        // Add a separator
        JSeparator separate = new JSeparator(SwingConstants.HORIZONTAL);

        //Add white and black pieces to captured panel
        whitePieces.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT / 8));
        blackPieces.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT / 8));
        capturedPiecesPanel.add(blackPieces);
        capturedPiecesPanel.add(separate);
        capturedPiecesPanel.add(whitePieces);

        // Add captured panel to main panel
        this.add(capturedPiecesPanel);
    }

    /**
     * Scroll to the bottom of the move history
     */
    private void scrollToBottom() {
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

    /**
     * Update the history whenever a move is performed
     */
    public void update(Move latestMove) {
        GameState currentState = board.getGameState();
        DefaultTableModel model = (DefaultTableModel) this.moveHistory.getModel();

        // Check that we have a move
        if(latestMove == null) {
            return;
        }

        // Add the latest move to our move history and set latest move
        this.lastMove = latestMove;
        this.allMoves.add(latestMove);

        // History is updated after the move is performed so if current player is white, then it was black who just moved
        if(currentState.getPlayerTurn().getColor().equals(Player.Color.WHITE)) {
            int rowCount = model.getRowCount();
            if(rowCount == 0) {
                model.addRow(new Object[] {currentState.getFullMoves() + ".", "", getNotationEntry(latestMove)});
            } else {
                model.setValueAt(getNotationEntry(latestMove), rowCount - 1, 2);
            }
        } else {
            model.addRow(new Object[] {currentState.getFullMoves() + ".", getNotationEntry(latestMove), ""});
        }

        // Scroll to bottom of move history
        scrollToBottom();

        // If the latest move was a capture, then add the piece to panel
        Piece capturedPiece = latestMove.getCapturedPiece();
        if (capturedPiece != null) {
            JLabel scaledImg = capturedPiece.getScaledImg();
            if(capturedPiece.getOwner().isWhite()) {
                blackPieces.add(scaledImg);
            } else {
                whitePieces.add(scaledImg);
            }
        }
    }

    /**
     * Reset our move history / captured pieces
     */
    public void reset() {

        // Clean up moves
        this.allMoves.clear();
        this.lastMove = null;

        // Remove all rows from move history
        DefaultTableModel model = (DefaultTableModel) this.moveHistory.getModel();
        for(int i = model.getRowCount()-1; i >= 0; i--) {
            model.removeRow(i);
        }

        // Clean up captured pieces
        blackPieces.removeAll();
        whitePieces.removeAll();

        // Need to repaint so that pieces disappear
        revalidate();
        repaint();
    }

    /**
     * Remove the latest move
     */
    public void undo() {
        allMoves.remove(this.lastMove);
        // TODO
    }

    /**
     * Redo the most recent move
     */
    public void redo() {
        // TODO
    }

    /**
     * Get notation of the latest move
     * @return the {@code String} notation entry for history
     */
    public String getNotationEntry(Move move) {

        if(move == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        Piece movedPiece = move.getMovedPiece();
        Piece takenPiece = move.getCapturedPiece();
        Tile movedFromTile = move.getOrigin();
        Position movedFrom = movedFromTile.getPosition();
        Tile movedToTile = move.getDestination();
        Position movedTo = movedToTile.getPosition();

        // If last move was a pawn move
        if(movedPiece instanceof Pawn) {
            // If the pawn took a piece, append just the column the pawn came from
            if(takenPiece != null) {
                builder.append(movedFrom.toString(), 0, 1);
                builder.append("x");
            }
            builder.append(movedTo);
        }
        // If last move was a king castle
        else if(movedPiece instanceof King && BoardUtils.deltaCol(movedFromTile, movedToTile) > 1) {
            if(movedTo.getColumn() > movedFrom.getColumn()) {
                builder.append("O-O");
            } else {
                builder.append("O-O-O");
            }
        }
        // If last move was not a pawn move or king castling
        else {
            builder.append(movedPiece.toString().toUpperCase());

            if(takenPiece != null) {
                builder.append("x");
            }
            builder.append(movedTo);
        }

        // TODO -> Allow more than just queen promotion
        if(move.isPromotion()) {
            builder.append("Q");
        }

        Player movingPlayer = movedPiece.getOwner();
        if(MoveUtils.isKingInCheck(board, movingPlayer, movingPlayer.opposite(board)) != null) {
            builder.append("+");
        }

        return builder.toString();
    }
}
