package com.chess.engine.board;

import com.chess.engine.Player;
import com.chess.engine.Position;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

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
    static void loadFen(Board board, String fen) throws FenException {

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
        final int[] row = {7};
        final int[] col = {0};
        final int[] blanks = {0};
        board.getTileMap().values().forEach( tile -> {
            if(!tile.isOccupied()) {
                blanks[0]++;
            } else {
                if(blanks[0] > 0) {
                    sb.append(blanks[0]); blanks[0] = 0;
                }
                sb.append(tile.getPiece().toString());
            }

            col[0]++;
            if (col[0] > 7) {
                if (blanks[0] > 0) {
                    sb.append(blanks[0]);
                }
                row[0]--; col[0] = 0; blanks[0] = 0;
                if (row[0] >= 0) sb.append('/');
            }
        });

        /*========== 2nd field : to play ==========*/
        GameState state = board.getGameState();
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

    /**
     * Set the game state for given board from fen string
     * @param board the board to update with fen string
     * @param fen the fen string to parse
     * @throws FenException appropriate exception if parsing failed
     */
    private static void setGameState(Board board, final String fen) throws FenException {
        String[] tokens = fen.split(" ");
        if(tokens.length != 6) throw new FenException("Invalid fen string");

        // Add game pieces
        buildBoardFromFEN(board, tokens[0]);

        GameState gameState = board.getGameState();

        // Set player's move
        gameState.setPlayerTurn(getPlayerTurn(board, tokens[1]));

        // Set castling
        gameState.setCastlingAbility(getCastlingAbility(tokens[2]));

        // Set enpassant
        gameState.setEnpassantSquare(BoardUtils.sqiToPosition(tokens[3]));

        // Set half move counter
        gameState.setHalfMoves(getHalfMove(tokens[4]));

        // Set full move counter
        gameState.setFullMoves(getFullMove(tokens[5]));

        // Populate moves for current game state
        board.getPlayers().values().forEach(player -> player.populateMoves(board));
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

                // If the character is a digit
                if (Character.isDigit(ch)) {
                    // Add an empty tile in the current rank denoted by digit in FEN
                    int numFiles = Integer.parseInt(ch + "");
                    int tempFile = filesAddedForRow;
                    for (int i = tempFile; i < (numFiles + tempFile); i++) {
                        Position emptyTilePos = new Position(7 - rankCount, i);
                        board.getTileMap().put(emptyTilePos, new Tile(emptyTilePos));
                        filesAddedForRow++;
                    }
                }
                // If the character was a piece, then add it for white or black
                else {
                    Position piecePosition = new Position(7 - rankCount, filesAddedForRow);
                    Piece newPiece = board.constructPiece(ch);
                    // Need to know the king's position
                    if(ch == 'k' || ch == 'K') {
                        board.setKingPosition((King) newPiece, piecePosition);
                    }
                    board.getTileMap().put(piecePosition, new Tile(piecePosition, newPiece));
                    filesAddedForRow++;
                }
            }
        }
    }

    /**
     * Whose turn it is White or Black
     * @param board the board to check
     * @param turn the turn string
     * @return the white or black player
     */
    private static Player getPlayerTurn(Board board, final String turn) throws FenException {
        if(Player.Color.WHITE.toString().equals(turn)) {
            return board.getPlayers().get(Player.Color.WHITE);
        } else if(Player.Color.BLACK.toString().equals(turn)) {
            return board.getPlayers().get(Player.Color.BLACK);
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

    // Custom exception parsing fen string
    static class FenException extends Exception {
        FenException(String message) {
            super(message);
        }
    }
}
