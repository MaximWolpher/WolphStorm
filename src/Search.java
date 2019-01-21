import java.util.ArrayList;

public class Search {
    MoveClass best_move;
    private static final int MATE_SCORE = 9999999;
    private Game game;
    private TranspositionTable transpositionTable;

    public Search(Game game) {
        this.game = game;
        this.transpositionTable = game.getTransTable();
    }

    int alphaBeta(int alpha, int beta, int depthleft, int ply) {
        if( depthleft == 0 ) {
            return Rating.eval_func(this.game.getChess());
        }
        ArrayList<Integer> moves = this.game.generate_moves();
        boolean checkmate = true;
        for (int m: moves)  {
            boolean legal = this.game.make_move(m);
            if (!legal) {
                this.game.unmake_move();
            }
            else {
                checkmate = false;
                int score = -alphaBeta(-beta, -alpha, depthleft - 1, ply + 1);
                this.game.unmake_move();
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
            return this.game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        return alpha;
    }

    int pvSearch(int alpha, int beta, int depthleft, int ply, int pv) {
        if( depthleft == 0 ) {
            return Rating.eval_func(this.game.getChess());
        }
        boolean bSearchPv = true;
        int score;
        ArrayList<Integer> moves = this.game.generate_moves();
        if(ply == 0) {
            this.game.updateMoves(moves, pv);
        }
        boolean checkmate = true;
        for (int m: moves)  {
            boolean legal = this.game.make_move(m);
            if (!legal) {
                this.game.unmake_move();
            }
            else {
                checkmate = false;
                if (bSearchPv) {
                    score = -pvSearch(-beta, -alpha, depthleft - 1, ply + 1, pv);
                }
                else {
                    score = -zwSearch(-alpha, depthleft - 1, ply + 1);
                    if (score > alpha) // in fail-soft ... && score < beta ) is common
                        score = -pvSearch(-beta, -alpha, depthleft - 1, ply + 1, pv); // re-search
                }
                this.game.unmake_move();
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
            return this.game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        return alpha;
    }
    int zwSearch(int beta, int depth, int ply ) {
        // alpha == beta - 1
        // this is either a cut- or all-node
        if( depth == 0 ) {
            return Rating.eval_func(this.game.getChess());
        }
        ArrayList<Integer> moves = this.game.generate_moves();
        int score;
        boolean checkmate = true;
        for (int m: moves) {
            boolean legal = this.game.make_move(m);
            if (!legal) {
                this.game.unmake_move();
            }
            else {
                checkmate = false;
                score = -zwSearch(1 - beta, depth - 1, ply + 1);
                this.game.unmake_move();
                if (score >= beta) {
                    return beta;   // fail-hard beta-cutoff
                }
            }
        }
        if(checkmate){
            return this.game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        // checkmate??
        return beta-1; // fail-hard, return alpha
    }

}
