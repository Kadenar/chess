package com.chess.engine.utils;

import com.chess.engine.board.*;
import com.chess.engine.pieces.Piece;

import java.util.Map;

public class FenUtils {

    // The default starting position for a chess game
    private static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /**
     * Loads the given fen string if valid
     * - Populates the board with the pieces for each player
     * - Sets whose turn it is
     * - Determines castling ability
     * - Gets enpassant square if any
     * - Sets half move counter
     * - Sets full move counter
     * @param fen a valid FEN string for a standard chess game
     */
    public static void loadFen(Board board, String fen) throws FenException {

        // If no FEN was provided, use the default starting position
        if ("".equals(fen)) {
            fen = FenUtils.DEFAULT_POSITION;
        }

        // Build the game board
        setGameState(board, fen);
    }

    public static String getFen(Board board) {
        StringBuilder sb = new StringBuilder();

        /*========== 1st field : pieces ==========*/
        int row = 7, col = 0;
        int blanks = 0;
        for(Map.Entry<String, Tile> entry : board.getTileMap().entrySet()) {
            Tile t = entry.getValue();
            if(!t.isOccupied()) {
                blanks++;
            } else {
                if(blanks > 0) {
                    sb.append(blanks); blanks = 0;
                }
                sb.append(t.getPiece().toString());
            }

            col++;
            if (col > 7) {
                if (blanks > 0) {
                    sb.append(blanks);
                }
                row--; col = 0; blanks = 0;
                if (row >= 0) sb.append('/');
            }
        }

        /*========== 2nd field : to play ==========*/
        GameState state = GameState.getInstance();
        sb.append(' ').append(state.getPlayerTurn().toString());

        /*========== 3rd field : castles ==========*/
        sb.append(' ');
        sb.append(state.getCastles()); // TODO needs to be more robust

        /*========== 4th field : ep square ==========*/
        sb.append(' ');
        Position epSq = state.getEPSquare();
        if (epSq == null)
            sb.append('-');
        else
            sb.append(epSq);

        /*========== 5th field : half move clock ==========*/
        sb.append(' ').append(state.getHalfMoves());

        /*========== 6th field : full move number ==========*/
        sb.append(' ').append(state.getFullMoves() / 2 + 1);

        return sb.toString();
    }

    /*
    * Sets the game state from fen string
    */
    private static void setGameState(Board board, String fen) throws FenException {
        String[] tokens = fen.split(" ");
        if(tokens.length != 6) throw new FenException("Invalid fen string");

        // Add game pieces
        buildBoardFromFEN(board, tokens[0]);

        // Set player's move
        GameState.getInstance().setPlayerTurn(getPlayerTurn(tokens[1]));

        // Set castling
        GameState.getInstance().setCastlingAbility(getCastlingAbility(tokens[2]));

        // Set enpassant TODO - This is a really ugly way to doing this...
        Tile t = board.getTileMap().getOrDefault(tokens[3], null);
        Position p = null;
        if(t != null) { p = t.getPosition(); }
        GameState.getInstance().setEnpassantSquare(p);

        // Set half move counter
        GameState.getInstance().setHalfMoves(getHalfMove(tokens[4]));

        // Set full move counter
        GameState.getInstance().setFullMoves(getFullMove(tokens[5]));
    }

    /**
     * Build board with pieces from fen
     * @param boardPositions the board position portion of the FEN string
     */
    private static void buildBoardFromFEN(Board board, final String boardPositions) throws FenException {

        // Split fen to create each rank
        String ranks[] = boardPositions.split("/");

        if(ranks.length != 8) throw new FenException("There should be exactly 8 cols.");

        // For each of the ranks in the fen string (should be 8)
        for(int rankCount = 0; rankCount < ranks.length; rankCount++) {

            // For each character in the rank
            char[] charArray = ranks[rankCount].toCharArray();
            int filesAddedForRow = 0;

            // For each character in the current rank
            for (char ch : charArray) {

                // Get the next character in the rank
                // If the character is a digit
                if (Character.isDigit(ch)) {

                    // Add an empty tile in the current rank denoted by digit in FEN
                    int numFiles = Integer.parseInt(ch + "");
                    int tempFile = filesAddedForRow;
                    for (int i = tempFile; i < (numFiles + tempFile); i++) {
                        Position pos = new Position(7-rankCount, i);
                        board.getTileMap().put(pos.toString(), new Tile.EmptyTile(pos));
                        filesAddedForRow++;
                    }
                }
                // If the character was a piece, then add it for white or black
                else {
                    Position piecePosition = new Position(7 - rankCount, filesAddedForRow);
                    Piece newPiece = BoardUtils.getInstance().constructPiece(ch, piecePosition);
                    board.getTileMap().put(piecePosition.toString(), new Tile.OccupiedTile(piecePosition, newPiece));
                    filesAddedForRow++;
                }
            }
        }
    }

    private static Player getPlayerTurn(final String turn) throws FenException {
        if(turn.equals(Player.WHITE.toString())) {
            return Player.WHITE;
        } else if(turn.equals(Player.BLACK.toString())) {
            return Player.BLACK;
        } else {
            throw new FenException("Malformed fen string: expected 'to play' as second field but found " + turn);
        }
    }

    private static String getCastlingAbility(String castles) {

        return castles;
    }

    private static int getHalfMove(String halfMove) {
        return Integer.valueOf(halfMove);
    }

    private static int getFullMove(String fullMoves) {
        return Integer.valueOf(fullMoves);
    }

    public static class FenException extends Exception {

        FenException(String message) {
            super(message);
        }
    }
}
