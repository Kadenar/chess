import com.chess.engine.board.Board;
import com.chess.ui.BoardGUI;

public class Chess {

    public static void main(String[] args) {
        new BoardGUI(new Board("r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq c6 0 2"));
    }
}
