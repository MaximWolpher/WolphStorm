/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        //Game game = new Game("qrb5/rk1p2K1/p2P4/Pp6/1N2n3/6p1/5nB1/6b1 w - - 0 1");
        Game game = new Game("r3k2r/3nq1b1/b2ppnp1/pN2N3/1p2P3/1P3Q1p/P1PBBPPP/1R3RK1 w kq - 7 10");
        Search search = new Search(game);

        game.view_board();
        System.out.println("Startboard");
        System.out.println(" ");
        int pv_move = 0;
        MoveClass best_move = null;
        System.out.println(game.getZobrist());
        long start = System.nanoTime();
        for(int iter=1; iter<=5; iter++) {
            int alpha = -Integer.MAX_VALUE;
            int beta = Integer.MAX_VALUE;
            //search.alphaBeta(game, alpha, beta, iter, 0);
            search.alphaBetaTT(alpha, beta, iter, 0);
            //search.pvSearch(alpha, beta, iter, 0, pv_move);
            //pv_move = search.best_move.move;
            System.out.println("Depth: "+iter);
            best_move = game.getTransTable().getBestMove(game);
            System.out.println(ChessBoard.parse_move(best_move.move));

        }
        System.out.println("time taken: " + (System.nanoTime() - start) / (1000000000) + " seconds");
        game.make_move(best_move.move);
        game.view_board();
    }

}


