package com.chess.ui.headers;

import javax.swing.*;
import java.awt.*;

public class RankHeaders extends JPanel {

    private static final String[] headers = {"8", "7", "6", "5", "4", "3", "2", "1" };

    public RankHeaders() {
        setLayout(new GridLayout(8, 1));
        for(String h : headers) {
            JLabel header = new JLabel();
            header.setText(h);
            header.setHorizontalAlignment(SwingConstants.CENTER);
            add(header);
        }
    }
}
