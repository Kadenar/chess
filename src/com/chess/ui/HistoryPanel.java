package com.chess.ui;


import com.chess.engine.utils.FenUtils;

import javax.swing.*;
import java.awt.*;

class HistoryPanel extends JPanel{

    private JPanel movesPanel;
    private JTextArea moveHistory;

    HistoryPanel() {
        super(new GridLayout(2, 1));
        movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
        moveHistory = new JTextArea();
        moveHistory.setEditable(false);
        moveHistory.setText(FenUtils.DEFAULT_POSITION);
        moveHistory.setLineWrap(true);
        //this.setSize(20, 20);
        movesPanel.add(new JLabel("Moves"));
        movesPanel.add(moveHistory);

        this.add(movesPanel, 0);
        this.add(new CapturedPanel(), 1);
    }
}
