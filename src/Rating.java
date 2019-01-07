
public class Rating {
    public static int piece_diff(ChessBoard chess) {
        int score =
                Long.bitCount(chess.board[0][0])*150+
                Long.bitCount(chess.board[0][1])*620+
                Long.bitCount(chess.board[0][2])*680+
                Long.bitCount(chess.board[0][3])*1200+
                Long.bitCount(chess.board[0][4])*2500-
                Long.bitCount(chess.board[1][0])*150-
                Long.bitCount(chess.board[1][1])*620-
                Long.bitCount(chess.board[1][2])*680-
                Long.bitCount(chess.board[1][3])*1200-
                Long.bitCount(chess.board[1][4])*2500;
        return score*(1-(2*chess.turn));
    }
    public static int piece_position(ChessBoard chess, boolean end_game){
        int score = 0;
        int piece_square[];
        long single;
        long board_t;
        int[] king_w = Constants.KingTable_W;
        int[] king_b = Constants.KingTable_B;
        if(end_game){
            king_w = Constants.KingTableEndGame_W;
            king_b = Constants.KingTableEndGame_B;
        }
        int[][][] tables = {{Constants.PawnTable_W,Constants.KnightTable_W,Constants.BishopTable_W,Constants.RookTable_W,
                new int[0],king_w},{Constants.PawnTable_B,Constants.KnightTable_B,Constants.BishopTable_B,
                Constants.RookTable_B,new int[0],king_b}};
        for(int side = 0;side<2;side++){
            for(int i = 0;i<6;i++){
                if(i == 4 ){continue;}
                piece_square = tables[side][i];
                board_t = chess.board[side][i];
                single = board_t & ~(board_t-1);
                while(single!=0) {
                    int tz = Long.numberOfTrailingZeros(single);

                    score += piece_square[tz];
                    board_t &= ~single;
                    single = board_t&~(board_t-1);
                }
            }
        }

        return score*(1-(2*chess.turn));
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


    public static int eval_func(ChessBoard chess, boolean end_game){
        int eval_score = 0;
        eval_score += piece_diff(chess);
        //eval_score += piece_position(chess,end_game);
        //eval_score += is_check(chess);
        //eval_score += castle_eval(chess);
        return eval_score*(1-(2*chess.turn));
    }

}