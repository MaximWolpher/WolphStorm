public class Transposition {

    public long key;
    public int depth;
    public int flag;
    public int value;
    public MoveClass best_move;


    public Transposition(long key, int depth, int flag, int value, MoveClass best_move) {
        this.key = key;
        this.depth = depth;
        this.flag = flag;
        this.value = value;
        this.best_move = best_move;
    }
}
