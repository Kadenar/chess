package com.chess.engine.board;

import com.chess.engine.Position;
import com.chess.engine.moves.Move;
import com.chess.engine.moves.MoveHistory;
import com.chess.engine.moves.MoveUtils;
import com.chess.engine.pieces.Piece;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PGNUtils {


    static String tagNames[] = {
            "Event",
            "Site",
            "Date",
            "Round",
            "White",
            "Black",
            "Result",
            "WhiteElo",
            "BlackElo"
    };

    /*
        [Event "F/S Return Match"]
        [Site "Belgrade, Serbia JUG"]
        [Date "1992.11.04"]
        [Round "29"]
        [White "Fischer, Robert J."]
        [Black "Spassky, Boris V."]
        [Result "1/2-1/2"]

        1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 {This opening is called the Ruy Lopez.}
        4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8 10. d4 Nbd7
        11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 b4 15. Nb1 h6 16. Bh4 c5 17. dxe5
        Nxe4 18. Bxe7 Qxe7 19. exd6 Qf6 20. Nbd2 Nxd6 21. Nc4 Nxc4 22. Bxc4 Nb6
        23. Ne5 Rae8 24. Bxf7+ Rxf7 25. Nxf7 Rxe1+ 26. Qxe1 Kxf7 27. Qe3 Qg5 28. Qxg5
        hxg5 29. b3 Ke6 30. a3 Kd6 31. axb4 cxb4 32. Ra5 Nd5 33. f3 Bc8 34. Kf2 Bf5
        35. Ra7 g6 36. Ra6+ Kc5 37. Ke1 Nf4 38. g3 Nxh3 39. Kd2 Kb5 40. Rd6 Kc5 41. Ra6
        Nf2 42. g4 Bd3 43. Re6 1/2-1/2
     */

    /**
     * Parse a PGN file
     * @param fileName the file to open
     */
    public static MoveHistory parsePGN(String fileName) {
        File file = new File(fileName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String token = bufferedReader.readLine();

            while(token != null && token.length() != 0) {

                token = token.replaceAll("/[^.|0-9]+/g", "");

                System.out.println(token);

                // Get the next token
                token = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the PGN from the move history
     * @param history the history of moves
     * @return the PGN representation of these moves
     */
    public static String getPGN(MoveHistory history) {
        StringBuilder builder = new StringBuilder();
        List<Move> moveHistory = history.getMoves(Objects::nonNull).collect(Collectors.toList());

        for(int i = 0; i < moveHistory.size(); i++) {
            Move move = moveHistory.get(i);
            builder.append(i+1).append(". ").append(history.getNotationEntry(move)).append(" ");
        }

        return builder.toString();
    }

    /**
     * Perform the move from move history
     * @param board the board to perform moves on
     * @param history the history to perform moves from
     */
    public static void performPGNMoves(Board board, MoveHistory history) {
        Map<Position, Tile> tileMap =  board.getTileMap();
        List<Move> moves = history.getMoves(Objects::nonNull).collect(Collectors.toList());
        moves.forEach(move -> MoveUtils.executeMove(board, tileMap.get(move.getOrigin().getPosition()), tileMap.get(move.getDestination().getPosition()), true));
    }
}
