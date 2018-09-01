import com.chess.engine.board.Board;
import com.chess.ui.ChessFrame;

import javax.swing.SwingUtilities;

public class Chess {

    public static void main(String[] args) {
        //Board board = new Board(FenUtils.DEFAULT_POSITION);
        Board board = new Board("r3k2r/p3b1pp/np1qbn2/2pN4/1PP1Np2/B2B4/P1QP2PP/R3K2R b KQkq - 0 9");
        //Board board = new Board("rnb1k1nr/p3b1pp/1p1q4/2pp4/2P1pp2/BPNB1N2/P1QP2PP/R3K2R w KQkq - 2 9");
        //Board board = new Board("r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq c6 0 2");
        SwingUtilities.invokeLater(() -> new ChessFrame(board));
    }
}
