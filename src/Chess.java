import com.chess.engine.board.Board;
import com.chess.engine.board.FenUtils;
import com.chess.ui.ChessFrame;

import javax.swing.SwingUtilities;

class Chess {

    public static void main(String[] args) {
        //Board board = new Board("r3k2r/p3b1pp/np1qbn2/2pN4/1PP1Np2/B2B4/P1QP2PP/R3K2R b KQkq - 0 9");
        //Board board = new Board("rnb1k1nr/p3b1pp/1p1q4/2pp4/2P1pp2/BPNB1N2/P1QP2PP/R3K2R w KQkq - 2 9");
        //Board board = new Board("r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq c6 0 2");
        String defaultPosition = FenUtils.DEFAULT_POSITION;
        String castlingBroken = "2k1r2r/pbpp1pbp/5NpB/1P5N/6n1/1q1Q2P1/P1P1PPBP/R3K2R w KQ - 1 18";
        String anotherCastle = "r3k2r/pB1p3p/1p1qp1p1/2p2p2/2P5/NPQ3PP/P3PP2/R3K2R w KQkq - 0 10";
        String lastCastle = "2k1r2r/pbpp1pbp/5NpB/1P5N/1q1Q2n1/6P1/P1P1PPBP/R3K2R w KQ - 3 19";
        String gameOver = "1kQ4B/p1p2p1p/1N4p1/1P5N/q5n1/4r1P1/P1P1PPBP/R3K2R b KQ - 0 12";
        SwingUtilities.invokeLater(() -> new ChessFrame(new Board(defaultPosition)));
    }
}
