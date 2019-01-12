import java.util.ArrayList;

public class Game {
    private ChessBoard chess;
    private Moves moves;
    private Magics magics;

    public Game(String fen) {

        this.chess = new ChessBoard();
        this.moves = new Moves();
        this.magics = new Magics();

        this.chess.fen_to_board(fen);
        this.magics.generate_magics();
        this.chess.magics = this.magics;
        this.moves.init_static_moves();

        chess.turn^=1;
        moves.generate_moves(chess, magics);
        chess.turn^=1;
    }

    public ArrayList<Integer> generate_moves(){
        return this.moves.generate_moves(this.chess, this.magics);
    }

    public boolean make_move(int move){
        return this.chess.make_move(move);
    }

    public void unmake_move(){
        this.chess.unmake_move();
    }

    public void view_board(){
        Utils.view_board(this.chess.board);
    }
}
