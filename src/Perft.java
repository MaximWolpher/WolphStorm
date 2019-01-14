import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;

public class Perft {
    private Game game;
    public int mate = 0;
    public int caps = 0;

    private void setGame(Game game) {
        this.game = game;
    }

    private int run_perft(int depth){
        int nodes = 0;
        boolean move_made = false;

        if(depth == 0){
            return 1;
        }
        ArrayList<Integer> moves = this.game.generate_moves();
        for(int m: moves) {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            } else {
                if((m&4)!=0){
                    this.caps += 1;
                }
                nodes += run_perft(depth - 1);
                game.unmake_move();
                move_made = true;
            }
        }
        if (!move_made){
            this.mate += 1;
        }
        return nodes;
    }

    public static void main(String[] args) {
        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        Perft perft = new Perft();
        perft.setGame(game);
        int nodes = perft.run_perft(6);
        System.out.println("Nodes: "+nodes+" "+perft.mate+" "+perft.caps);

    }
}
