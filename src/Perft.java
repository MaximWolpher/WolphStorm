import java.math.BigInteger;
import java.util.ArrayList;

public class Perft {
    private Game game;
    public int mate = 0;
    public long caps = 0;
    public int checks = 0;
    public int promos = 0;
    public int castles = 0;
    public int ep_caps = 0;
    public int draw = 0;

    private void setGame(Game game) {
        this.game = game;
    }

    private long run_perft(int depth){
        long nodes = 0;
        int special;
        boolean move_made = false;

        if(depth == 0){
            return 1;
        }
        ArrayList<Integer> moves = this.game.generate_moves();
        for(int m: moves) {
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            else {
                if(depth == 1) {
                    special = m & 15;
                    if ((special & 4) != 0) {
                        this.caps += 1;
                    }
                    if (special > 5) {
                        this.promos += 1;
                    }
                    if (special == 2 || special == 3) {
                        this.castles += 1;
                    }
                    if (special == 5) {
                        this.ep_caps += 1;
                    }
                    if (!game.isNotInCheck()) {
                        this.checks += 1;
                    }
                }
                //System.out.println(game.getTurn());
                //game.view_board();
                nodes += run_perft(depth - 1);
                game.unmake_move();
                move_made = true;
            }
        }
        if (!move_made & depth == 1){
            if(game.isNotInCheck()){
                this.draw += 1;
            }
            else {
                this.mate += 1;
            }
        }
        return nodes;
    }

    public static void main(String[] args) {
        Game game = new Game("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        Perft perft = new Perft();
        perft.setGame(game);
        System.out.println("Zobrist first: " + game.getZobrist());
        long start = System.nanoTime();
        long nodes = perft.run_perft(5);
        System.out.println("Zobrist after: "+ game.getZobrist());
        System.out.println("Time taken: " + (System.nanoTime() - start) / (1e9) + " seconds");
        System.out.println("Nodes per second: " + (nodes / ((System.nanoTime() - start) / (1e3))) + " Mil Nodes/s");
        System.out.println("Nodes: "+nodes);
        System.out.println("Captures: "+perft.caps);
        System.out.println("EP captures: "+perft.ep_caps);
        System.out.println("Castles: "+perft.castles);
        System.out.println("Promos: "+perft.promos);
        System.out.println("Checks: "+perft.checks);
        System.out.println("Checkmates: "+perft.mate);
        System.out.println("Stalemate: "+perft.draw);

    }
}
