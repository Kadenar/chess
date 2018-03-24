package com.chess.engine.board;

import com.chess.engine.board.Tile.EmptyTile;
import com.chess.engine.board.Tile.OccupiedTile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.utils.BoardUtils;
import com.chess.engine.utils.FenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private static final int NUM_TILES = 64;
    private List<Tile> tiles = new ArrayList<>(NUM_TILES);
    private Map<String, Player> players = new HashMap<>();

    // Variables used to load/save FEN:
    // Full FEN string
    private String fen;

    // Which player's turn it is
    private Player currentMove;

    // State of castling / enPassant
    private String castling, enPassant;

    // Number of halfmoves / full moves
    private int halfMoves, fullMoves;

    public Board() {
        this("");
    }

    public Board(String fen) {
        // Add both players
        this.players.put(Player.WHITE.toString(), Player.WHITE);
        this.players.put(Player.BLACK.toString(), Player.BLACK);

        // Load in the default position
        try {
            this.loadFen(fen);
        } catch(Exception e) {
            System.out.println("Error encountered parsing fen");
            e.printStackTrace();
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
    public void loadFen(String fen) throws Exception {
        // If no FEN was provided, use the default starting position
        if ("".equals(fen)) {
            fen = FenUtils.DEFAULT_POSITION;
        }

        // Check if the FEN string provided was valid
        if(!FenUtils.isValid(fen)) {
            throw new Exception("Invalid fen attempted to be loaded");
        }

        // Set the current board's fen if it was valid
        this.fen = fen;

        // Get the game piece locations and add them to the board
        buildFromFEN(FenUtils.getGamePieces(fen));
        currentMove = FenUtils.getPlayerTurn(fen).equals(Player.WHITE.toString()) ? Player.WHITE : Player.BLACK;
        castling = FenUtils.getCastlingAbility(fen);
        enPassant = FenUtils.getEnPassantSquare(fen);
        halfMoves = FenUtils.getHalfMove(fen);
        fullMoves = FenUtils.getFullMove(fen);
    }

    /**
     * Build board with pieces from fen
     * @param boardPositions
     * @throws Exception
     */
    private void buildFromFEN(final String boardPositions) throws Exception {

        // Split fen to create each rank
        String ranks[] = boardPositions.split("/");
        if(ranks.length !=8 ) {
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
                        this.getTiles().add(new EmptyTile(new Position(7 - rankCount, i)));
                        filesAddedForRow++;
                    }
                }
                // If the character was a piece, then add it for white or black
                else {
                    Position piecePosition = new Position(7 - rankCount, filesAddedForRow);
                    Piece newPiece = BoardUtils.getInstance().constructPiece(ch, piecePosition);
                    this.getTiles().add(new OccupiedTile(piecePosition, newPiece));
                    filesAddedForRow++;
                }
            }
        }
    }

    public List<Tile> getTiles() {
        return this.tiles;
    }

    public Map<String, Player> getPlayers() {
        return this.players;
    }

    public String getCurrentFen() {
        return fen;
    }

    public void printFen() {
        String fen = getCurrentFen();
        // Print the current fen
        System.out.println(fen);
        System.out.println("=========");
        System.out.println("Board positions: " + FenUtils.getGamePieces(fen));
        System.out.println("Turn: " + currentMove.toString());
        System.out.println("Castling: " + castling);
        System.out.println("EnPassant: " + enPassant);
        System.out.println("Half moves: " + halfMoves);
        System.out.println("Full moves: " + fullMoves);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < NUM_TILES; i++) {
            if(i == 0 || i % 8 == 0) {
                builder.append(7 - i/8+1 + " ");
            }

            final String tileText = prettyPrint(getTiles().get(i));
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % 8 == 0) {
                builder.append("\n");
            }
        }
        builder.append("   a  b  c  d  e  f  g  h");
        return builder.toString();
    }

    private static String prettyPrint(Tile tile) {
        return tile.toString();
    }
}
