package com.chess.ui;

import com.chess.engine.board.Board;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Button;

public class ChessFrame extends JFrame {

    private final static int WINDOW_WIDTH = 600;
    private final static int WINDOW_HEIGHT = 600;

    private JPanel historyPanel; // TODO To be implemented further
    private BoardUI boardUI;

    public ChessFrame(Board board) {
        super("Chess");
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        getContentPane().setLayout(new BorderLayout());

        // Add all headers and panels to the frame
        addHeadersAndPanels();

        // Add the actual chessboard
        initChessBoard(board);

        this.setVisible(true);
    }

    private void addHeadersAndPanels() {
        // Add file and rank headers
        this.getContentPane().add(new FileHeaders(), BorderLayout.SOUTH);
        this.getContentPane().add(new CapturedPanel(), BorderLayout.WEST);

        // Add history panel
        this.historyPanel = new JPanel();
        this.historyPanel.setSize(20, 20);
        this.historyPanel.add(new JLabel("Moves"));
        this.getContentPane().add(this.historyPanel, BorderLayout.EAST);

        // Add new game button
        Button newGame = new Button("New game");
        this.getContentPane().add(newGame, BorderLayout.NORTH);
        //newGame.addActionListener(e -> resetGameState());

        //Button printFen = new Button("Print fen");
        //getContentPane().add(printFen, BorderLayout.NORTH);
        //printFen.addActionListener(e -> System.out.println(FenUtils.getFen(board)));
    }

    private void initChessBoard(Board board) {
        boardUI = new BoardUI(board);
    }
}
