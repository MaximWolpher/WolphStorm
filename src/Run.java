import java.util.ArrayList;

/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        Game game = new Game("8/8/8/3k4/2p1p3/2PpP3/3P3P/r3K2R w K - 0 1");
        boolean legal;

        game.view_board();

        boolean move_made = false;
        ArrayList<Integer> moves = game.generate_moves();
        for(int m: moves){
            legal = game.make_move(m);
            if(!legal){
                game.unmake_move();
            }
            else {
                move_made = true;
                game.view_board();
                System.out.println("");
                game.unmake_move();
            }
        }
        if(!move_made) {
            System.out.println("CHECKMATE");
        }
    }
}


