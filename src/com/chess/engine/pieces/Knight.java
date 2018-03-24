package com.chess.engine.pieces;

import com.chess.engine.board.Player;
import com.chess.engine.board.Position;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    //private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17 };

    public Knight(Player color, Position position) {
        super(color, position);
    }

    @Override
    public String getPieceImagePath() {
        return "images/" + getOwner().toString() + "knight.png";
    }

    @Override
    public List<Position> createPossibleMoves() {
        Position currentPos = getPosition();
        List<Position> validPositions = new ArrayList<>();
        List<Position> potentialPositions = new ArrayList<>();

        // Check positions up 2 and over 1
        potentialPositions.add(new Position(currentPos.getRow() + 1, currentPos.getColumn() - 2));
        potentialPositions.add(new Position(currentPos.getRow() + 1, currentPos.getColumn() + 2));

        // Check positions up 1 and over 2
        potentialPositions.add(new Position(currentPos.getRow()+2, currentPos.getColumn() -1));
        potentialPositions.add(new Position(currentPos.getRow()+2, currentPos.getColumn()+1));

        // Check positions down 2 and over 1
        potentialPositions.add(new Position(currentPos.getRow()-2, currentPos.getColumn() -1));
        potentialPositions.add(new Position(currentPos.getRow()-2, currentPos.getColumn()+1));

        // Check positions down 1 and over 2
        potentialPositions.add(new Position(currentPos.getRow()-1, currentPos.getColumn()+2));
        potentialPositions.add(new Position(currentPos.getRow()-1, currentPos.getColumn()-2));

        // Loop through potential positions and check validity
        for(Position pos : potentialPositions) {
            if(pos.isValidCoord()) {
                validPositions.add(pos);
            }
        }

        // Return valid positions that the knight can move to
        return validPositions;
    }

    @Override
    public String toString() {
        return getOwner().isWhite() ? "N" : "n";
    }
}
