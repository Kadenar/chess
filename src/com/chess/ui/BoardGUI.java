package com.chess.ui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.utils.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoardGUI extends JFrame {

    private JPanel boardTiles, mainBoard;
    private JPanel historyPanel;
    private Board gameBoard;
    private final static int WINDOW_WIDTH = 600;
    private final static int WINDOW_HEIGHT = 600;
    private Map<String, TileUI> uiTiles;

    public BoardGUI(Board board) {
        super("Kadenar Chess");
        JFrame.setDefaultLookAndFeelDecorated(true);

        // add the ui components
        addUIComponents(board);

        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setVisible(true);
    }

    public Board getBoard() {
        return this.gameBoard;
    }

    private void addUIComponents(Board board) {
        this.mainBoard = new JPanel();
        this.mainBoard.setLayout(new BorderLayout());

        // Add new game button
        Button newGame = new Button("New game");
        newGame.addActionListener(e -> {
            // Recreate the board object with default position
            BoardUtils.getInstance().updateBoardWithFen(board, "");

            // Remove all objects from the frame
            this.removeAll();

            // Re-add all UI headers etc
            addUIComponents(board);

            System.out.println(board.toString());

            repaint();
            revalidate();
        });

        this.mainBoard.add(newGame, BorderLayout.NORTH);

        // Add file and rank headers
        this.mainBoard.add(new FileHeaders(), BorderLayout.SOUTH);
        this.mainBoard.add(new RankHeaders(), BorderLayout.WEST);

        // Add history panel
        this.historyPanel = new JPanel();
        this.historyPanel.setSize(20, 20);
        this.historyPanel.add(new JLabel("Moves"));
        this.mainBoard.add(historyPanel, BorderLayout.EAST);

        // Draw the board pieces
        this.initializeBoard(board);
    }

    private void initializeBoard(Board board) {
        this.gameBoard = board;
        this.uiTiles = new LinkedHashMap<>();

        // Create the chess tiles
        this.boardTiles = new JPanel();
        this.boardTiles.addMouseListener(new PieceListener(this));
        this.boardTiles.setLayout(new GridLayout(8,8));

        // Add all the chess tiles to the UI
        int i = 0;
        for (Map.Entry<String, Tile> entry : board.getTileMap().entrySet()) {
            TileUI uiTile = new TileUI(entry.getValue());
            this.boardTiles.add(uiTile, i);
            this.uiTiles.put(entry.getKey(), uiTile);
            i++;
        }

        // Add the individual tiles to the board in the center
        this.mainBoard.add(boardTiles, BorderLayout.CENTER);

        // add the chess board to the frame
        this.add(mainBoard);
    }

    /*
    * Mouse coordinates are the inverse of the tile coordinates
    * So we need to take the absolute value of 8 - mouse coordinate
    */
    private Point getTilePosition() {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        int tile_x_pos = (mouse.x / (this.boardTiles.getWidth() / 8) - 1);
        int tile_y_pos = mouse.y / (this.boardTiles.getHeight() / 8);
        tile_y_pos = Math.abs(8 - tile_y_pos);
        return new Point(tile_x_pos, tile_y_pos);
    }

    private void movePieceToNewTile(TileUI originatingTile) {
        // Originating tile piece
        if(originatingTile == null || originatingTile.getPieceUI() == null) return;

        Position draggedToPosition = new Position(getTilePosition());
        TileUI draggedToTile = this.uiTiles.getOrDefault(draggedToPosition.toString(), null);

        // If the dragged to tile is null or the same, just exit
        if(draggedToTile == null || draggedToTile == originatingTile) return;

        // Otherwise, move the piece on the originating tile to the new tile
        draggedToTile.movePieceToTile(originatingTile);

        repaint();
    }

    class PieceListener implements MouseListener {
        private BoardGUI gui;
        private TileUI originatingTile = null;

        PieceListener(BoardGUI gui) {
            this.gui = gui;
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            originatingTile = gui.uiTiles.getOrDefault(new Position(gui.getTilePosition()).toString(), null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            gui.movePieceToNewTile(originatingTile);
            originatingTile = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }
}
