import java.util.ArrayList;
import java.util.Stack;

public class Search {
    Stack<move_class> best_move = new Stack<>();

    int alphaBeta(Game game, int alpha, int beta, int depthleft, int ply) {
        if( depthleft == 0 ) {
            return Rating.eval_func(game.getChess());
        }


        ArrayList<Integer> moves = game.generate_moves();
        for (int m: moves)  {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            else {
                int score = -alphaBeta(game, -beta, -alpha, depthleft - 1, ply + 1);
                game.unmake_move();
                if (score >= beta) {
                    return beta;   //  fail hard beta-cutoff
                }
                if (score > alpha) {
                    alpha = score; // alpha acts like max in MiniMax
                }
            }
        }
        return alpha;
    }

}