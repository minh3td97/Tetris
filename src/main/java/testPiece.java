import tetris.Board;
import tetris.Piece;

public class testPiece {
    public static void main(String[] args) {
        Board board = new Board(4,6);
        Piece[] piece_ = Piece.getPieces();

        board.place(piece_[Piece.SQUARE],0,0); board.commit();
        System.out.println(board.toString());
        board.clearRows();
        System.out.println(board.toString());
        board.undo();
        System.out.println(board.toString());

        board.place(piece_[Piece.SQUARE],2,0); board.commit();
        System.out.println(board.toString());
        board.clearRows();
        System.out.println(board.toString());
        board.undo();
        System.out.println(board.toString());
    }
}
