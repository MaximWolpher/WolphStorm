import java.math.BigInteger;
import java.util.*;

/**
 * Created by maxim on 04/03/2017.
 */
public class ChessBoard {

    public long[][] board = {{0L,0L,0L,0L,0L,0L}, {0L,0L,0L,0L,0L,0L}};
    public long white_pieces;
    public long black_pieces;

    public void setMagics(Magics magics) {
        this.magics = magics;
    }

    private Magics magics;

    public void setMoves(Moves moves) {
        this.moves = moves;
    }

    private Moves moves;


    public int turn = 1; // 1 white
    public int castles = 0;// 15 all castles allowed (wq, wk, bq, bk)
    public int EP = 0;
    public int hmc = 0; //half move clock

    public int game_over = -1; //{-1 = not over, 1 = white win, 0 = black win, 2 = draw}
    private ArrayList<Previous_Moves> previous_states = new ArrayList<>();

    private void update_pieces(){
        this.black_pieces = this.board[0][0]|this.board[0][1]|this.board[0][2]|this.board[0][3]|
                this.board[0][4]|this.board[0][5];
        this.white_pieces = this.board[1][0]|this.board[1][1]|this.board[1][2]|this.board[1][3]|
                this.board[1][4]|this.board[1][5];

    }

    public void changeTurn(){
        this.turn ^= 1;
    }

    public ArrayList<Integer> generate_moves(){
        return this.moves.generate_moves(this, this.magics);
    }


    public boolean make_move(int move){
        int from = (move >>> 16) & 0x3f;
        int to = (move >>> 10) & 0x3f;
        int type_from = (move >>> 7) & 7;
        int type_to = (move >>> 4) & 7;
        int special = move & 0xf;

        int new_cast = this.castles;
        int new_ep = this.EP;
        int new_hmc = this.hmc;
        previous_states.add(new Previous_Moves(move, new_cast, new_ep, new_hmc, type_to));

        update_moves(from, to, type_from, type_to, special);

        if(type_from==0 || (special&4)==4){
            this.hmc=0;
        }
        else {
            this.hmc++;
        }
        update_EP(move);
        update_pieces();
        update_castles(this.turn, type_from, from, special, type_to, to);

        boolean legal = isNotInCheck();

        this.turn^=1;

        return legal;
    }

    public void unmake_move(){
        Previous_Moves prev = this.previous_states.get(this.previous_states.size()-1);
        int move = prev.move_prev;
        int from = (move >>> 16) & 0x3f;
        int to = (move >>> 10) & 0x3f;
        int type_from = (move >>> 7) & 7;
        int type_to = (move >>> 4) & 7;
        int special = move & 0xf;

        this.castles = prev.castle_prev;
        this.EP = prev.EP_prev;
        this.hmc = prev.hmc_prev;

        this.turn^=1;

        update_moves(from, to, type_from, type_to, special);

        previous_states.remove(previous_states.size()-1);
        update_pieces();
    }

    private void update_moves(int from, int to, int type_from, int type_to, int special){
        if(special<=1){ // Quiet move
            this.board[this.turn][type_from] ^= (1L<<from)|(1L<<to);
        }
        else if(special==4){ // Capture
            update_capture(from, to, type_from, type_to);

        }
        else if(special==3){ // Queen castle
            update_queen_castle(from);

        }
        else if(special==2){ // King castle
            update_king_castle(from);

        }
        else if(special==5){ // En Passant
            update_EP_move(from, to, type_from);

        }
        else if(special > 5){ // Promo
            update_promo(from, to, type_from, type_to, special);
        }
    }

    private void update_promo(int from, int to, int type_from, int type_to, int special){
        this.board[this.turn][type_from] ^= (1L<<from);
        this.board[this.turn][((special&3)+1)] ^= (1L<<to);

        if((special&4)==4){ // Promo Capture
            this.board[this.turn^1][type_to] ^= (1L << to);
        }
    }

    private void update_EP_move(int from, int to, int type_from){
        this.board[this.turn][type_from] ^= (1L<<from)|(1L<<(to));
        long ep_pawn = (Constants.FILE_MASKS[to%8]& Constants.RANK_MASKS[from/8]); // TODO speed test &7 vs %8
        this.board[this.turn^1][0] ^= ep_pawn;
    }

    private void update_king_castle(int from){
        this.board[this.turn][5] ^= (1L<<from)|(1L<<(from-2));
        this.board[this.turn][3] ^= (1L<<(from-3))|(1L<<(from-1));
    }

    private void update_queen_castle(int from){
        this.board[this.turn][5] ^= (1L<<from)|(1L<<(from+2));
        this.board[this.turn][3] ^= (1L<<(from+4))|(1L<<(from+1));
    }

