/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        //Game game = new Game("qrb5/rk1p2K1/p2P4/Pp6/1N2n3/6p1/5nB1/6b1 w - - 0 1");
        Game game = new Game("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        Search search = new Search(game);

        game.view_board();
        System.out.println("Startboard");
        System.out.println(" ");
        int pv_move = 0;
        for(int iter=1; iter<=5; iter++) {
            int alpha = -Integer.MAX_VALUE;
            int beta = Integer.MAX_VALUE;
            long start = System.nanoTime();
            //search.alphaBeta(game, alpha, beta, iter, 0);
            search.pvSearch(alpha, beta, iter, 0, pv_move);
            pv_move = search.best_move.move;
            System.out.println("Depth: "+iter+" Move: "+ChessBoard.parse_move(pv_move) + " Score: " + search.best_move.score);
            //System.out.println("time taken: " + (System.nanoTime() - start) / (1000000000) + " seconds");
        }
        game.make_move(search.best_move.move);
        game.view_board();
    }

}


