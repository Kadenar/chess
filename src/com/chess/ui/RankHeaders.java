package com.chess.ui;

import javax.swing.*;
import java.awt.*;

class RankHeaders extends JPanel {

    private static final String[] headers = {"8", "7", "6", "5", "4", "3", "2", "1" };

    RankHeaders() {
        setLayout(new GridLayout(8, 1));
        setSize(20, 20);
        for(String h : headers) {
            JLabel header = new JLabel();
            header.setText(h);
            header.setHorizontalAlignment(SwingConstants.CENTER);
            add(header);
        }
    }
}
