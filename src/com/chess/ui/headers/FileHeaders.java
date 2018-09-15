package com.chess.ui.headers;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;

public class FileHeaders extends JPanel{
    private static final String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h" };

    public FileHeaders() {
        setLayout(new GridLayout(1, 8));

        for(String h : headers) {
            JLabel header = new JLabel();
            //header.setPreferredSize(new Dimension(ChessConsts.FILE_WIDTH, ChessConsts.FILE_HEIGHT));
            header.setText(h);
            header.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(header);
        }
    }
}