    private void update_capture(int from, int to, int type_from, int type_to){
        this.board[this.turn][type_from] ^= (1L<<from)|(1L<<to);
        this.board[this.turn^1][type_to] ^= (1L<<to);
    }

    private void update_castles(int turn, int type_from, int from, int special, int type_to, int to){
        int side = turn == 0 ? 3 : 12;
        int opp_side = turn == 0 ? 12 : 3;
        if((this.castles & side) != 0) {
            if (type_from == 5 || special == 2 || special == 3) {
                // Remove castle rights for side to move if king move or castle-move
                this.castles &= opp_side;
            }
            if (type_from == 3) {
                this.castles &= castle_keep(from);
            }
        }
        if(((this.castles & opp_side) != 0) & ((special & 4) != 0) & (type_to == 3)){
            this.castles &= castle_keep(to);
        }
    }

    private int castle_keep(int square){
        int keep = 15;
        switch (square) {
            // Rook starting squares
            case 0:
                keep = 7;
                break;
            case 7:
                keep = 11;
                break;
            case 56:
                keep = 13;
                break;
            case 63:
                keep = 14;
                break;
        }
        return keep;
    }


    public void update_EP(int move) {
        int from = (move >>> 16) & 0x3f;
        int special = move & 0xf;
        if (special == 1) {
            this.EP = (from & 7) + 1;
        } else {
            this.EP = 0;
        }
    }

    public boolean isNotInCheck(){
        int king_square = Utils.pop_1st_bit(this.board[this.turn][5]);
        if((this.moves.Knight_Move_List[king_square] & this.board[this.turn^1][1]) != 0){
            return false;
        }

        // King Needed?
        if((this.moves.King_Move_List[king_square] & this.board[this.turn^1][5]) != 0){
            return false;
        }

        long[] pawn_attacks = (this.turn == 0)? this.moves.BlackPawn_Attack_List: this.moves.WhitePawn_Attack_List;
        if((pawn_attacks[king_square] & this.board[this.turn^1][0]) != 0){
            return false;
        }

        // Sliding attacks

        long rook_attacks;
        long occupied = this.white_pieces | this.black_pieces;


        rook_attacks = Bitboards.Sliding_Attacks(occupied, king_square, this.magics.rook_magics);
        if((rook_attacks & this.board[this.turn^1][3]) != 0){
            return false;
        }

        long bishop_attacks;
        bishop_attacks = Bitboards.Sliding_Attacks(occupied, king_square, this.magics.bishop_magics);
        if((bishop_attacks & this.board[this.turn^1][2]) != 0){
            return false;
        }

        long queen_attacks;
        queen_attacks = rook_attacks | bishop_attacks;
        return (queen_attacks & this.board[this.turn^1][4]) == 0;
    }


    public static String parse_move(int move){
        String[] promos = {"n","b","r","q"};
        String[] ranks = {"a","b","c","d","e","f","g","h"};
        String parsed_move = "";
        int from = (move >>> 16) & 0x3f;
        int to = (move >>> 10) & 0x3f;
        int type_from = (move >>> 7) & 7;
        int type_to = (move >>> 4) & 7;
        int special = move & 0xf;
        //From
        parsed_move+=ranks[ranks.length - (1 + (from&7))];
        parsed_move+=Integer.toString(1 + (from>>3));
        //To
        parsed_move+=ranks[ranks.length - (1 + (to & 7))];
        parsed_move+=Integer.toString(1 + (to>>3));
        //Promo
        if(special>5){
            int promo_piece = (special&3);
            parsed_move+=promos[promo_piece];
        }
        else if(special == 2){
            // Short Castle zero or O??
            parsed_move = "O-O";
        }
        else if(special == 3){
            // Long Castle
            parsed_move = "O-O-O";
        }
        return parsed_move;

    }
    public static int parse_alg(String str_move){
        String promos = "nbrq";
        String files = "abcdefgh";
        int move = 0;
        int from_f = files.indexOf(str_move.charAt(0));
        int from_r = Character.getNumericValue(str_move.charAt(1));
        int from = (8-from_r)*8+from_f;
        int to_f =  files.indexOf(str_move.charAt(2));
        int to_r = Character.getNumericValue(str_move.charAt(3));
        int to = (8-to_r)*8+to_f;

        move |= (from<<18);
        move |= (to<<12);
        if(str_move.length()==5){
            move |= (8+promos.indexOf(str_move.charAt(4)));
        }
        return move;
    }


