import java.util.ArrayList;

public class Perft {
    private Game game;
    public int mate = 0;
    public int caps = 0;
    public int checks = 0;
    public int promos = 0;
    public int castles = 0;
    public int ep_caps = 0;

    private void setGame(Game game) {
        this.game = game;
    }

    private int run_perft(int depth){
        int nodes = 0;
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
            } else {
                special = m & 15;
                if((special&4)!=0){
                    this.caps += 1;
                }
                if(special>5){
                    this.promos += 1;
                }
                if(special == 2 || special == 3){
                    this.castles += 1;
                }
                if(special == 5){
                    this.ep_caps += 1;
                }
                if(!game.isNotInCheck()){
                    this.checks += 1;
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
        int nodes = perft.run_perft(5);
        System.out.println("Nodes: "+nodes);
        System.out.println("Checkmates: "+perft.mate);
        System.out.println("Captures: "+perft.caps);
        System.out.println("Checks: "+perft.checks);
        System.out.println("Promos: "+perft.promos);
        System.out.println("EP captures: "+perft.ep_caps);
        System.out.println("Castles: "+perft.castles);
    }
}
