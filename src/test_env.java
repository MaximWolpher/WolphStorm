import java.util.ArrayList;

public class test_env {
    static int fast(int single){
        return single + (7 - ((single >>> 3) << 1) << 3);
    }
    static int slow(int single){
       return single + (7 - (single / 8) * 2) * 8;
    }

    public static void main(String[] args) {
        /*Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        System.out.println(Rating.piece_position(game.getChess(),false));
        ArrayList<Integer> moves = game.generate_moves();
        game.make_move(moves.get(2));
        game.view_board();
        System.out.println(Rating.piece_position(game.getChess(),false));*/
        for(int y=0; y<10; y++) {
            long start = System.nanoTime();
            for (int x = 0; x < 64; x++) {
                int nope = x%8;
            }
            long endfirst = System.nanoTime();
            for (int x = 0; x < 64; x++) {
                int yes = x&7;
            }
            long endend = System.nanoTime();

            System.out.println("Fast: " + (endfirst - start));
            System.out.println("Slow: " + (endend - endfirst));
        }

    }
}


