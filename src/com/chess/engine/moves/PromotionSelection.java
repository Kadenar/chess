package com.chess.engine.moves;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class PromotionSelection {

    static Piece displaySelection(Board board, Player currentPlayer) {
        Queen queen = new Queen(currentPlayer);
        queen.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked (MouseEvent e){
                System.out.println("Clicked queen");
            }

            @Override
            public void mousePressed (MouseEvent e){ }

            @Override
            public void mouseReleased (MouseEvent e){ }

            @Override
            public void mouseEntered (MouseEvent e){ }

            @Override
            public void mouseExited (MouseEvent e){ }
        });

        Knight knight = new Knight(currentPlayer);
        knight.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked (MouseEvent e){
                System.out.println("Clicked knight");
            }

            @Override
            public void mousePressed (MouseEvent e){ }

            @Override
            public void mouseReleased (MouseEvent e){ }

            @Override
            public void mouseEntered (MouseEvent e){ }

            @Override
            public void mouseExited (MouseEvent e){ }
        });

        Rook rook = new Rook(currentPlayer);
        rook.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked (MouseEvent e){
                System.out.println("Clicked rook");
            }

            @Override
            public void mousePressed (MouseEvent e){ }

            @Override
            public void mouseReleased (MouseEvent e){ }

            @Override
            public void mouseEntered (MouseEvent e){ }

            @Override
            public void mouseExited (MouseEvent e){ }
        });

        Bishop bishop = new Bishop(currentPlayer);
        bishop.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked (MouseEvent e){
                System.out.println("Clicked bishop");
            }

            @Override
            public void mousePressed (MouseEvent e){ }

            @Override
            public void mouseReleased (MouseEvent e){ }

            @Override
            public void mouseEntered (MouseEvent e){ }

            @Override
            public void mouseExited (MouseEvent e){ }
        });

        Piece[] options = {queen, rook, knight, bishop};
        int value = JOptionPane.showOptionDialog(board, "Promote your pawn to which piece?", "Promotion!",
                JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        Piece pickedPiece;
        if(value == 0) {
            pickedPiece = queen;
        } else if(value == 1) {
            pickedPiece = rook;
        } else if(value == 2) {
            pickedPiece = knight;
        } else {
            pickedPiece = bishop;
        }

        return pickedPiece;
    }
}
