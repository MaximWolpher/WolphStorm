import java.util.ArrayList;

/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

            Game game = new Game("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
            boolean legal;

            ArrayList<Integer> moves = game.generate_moves();
            for(int m: moves){
                legal = game.make_move(m);
                if(!legal){
                    game.unmake_move();
                }
                else {
                    game.view_board();
                    game.unmake_move();
            }
        }
    }
}


