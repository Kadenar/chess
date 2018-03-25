package com.chess.ui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class BoardGUI extends JFrame {

    private JPanel boardTiles;
    private JScrollPane historyPanel;
    private Board board;
    private final static int WINDOW_WIDTH = 600;
    private final static int WINDOW_HEIGHT = 600;
    private List<TileUI> allTiles;

    public BoardGUI(Board board) {
        super("Kadenar Chess");
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.board = board;
        this.allTiles = new ArrayList<>();
        this.initializeBoard();
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // TODO - Allow for resizing later after mouse positioning is figured out
        this.setResizable(false);
        this.setVisible(true);
    }

    private void initializeBoard() {
        JPanel mainBoard = new JPanel();
        mainBoard.setLayout(new BorderLayout());

        // Add file and rank headers
        mainBoard.add(new FileHeaders(), BorderLayout.SOUTH);
        mainBoard.add(new RankHeaders(), BorderLayout.WEST);

        // Add history panel
        historyPanel = new JScrollPane();
        historyPanel.setSize(20, 20);
        historyPanel.add(new JLabel("Moves"));
        mainBoard.add(historyPanel, BorderLayout.EAST);

        // Create the chess tiles
        boardTiles = new JPanel();
        boardTiles.addMouseListener(new PieceListener(this));
        boardTiles.setLayout(new GridLayout(8,8));

        // Add all the chess tiles to the UI
        List<Tile> tiles = board.getTiles();
        for(int i = 0; i < tiles.size(); i++) {
            TileUI t = new TileUI(tiles.get(i));
            boardTiles.add(t, i);
            this.allTiles.add(t);
        }

        // Add the individual tiles to the board in the center
        mainBoard.add(boardTiles, BorderLayout.CENTER);

        // add the chess board to the frame
        this.add(mainBoard);
    }

    public void movePieceToNewTile(TileUI originatingTile) {
        int mouseX = MouseInfo.getPointerInfo().getLocation().x;
        int mouseY = MouseInfo.getPointerInfo().getLocation().y;
        int tile_x_pos = mouseX / (73);
        int tile_y_pos = mouseY / (70);
        System.out.println("Dragged to position: [" + tile_x_pos + ", " + tile_y_pos + "]");

        Position draggedToPosition = new Position(tile_x_pos, tile_y_pos);
        TileUI draggedToTile = null;
        for (TileUI t : allTiles) {
            Position foundPos = t.getTile().getPosition();
            if (draggedToPosition.equals(foundPos)) {
                draggedToTile = t;
                break;
            }
        }

        if(draggedToTile == null || draggedToTile == originatingTile) {
            return;
        }

        draggedToTile.addPieceToTile(originatingTile.getPieceUI(), false);
        repaint();
    }

    static class PieceListener implements MouseListener {
        private BoardGUI gui;
        private TileUI originatingTile = null;

        PieceListener(BoardGUI gui) {
            this.gui = gui;
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            int mouseX = MouseInfo.getPointerInfo().getLocation().x;
            int mouseY = MouseInfo.getPointerInfo().getLocation().y;
            int tile_x_pos = mouseX / (73);
            int tile_y_pos = mouseY / (70);
            System.out.println("Originating position: [" + tile_x_pos + ", " + tile_y_pos + "]");
            Position originatingPosition = new Position(tile_x_pos, tile_y_pos);
            for(TileUI t : gui.allTiles) {
                if(originatingPosition.equals(t.getTile().getPosition())) {
                    originatingTile = t;
                }
            }
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
