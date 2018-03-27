package com.chess.engine.utils;

import com.chess.engine.board.Board;
import com.chess.engine.board.Player;
import com.chess.engine.board.Position;
import com.chess.engine.pieces.*;

import java.util.Map;

import static com.chess.engine.board.Tile.EmptyTile;
import static com.chess.engine.board.Tile.OccupiedTile;

public class BoardUtils {

    private static final BoardUtils INSTANCE = getInstance();
    private Board board;

    public static BoardUtils getInstance() {
        if(INSTANCE == null) {
            return new BoardUtils();
        }

        return INSTANCE;
    }

    // This object can only reference one board at a time
    private BoardUtils() { /*Singleton*/ }

    public Board getBoard() {
        return this.board;
    }

    /**
     * Updates reference to game board and recreates it with given fen
     * @param board the game board to be created
     * @param fen the fen string to update the board with
     */
    public void updateBoardWithFen(Board board, String fen) {
        this.board = board;

        // Clear each player's pieces
        for (Map.Entry<String, Player> entry : board.getPlayers().entrySet()) {
            entry.getValue().getPieces().clear();
        }

        // Clear tiles on the board
        board.getTileMap().clear();

        // Load in the default position
        try {
            loadFen(fen);
        } catch(Exception e) {
            System.out.println("Error encountered parsing fen");
        }
    }

    /**
     * Loads the given fen string if valid
     * - Populates the board with the pieces for each player
     * - Sets whose turn it is
     * - Determines castling ability
     * - Gets enpassant square if any
     * - Sets half move counter
     * - Sets full move counter
     * @param fen a valid FEN string for a standard chess game
     * @throws Exception parsing error occurred and FEN string was not valid
     */
    private void loadFen(String fen) throws Exception {

        // If no FEN was provided, use the default starting position
        if ("".equals(fen)) {
            fen = FenUtils.DEFAULT_POSITION;
        }

        // Check if the FEN string provided was valid
        if(!FenUtils.isValid(fen)) {
            throw new Exception("Invalid fen attempted to be loaded");
        }

        // Set the current board's fen if it was valid
        board.setCurrentFen(fen);

        // Get the game piece locations and add them to the board
        buildBoardFromFEN(FenUtils.getGamePieces(fen));
        board.setCurrentMove(FenUtils.getPlayerTurn(fen).equals(Player.WHITE.toString()) ? Player.WHITE : Player.BLACK);
        board.setCastlingAbility(FenUtils.getCastlingAbility(fen));
        board.setEnpassantSquare(FenUtils.getEnPassantSquare(fen));
        board.setHalfMoves(FenUtils.getHalfMove(fen));
        board.setFullMoves(FenUtils.getFullMove(fen));
    }

    /**
     * Build board with pieces from fen
     * @param boardPositions the board position portion of the FEN string
     * @throws Exception exception if incorrect positions passed
     */
    private void buildBoardFromFEN(final String boardPositions) throws Exception {

        // Split fen to create each rank
        String ranks[] = boardPositions.split("/");
        if(ranks.length != 8 ) {
            throw new Exception("Board representation incorrect in the FEN.");
        }

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
                        board.getTileMap().put(pos.toString(), new EmptyTile(pos));
                        filesAddedForRow++;
                    }
                }
                // If the character was a piece, then add it for white or black
                else {
                    Position piecePosition = new Position(7 - rankCount, filesAddedForRow);
                    Piece newPiece = BoardUtils.getInstance().constructPiece(ch, piecePosition);
                    board.getTileMap().put(piecePosition.toString(), new OccupiedTile(piecePosition, newPiece));
                    filesAddedForRow++;
                }
            }
        }
    }

    private Piece constructPiece(char ch, Position position) {
        Piece piece = null;

        // Determine the color this piece belongs to
        Player color = Character.isUpperCase(ch) ? Player.WHITE : Player.BLACK;

        // Determine the type of piece
        switch(Character.toLowerCase(ch)) {
            case 'p':
                piece = new Pawn(color, position);
                break;
            case 'b':
                piece = new Bishop(color, position);
                break;
            case 'r':
                piece = new Rook(color, position);
                break;
            case 'n':
                piece = new Knight(color, position);
                break;
            case 'q':
                piece = new Queen(color, position);
                break;
            case 'k':
                piece = new King(color, position);
                break;
            default:
                break;
        }

        // Add the piece for that player
        color.addPiece(piece);

        // return the piece
        return piece;
    }
}
