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

        //this.chess.fen_to_board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        this.chess.fen_to_board("rn2k2b/6P1/3n4/4PpP1/8/n7/PPPPP2P/R3K2R w KQkq f3 0 1");
        Utils.view_board(this.chess.board);
        this.magics.generate_magics();
        this.moves.init_static_moves();
        Utils.view_board(this.chess.board);
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
            //Utils.view_board(game.chess.board);

        }
    }

}
