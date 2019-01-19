import java.util.ArrayList;

/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        Game game = new Game("8/2k5/8/8/8/7n/8/4K1N1 w - - 0 1");
        Search search = new Search();
        game.view_board();

        ArrayList<Integer> moves = game.generate_moves();
        move_class best_move = new move_class(0,0,0);
        int score;
        int alpha = -Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;
        for(int move: moves) {

            boolean legal = game.make_move(move);
            if (!legal) {
                game.unmake_move();
            }
            else {
                score = search.alphaBeta(game, alpha, beta, 1, 1);
                game.unmake_move();
                System.out.println(ChessBoard.parse_move(move) + " " + score);
                if (score > alpha) {
                    alpha = score;
                    best_move = new move_class(move, score, 0);
                }
            }
        }
        System.out.println("best");
        System.out.println(ChessBoard.parse_move(best_move.move)+" "+best_move.score);
    }

}


