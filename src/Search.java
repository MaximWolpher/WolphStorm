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

    int alphaBetaTT(int alpha, int beta, int depthleft, int ply) {
        int hash_flag = TranspositionTable.hashALPHA;
        int hash_val = this.transpositionTable.ProbeHash(this.game, depthleft, alpha, beta);
        if (hash_val != TranspositionTable.valUNKNOWN) {
            return hash_val;
        }
        if( depthleft == 0 ) {
            int eval = Rating.eval_func(this.game.getChess());
            this.transpositionTable.RecordHash(this.game, depthleft, eval, TranspositionTable.hashEXACT, null);
            return eval;
        }
        ArrayList<Integer> moves = this.game.generate_moves();
        MoveClass best_move = null;
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
                    this.transpositionTable.RecordHash(
                            this.game, depthleft, beta, TranspositionTable.hashBETA, this.best_move
                    );
                    return beta;   //  fail hard beta-cutoff
                }
                if (score > alpha) {
                    hash_flag = TranspositionTable.hashEXACT;
                    best_move = new MoveClass(m, score, ply);
                    alpha = score; // alpha acts like max in MiniMax
                }
            }
        }
        if(checkmate){
            alpha = this.game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        this.transpositionTable.RecordHash(this.game, depthleft, alpha, hash_flag, best_move);
        return alpha;
    }

    int pvSearch(int alpha, int beta, int depthleft, int ply, int pv) {
        int hash_flag = TranspositionTable.hashALPHA;
        int hash_val = this.transpositionTable.ProbeHash(this.game, depthleft, alpha, beta);
        if (hash_val != TranspositionTable.valUNKNOWN) {
            return hash_val;
        }
        if( depthleft == 0 ) {
            int eval = Rating.eval_func(this.game.getChess());
            this.transpositionTable.RecordHash(this.game, depthleft, eval, TranspositionTable.hashEXACT, null);
            return eval;
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
                if (score >= beta) {
                    this.transpositionTable.RecordHash(
                            this.game, depthleft, beta, TranspositionTable.hashBETA, this.best_move
                    );
                    return beta;   // fail-hard beta-cutoff
                }
                if (score > alpha) {
                    alpha = score;
                    bSearchPv = false;
                    hash_flag = TranspositionTable.hashEXACT;
                    if(ply == 0){
                        this.best_move = new MoveClass(m, score, ply);
                    }
                }
            }
        }
        if(checkmate){
            alpha = this.game.isNotInCheck() ? 0: ply - MATE_SCORE;
        }
        this.transpositionTable.RecordHash(this.game, depthleft, alpha, hash_flag, this.best_move);
        return alpha;
    }
    int zwSearch(int beta, int depth, int ply ) {
        // alpha == beta - 1
        // this is either a cut- or all-node
        if( depth == 0 ) {
            int eval = Rating.eval_func(this.game.getChess());
            this.transpositionTable.RecordHash(this.game, depth, eval, TranspositionTable.hashBETA, null);
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
                    this.transpositionTable.RecordHash(this.game, depth, beta, TranspositionTable.hashBETA, null);
                    return beta;   // fail-hard beta-cutoff
                }
            }
        }
        if(checkmate){
            beta = this.game.isNotInCheck() ? 0: ply - MATE_SCORE; // TODO plus one?
        }
        // checkmate??

        this.transpositionTable.RecordHash(this.game, depth, beta-1, TranspositionTable.hashBETA, null);
        return beta-1; // fail-hard, return alpha
    }

}
