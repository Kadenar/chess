package com.chess.ui;

import com.chess.engine.board.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TileUI extends JPanel {

    TileUI(Tile tile) {
        setSize(20, 20);
        int boardIndex = (tile.getBoardIndex() + tile.getPosition().getRow()) % 2;
        setBackground( boardIndex == 0 ? Color.WHITE : Color.BLUE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        // If the tile is occupied, add the image to it
        if(tile.isOccupied()) {
            add(new PieceUI(tile.getPiece())).addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("clicked" + e.getLocationOnScreen());
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("pressed: " + e.getLocationOnScreen());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println("released: " + e.getLocationOnScreen());

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    System.out.println("entered: " + e.getLocationOnScreen());

                }

                @Override
                public void mouseExited(MouseEvent e) {
                    System.out.println("exited: " + e.getLocationOnScreen());
                }
            });
        }
    }
}
