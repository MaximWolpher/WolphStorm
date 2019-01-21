import java.util.HashMap;

public class TranspositionTable {
    private HashMap<Integer, Transposition> hash_table;
    private int size = 1<<20;
    public static final int hashEXACT = 0;
    public static final int hashALPHA = 1;
    public static final int hashBETA = 2;
    public static final int valUNKNOWN = 2345678; //larger than mate


    public TranspositionTable(){
        // trying loadFactor > 1.0f to not rehash
        this.hash_table = new HashMap<>(this.size,1.1f);
    }

    public int ProbeHash(Game game, int depth, int alpha, int beta){
        long zobrist_key = game.getZobrist();
        int zobrist_idx = (int)(zobrist_key % this.size);
        Transposition hash = this.hash_table.get(zobrist_idx);

        if (hash.key == zobrist_key){
            if (hash.depth >= depth) {
                if (hash.flag == hashEXACT){
                    return hash.value;
                }
                if ((hash.flag == hashALPHA) && (hash.value <= alpha)){
                    return alpha;
                }
                if ((hash.flag == hashBETA) && (hash.value >= beta)){
                    return beta;
                }
            }
            // WHAT? RememberBestMove();
        }
        return valUNKNOWN;
    }

    public void RecordHash(Game game, int depth, int val, int hashf, MoveClass best_move) {
        long zobrist_key = game.getZobrist();
        int zobrist_idx = (int)(zobrist_key % this.size);
        Transposition hash = this.hash_table.get(zobrist_idx);

        hash.key = zobrist_key;
        hash.best_move = best_move;
        hash.value = val;
        hash.flag = hashf;
        hash.depth = depth;
    }
}
