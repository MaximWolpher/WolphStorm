
public class Rating {
    public static int piece_diff(ChessBoard chess) {
        int score =
                Long.bitCount(chess.board[1][0])*150+
                Long.bitCount(chess.board[1][1])*620+
                Long.bitCount(chess.board[1][2])*680+
                Long.bitCount(chess.board[1][3])*1200+
                Long.bitCount(chess.board[1][4])*2500-
                Long.bitCount(chess.board[0][0])*150-
                Long.bitCount(chess.board[0][1])*620-
                Long.bitCount(chess.board[0][2])*680-
                Long.bitCount(chess.board[0][3])*1200-
                Long.bitCount(chess.board[0][4])*2500;
        return score;
    }
    public static int piece_position(ChessBoard chess, boolean end_game){
        int score = 0;
        int[] piece_square;
        int single;
        long board_t;
        int[] king_w = Constants.KingTable_W;
        if(end_game){
            king_w = Constants.KingTableEndGame_W;
        }
        int[][] tables = {Constants.PawnTable_W,Constants.KnightTable_W,Constants.BishopTable_W,Constants.RookTable_W,
                new int[0],king_w}; // TODO king_W or new int

        for(int side = 0;side<2;side++){
            for(int i = 0;i<6;i++){
                if(i == 4 ){continue;}
                piece_square = tables[i];
                board_t = chess.board[side][i];
                while(board_t != 0) {
                    single = Utils.pop_1st_bit(board_t);
                    board_t ^= (1L << single);
                    single = 63 - single;
                    single = side == 0 ? single + (7 - (single / 8) * 2) * 8 : single; // faster than bitshift
                    // Side to move is opponent
                    score = chess.turn == side ? score - piece_square[single] : score + piece_square[single];
                }
            }
        }
        return score;
    }

    public static int pawn_eval(long board[]){
        return 0;
    }

    public static int castle_eval(ChessBoard chess){
        int cast_right = chess.castles;
        int score = 0;
        if ((cast_right&8)==8){score+=100;}
        if ((cast_right&4)==4){score+=100;}
        if ((cast_right&2)==2){score-=100;}
        if ((cast_right&1)==1){score-=100;}
        return score;
    }


    public static int eval_func(ChessBoard chess){
        int eval_score = 0;
        eval_score += piece_diff(chess);
        eval_score += piece_position(chess,false);
        //eval_score += is_check(chess);
        //eval_score += castle_eval(chess);
        return eval_score * (2 * chess.turn - 1);
    }

}