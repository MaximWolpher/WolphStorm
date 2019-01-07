import jdk.jfr.Unsigned;

import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    private ChessBoard chess;
    private Moves moves;
    private Magics magics;

    private void initiate(){
        this.chess = new ChessBoard();
        this.moves = new Moves();
        this.magics = new Magics();

        this.chess.fen_to_board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        //this.chess.fen_to_board("rnbqkbnr/ppppp1pp/8/8/4pP11/8/PPPPPP1P/R3K2R b KQkq - 0 1");
        this.magics.generate_magics();
        this.moves.init_static_moves();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.initiate();
        ArrayList<Integer> moves = game.moves.generate_moves(game.chess, game.magics);
        System.out.println(moves.toString());
        for(int m: moves){
            game.chess.make_move(m);
            Utils.view_board(game.chess.board);
            game.chess.unmake_move();
            Utils.view_board(game.chess.board);

        }
    }

}
