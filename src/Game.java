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
        this.chess.fen_to_board("rn2k2r/pppp1ppp/4pn2/2K3q1/5P2/1PN1PN2/P1PP2PP/R4B1R w kq - 0 1");
        Utils.view_board(this.chess.board);
        this.magics.generate_magics();
        this.chess.magics = this.magics;
        this.moves.init_static_moves();
        chess.turn^=1;
        moves.generate_moves(chess, magics);
        chess.turn^=1;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.initiate();
        boolean legal;

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
            legal = game.chess.make_move(m);
            if(!legal){
                game.chess.unmake_move();
            }
            else {
                Utils.view_board(game.chess.board);
                game.chess.unmake_move();
                //Utils.view_board(game.chess.board);
            }

        }
    }

}
