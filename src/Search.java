import java.util.ArrayList;

public class Search {
    move_class best_move;

    int alphaBeta(Game game, int alpha, int beta, int depthleft, int ply) {
        if( depthleft == 0 ) {
            return Rating.eval_func(game.getChess());
        }
        ArrayList<Integer> moves = game.generate_moves();
        boolean checkmate = true;
        for (int m: moves)  {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            else {
                checkmate = false;
                int score = -alphaBeta(game, -beta, -alpha, depthleft - 1, ply + 1);
                game.unmake_move();
                if (score >= beta) {
                    return beta;   //  fail hard beta-cutoff
                }
                if (score > alpha) {
                    if(ply == 0){
                        this.best_move = new move_class(m, score, ply);
                    }
                    alpha = score; // alpha acts like max in MiniMax
                }
            }
        }
        if(checkmate){
            return game.isNotInCheck() ? 0: ply - 999999;
        }
        return alpha;
    }
}
