import java.util.ArrayList;

public class Perft {
    private Game game;

    private void setGame(Game game) {
        this.game = game;
    }

    private int run_perft(int depth){
        int nodes = 0;
        if(depth == 0){
            return 1;
        }
        ArrayList<Integer> moves = this.game.generate_moves();
        for(int m: moves) {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            } else {

                //System.out.println("Depth: "+depth);
                if(depth==2){
                    game.view_board();
                    System.out.println("attacks");
                    game.enemy_attacks();
                }

                nodes += run_perft(depth - 1);
                game.unmake_move();
            }
        }
        return nodes;
    }

    public static void main(String[] args) {
        Game game = new Game("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        Perft perft = new Perft();
        perft.setGame(game);
        int nodes = perft.run_perft(2);
        System.out.println("Nodes: "+nodes);

    }
}