    public String bitboard_to_fen() {

        String chessBoard[][]=new String[8][8];
        for (int i=0;i<64;i++) {
            chessBoard[i/8][i%8]=" ";
        }
        for (int i=0;i<8*8;i++) {
            if (((board[0][0]>>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
            if (((board[0][1]>>>i)&1)==1) {chessBoard[i/8][i%8]="N";}
            if (((board[0][2]>>>i)&1)==1) {chessBoard[i/8][i%8]="B";}
            if (((board[0][3]>>>i)&1)==1) {chessBoard[i/8][i%8]="R";}
            if (((board[0][4]>>>i)&1)==1) {chessBoard[i/8][i%8]="Q";}
            if (((board[0][5]>>>i)&1)==1) {chessBoard[i/8][i%8]="K";}
            if (((board[1][0]>>>i)&1)==1) {chessBoard[i/8][i%8]="p";}
            if (((board[1][1]>>>i)&1)==1) {chessBoard[i/8][i%8]="n";}
            if (((board[1][2]>>>i)&1)==1) {chessBoard[i/8][i%8]="b";}
            if (((board[1][3]>>>i)&1)==1) {chessBoard[i/8][i%8]="r";}
            if (((board[1][4]>>>i)&1)==1) {chessBoard[i/8][i%8]="q";}
            if (((board[1][5]>>>i)&1)==1) {chessBoard[i/8][i%8]="k";}
        }

        String FEN = "";
        for (int i=0;i<8;i++) {
            int counter = 0;
            for (int j = 0;j<8;j++){

                if (!chessBoard[i][j].equals(" ")){
                    if(counter>0){FEN+=counter+chessBoard[i][j];}
                    else{FEN+=chessBoard[i][j];}
                    counter = 0;}
                else {counter++;}
                if(j==7){
                    if(counter>0){FEN+=counter;}
                    if(i!=7){FEN+="/";}
                }}
        }
        if(this.turn == 1){FEN += " w ";}
        else{FEN+= " b ";}

        if (castles!=0) {
            if ((castles & 8) == 8) {
                FEN += "K";
            }
            if ((castles & 4) == 4) {
                FEN += "Q";
            }
            if ((castles & 2) == 2) {
                FEN += "k";
            }
            if ((castles & 1) == 1) {
                FEN += "q";
            }
            FEN+=" ";
        }
        else{
            FEN += "- ";
        }

        // En passant
        if(EP>0){
            int ascii = 96;
            FEN += (char)(ascii+EP);
            if(this.turn==1){
                FEN+="6";
            }
            else{
                FEN+="3";
            }
        }
        else{FEN += "-";}

        //TODO move counters
        FEN += " 0 1";
        return FEN;
    }

    public void fen_to_board(String fen){
        String[] fen_array = fen.split(" ");
        String[] pieces = {"pnbrqk", "PNBRQK"};
        int[] sides = {1, 0};
        String bit_string;
        char[] fen_position = fen_array[0].replace("/","").toCharArray();
        for(int side: sides) {
            for (char piece : pieces[side].toCharArray()) {
                bit_string = "";
                for (char character : fen_position) {
                    if (character == piece) {
                        bit_string += '1';
                    } else if (Character.isDigit(character)) {
                        for (int x = 0; x < Character.getNumericValue(character); x++) {
                            bit_string += '0';
                        }
                    } else {
                        bit_string += '0';
                    }
                }
                this.board[side][pieces[side].indexOf(piece)] = new BigInteger(bit_string, 2).longValue();
            }
        }
        fen_to_turn(fen);
        fen_to_castle(fen);
        fen_to_ep(fen);
        update_pieces();
    }

    public void fen_to_turn(String fen){
        String[] fen_array = fen.split(" ");
        if(fen_array[1].equals("w")){
            this.turn=1;
        }
        else {this.turn=0;}
    }

    public void fen_to_castle(String fen){
        String[] fen_array = fen.split(" ");
        String s = fen_array[2];
        if(s.equals("-")){
            this.castles = 0;
        }
        else {
            int castle = 0;
            for(int i = 0;i < s.length();i++){
                char c = s.charAt(i);
                if(c == 'K'){castle^=1<<3;}
                if(c == 'Q'){castle^=1<<2;}
                if(c == 'k'){castle^=1<<1;}
                if(c == 'q'){castle^=1;}
            }
            this.castles = castle;
        }

    }
    public void fen_to_ep(String fen){
        String[] fen_array = fen.split(" ");
        String s = fen_array[3];
        if(s.equals("-")){
            this.EP = 0;
        }
        else{
            char c = s.charAt(0);
            this.EP = (8-((int)c-96))+1;
        }

    }

}
