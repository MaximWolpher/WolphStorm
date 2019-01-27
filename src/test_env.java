import java.util.ArrayList;

public class test_env {

    public static void main(String[] args) {
        Game game = new Game("1r2k2r/1b3pbp/1NPqN1n1/1P1Q4/1P2P3/P3B3/5PP1/R3KB1R w KQkq - 0 1");
        game.view_board();
        ArrayList<Integer> moves = game.generate_moves();
        for(int m: moves){
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            System.out.println(ChessBoard.parse_move(m));
            game.view_board();
            game.unmake_move();
        }
        System.out.println(moves.toString());
        System.out.println("SORTING");
        moves = game.updateMoves(moves,0);
        System.out.println(moves.toString());

        for(int m: moves){
            boolean legal = game.make_move(m);
            if (!legal) {
                game.unmake_move();
            }
            System.out.println(ChessBoard.parse_move(m));
            game.view_board();
            game.unmake_move();
        }
    }
}


