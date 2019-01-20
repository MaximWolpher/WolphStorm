import java.util.ArrayList;

public class Search {
    MoveClass best_move;
    private static final int MATE_SCORE = 9999999;

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
                        this.best_move = new MoveClass(m, score, ply);
                    }
                    alpha = score; // alpha acts like max in MiniMax
                }
            }
        }
        if(checkmate){
            return game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        return alpha;
    }

    int pvSearch(Game game, int alpha, int beta, int depthleft, int ply, int pv) {
        if( depthleft == 0 ) {
            return Rating.eval_func(game.getChess());
        }
        boolean bSearchPv = true;
        int score;
        ArrayList<Integer> moves = game.generate_moves();
        if(ply == 0) {
            game.updateMoves(moves, pv);
        }
        boolean checkmate = true;
        for (int m: moves)  {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            else {
                checkmate = false;
                if (bSearchPv) {
                    score = -pvSearch(game, -beta, -alpha, depthleft - 1, ply + 1, pv);
                }
                else {
                    score = -zwSearch(game, -alpha, depthleft - 1, ply + 1);
                    if (score > alpha) // in fail-soft ... && score < beta ) is common
                        score = -pvSearch(game, -beta, -alpha, depthleft - 1, ply + 1, pv); // re-search
                }
                game.unmake_move();
                if (score >= beta)
                    return beta;   // fail-hard beta-cutoff
                if (score > alpha) {
                    alpha = score;
                    bSearchPv = false;
                    if(ply == 0){
                        this.best_move = new MoveClass(m, score, ply);
                    }
                }
            }
        }
        if(checkmate){
            return game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        return alpha;
    }
    int zwSearch(Game game, int beta, int depth, int ply ) {
        // alpha == beta - 1
        // this is either a cut- or all-node
        if( depth == 0 ) {
            return Rating.eval_func(game.getChess());
        }
        ArrayList<Integer> moves = game.generate_moves();
        int score;
        boolean checkmate = true;
        for (int m: moves) {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            else {
                checkmate = false;
                score = -zwSearch(game,1 - beta, depth - 1, ply + 1);
                game.unmake_move();
                if (score >= beta) {
                    return beta;   // fail-hard beta-cutoff
                }
            }
        }
        if(checkmate){
            return game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        // checkmate??
        return beta-1; // fail-hard, return alpha
    }

}
