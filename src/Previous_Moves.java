/**
 * Created by maxim on 17/04/2017.
 */
public class Previous_Moves {
    int move_prev;
    int castle_prev;
    int EP_prev;
    int hmc_prev;
    int cap_piece;
    long[][] attacks;

    Previous_Moves(int move, int castles, int EP, int hmc, int cap_piece, long[][] attacks){
        this.move_prev = move;
        this.castle_prev = castles;
        this.EP_prev = EP;
        this.hmc_prev = hmc;
        this.cap_piece = cap_piece;
        this.attacks = attacks;

    }
}
