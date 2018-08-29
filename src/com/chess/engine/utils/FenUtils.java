package com.chess.engine.utils;

import com.chess.engine.board.*;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.Map;

public class FenUtils {

    // The default starting position for a chess game
    public static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

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

    /**
     * Retrieve the fen string based on the current board
     * @param board the board to parse
     * @return the fen string representation of the current board state
     */
    public static String getFen(final Board board) {
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
        sb.append(state.getCastles());

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
    private static void setGameState(Board board, final String fen) throws FenException {
        String[] tokens = fen.split(" ");
        if(tokens.length != 6) throw new FenException("Invalid fen string");

        // Add game pieces
        buildBoardFromFEN(board, tokens[0]);

        GameState gameState = GameState.getInstance();

        // Set player's move
        gameState.setPlayerTurn(getPlayerTurn(tokens[1]));

        // Set castling
        gameState.setCastlingAbility(getCastlingAbility(tokens[2]));

        // Set enpassant
        Position epSq = BoardUtils.sqiToPosition(tokens[3]);
        gameState.setEnpassantSquare(epSq);

        // Set half move counter
        gameState.setHalfMoves(getHalfMove(tokens[4]));

        // Set full move counter
        gameState.setFullMoves(getFullMove(tokens[5]));
    }

    /**
     * Build board with pieces from fen
     * @param boardPositions the board position portion of the FEN string
     */
    private static void buildBoardFromFEN(final Board board, final String boardPositions) throws FenException {

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
                        Position emptyTilePos = new Position(7 - rankCount, i);
                        board.getTileMap().put(emptyTilePos.toString(), new Tile(emptyTilePos));
                        filesAddedForRow++;
                    }
                }
                // If the character was a piece, then add it for white or black
                else {
                    Position piecePosition = new Position(7 - rankCount, filesAddedForRow);
                    Piece newPiece = BoardUtils.constructPiece(ch);
                    // Need to know the king's position
                    if(ch == 'k' || ch == 'K') {
                        GameState.getInstance().setKingPosition((King) newPiece, piecePosition);
                    }
                    board.getTileMap().put(piecePosition.toString(), new Tile(piecePosition, newPiece));
                    filesAddedForRow++;
                }
            }
        }
    }

    // Whose turn it is White or Black
    private static Player getPlayerTurn(final String turn) throws FenException {
        if(Player.WHITE.toString().equals(turn)) {
            return Player.WHITE;
        } else if(Player.BLACK.toString().equals(turn)) {
            return Player.BLACK;
        } else {
            throw new FenException("Malformed fen string: expected 'to play' as second field but found " + turn);
        }
    }

    // Whether castling is allowed for a player
    private static String getCastlingAbility(String castles) {
        return castles;
    }

    // Number of moves without a capture
    private static int getHalfMove(String halfMove) {
        return Integer.valueOf(halfMove);
    }

    // Number of moves in this game
    private static int getFullMove(String fullMoves) {
        return Integer.valueOf(fullMoves);
    }

    static class FenException extends Exception {
        FenException(String message) {
            super(message);
        }
    }
}
