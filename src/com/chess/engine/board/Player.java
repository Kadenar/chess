package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public enum Player {
    WHITE,
    BLACK;

    private List<Piece> pieces = new ArrayList<>();

    public void addPiece(Piece p) {
        pieces.add(p);
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean isWhite() {
        return this == WHITE;
    }

    public boolean isBlack() {
        return this == BLACK;
    }

    public String toString() {
        return isWhite() ? "w" : "b";
    }
}
