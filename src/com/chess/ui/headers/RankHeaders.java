package com.chess.ui.headers;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;

public class RankHeaders extends JPanel {

    private static final String[] headers = {"8", "7", "6", "5", "4", "3", "2", "1" };

    public RankHeaders() {
        setLayout(new GridLayout(8, 1));
        for(String h : headers) {
            JLabel header = new JLabel();
            header.setText(h);
            header.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(header);
        }
    }
}
