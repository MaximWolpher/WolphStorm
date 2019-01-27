/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        //Game game = new Game("qrb5/rk1p2K1/p2P4/Pp6/1N2n3/6p1/5nB1/6b1 w - - 0 1");
        Game game = new Game("1rr3k1/3b1pq1/4p2p/1n1pP2P/2P2QP1/1p6/1P6/1K1RNB1R b - - 0 31");
        Search search = new Search(game);

        game.view_board();
        System.out.println("Startboard");
        System.out.println(" ");
        MoveClass best_move = null;
        System.out.println(game.getZobrist());
        long start = System.nanoTime();
        for(int iter=1; iter<=10; iter++) {
            int alpha = -Integer.MAX_VALUE;
            int beta = Integer.MAX_VALUE;
            //search.alphaBeta(game, alpha, beta, iter, 0);
            //search.alphaBetaTT(alpha, beta, iter, 0);
            search.pvSearch(alpha, beta, iter, 0, best_move);
            System.out.println("Depth: "+iter);
            best_move = game.getTransTable().getBestMove(game);
            System.out.println(ChessBoard.parse_move(best_move.move));

        }
        System.out.println("time taken: " + (System.nanoTime() - start) / (1000000000) + " seconds");
        game.make_move(best_move.move);
        game.view_board();
    }

}


