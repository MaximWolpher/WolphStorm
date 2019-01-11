import java.util.ArrayList;

public class Game {
    private ChessBoard chess;
    private Moves moves;
    private Magics magics;

    private void initiate(){
        this.chess = new ChessBoard();
        this.moves = new Moves();
        this.magics = new Magics();

        //this.chess.fen_to_board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        this.chess.fen_to_board("rn1bkq1r/6P1/3n4/4P1P1/8/n7/PPPPP2P/R3K2R w KQkq f3 0 1");
        Utils.view_board(this.chess.board);
        this.magics.generate_magics();
        this.moves.init_static_moves();
        chess.turn^=1;
        moves.generate_moves(chess, magics);
        chess.turn^=1;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.initiate();

        ArrayList<Integer> moves = game.moves.generate_moves(game.chess, game.magics);
        for(int turn=0; turn<2; turn++) {
            for (int type_idx = 0; type_idx < 6; type_idx++) {
                System.out.println(turn + " " + type_idx);
                Utils.view_bitboard(game.moves.attacks[turn][type_idx]);
            }
        }
        System.out.println("");
        System.out.println(moves.toString());
        for(int m: moves){
            game.chess.make_move(m);
            Utils.view_board(game.chess.board);
            game.chess.unmake_move();
            //Utils.view_board(game.chess.board);

        }
    }

}
