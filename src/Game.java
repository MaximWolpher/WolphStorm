import java.util.ArrayList;

public class Game {
    private ChessBoard chess;

    public Game(String fen) {

        this.chess = new ChessBoard();
        Moves moves = new Moves();
        moves.init_static_moves();
        Magics magics = new Magics();
        magics.generate_magics();

        this.chess.setMoves(moves);
        this.chess.setMagics(magics);
        this.chess.fen_to_board(fen);

        this.chess.changeTurn();
        moves.generate_moves(chess, magics);
        this.chess.changeTurn();
    }

    public ArrayList<Integer> generate_moves(){
        return this.chess.generate_moves();
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

    public void enemy_attacks(){
        this.chess.enemy_attacks();
    }
}
