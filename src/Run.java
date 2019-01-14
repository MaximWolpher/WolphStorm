import java.util.ArrayList;

/**
 * Created by maxim on 04/03/2017
 */
public class Run {
    public static void main(String[] args) {

        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
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
                game.unmake_move();
            }
        }
        if(!move_made) {
            System.out.println("CHECKMATE");
        }
    }
}


