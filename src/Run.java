/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        Game game = new Game("2k5/8/8/2K5/4Q3/8/8/8 w - - 0 1");
        Search search = new Search();

        game.view_board();
        System.out.println("Startboard");
        System.out.println(" ");

        int alpha = -Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;
        long start = System.nanoTime();
        search.alphaBeta(game, alpha, beta, 6, 0);
        System.out.println(ChessBoard.parse_move(search.best_move.move)+" "+search.best_move.score);
        System.out.println("time taken: " + (System.nanoTime() - start)/(1000000000) + " seconds");
        game.make_move(search.best_move.move);
        game.view_board();
    }

}


