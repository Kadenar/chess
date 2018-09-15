package com.chess.ui.panels;

import com.chess.ChessConsts;
import com.chess.engine.board.Board;
import com.chess.ui.headers.FileHeaders;
import com.chess.ui.headers.RankHeaders;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class BoardPanel extends JPanel {

    public BoardPanel(Board board) {
        super(new BorderLayout());
        this.setPreferredSize(new Dimension(ChessConsts.BOARD_WIDTH, ChessConsts.BOARD_HEIGHT));
        this.add(board.getLayeredPane(), BorderLayout.CENTER);
        this.add(new RankHeaders(), BorderLayout.WEST);
        this.add(new FileHeaders(), BorderLayout.SOUTH);
    }
}
