package com.chess.engine.moves;

import com.chess.engine.Player;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class PromotionSelection extends JOptionPane {

    private PromotionSelectionTile selection = null;

    /**
     * Add promotion selections and allow user to pick piece to select to
     * @param board the board to display selections for
     * @param currentPlayer the {@code Player} who is promoting
     * @return the {@code Piece} selected to promote to
     */
    Piece displaySelections(Board board, Player currentPlayer) {
        Queen queen = new Queen(currentPlayer);
        PromotionSelectionTile queenTile = createPanelWithPiece(queen);
        selection = queenTile;
        selection.toggle(true);
        Rook rook = new Rook(currentPlayer);
        PromotionSelectionTile rookTile = createPanelWithPiece(rook);
        Knight knight = new Knight(currentPlayer);
        PromotionSelectionTile knightTile = createPanelWithPiece(knight);
        Bishop bishop = new Bishop(currentPlayer);
        PromotionSelectionTile bishopTile = createPanelWithPiece(bishop);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Promote to:"));
        panel.add(queenTile);
        panel.add(rookTile);
        panel.add(knightTile);
        panel.add(bishopTile);

        String[] options = {"Yes"};
        showOptionDialog(board, panel, "Promoting pawn",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        return selection.getPiece();
    }

    /**
     * Add a promotion selection tile with given piece
     * @param piece the {@code Piece} to display
     * @return the {@code PromotionSelectionTile} that can be clicked to toggle
     */
    private PromotionSelectionTile createPanelWithPiece(Piece piece) {
        PromotionSelectionTile promotionTile = new PromotionSelectionTile(piece);
        promotionTile.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Remove selection toggle from previous highlighted
                if(selection != null) {
                    selection.toggle(false);
                }

                // Update selection and toggle it
                selection = promotionTile;
                selection.toggle(true);
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

        return promotionTile;
    }
}
