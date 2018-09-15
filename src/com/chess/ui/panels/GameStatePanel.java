package com.chess.ui.panels;

import com.chess.ChessConsts;
import com.chess.engine.board.Board;
import com.chess.engine.moves.MoveHistory;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Dimension;

public class GameStatePanel extends JPanel {

    private final Board board;
    private final MoveHistory moveHistoryPanel;
    private final CapturedPiecesPanel capturedPiecesPanel;

    public GameStatePanel(Board board) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.board = board;
        this.setPreferredSize(new Dimension(ChessConsts.HISTORY_WIDTH, ChessConsts.HISTORY_HEIGHT));

        moveHistoryPanel = board.getMoveHistory();
        capturedPiecesPanel = new CapturedPiecesPanel();

        this.add(moveHistoryPanel);
        this.add(capturedPiecesPanel);
    }


}
