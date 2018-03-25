import com.chess.engine.board.Board;
import com.chess.ui.BoardGUI;

public class Chess {

    public static void main(String[] args) {
        String fen1 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        String fen2 = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
        String fen3 = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        String fen4 = "r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq c6 0 2";

        //Board board = new Board();
        Board board = new Board(fen4);
        /*System.out.println(board.toString());

        // Test another positino
        BoardUtils.getInstance().updateBoardWithFen(board, fen1);
        System.out.println(board.toString());

        BoardUtils.getInstance().updateBoardWithFen(board, fen2);
        System.out.println(board.toString());

        BoardUtils.getInstance().updateBoardWithFen(board, fen3);
        System.out.println(board.toString());
        */
        //BoardUtils.getInstance().updateBoardWithFen(board, fen4);
        //System.out.println(board.toString());

        new BoardGUI(board);
    }
}
