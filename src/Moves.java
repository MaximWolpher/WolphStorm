import java.util.ArrayList;

public class Moves {

    long[] King_Move_List = new long[64];
    long[] Knight_Move_List = new long[64];
    long[] WhitePawn_Move_List = new long[64];
    long[] BlackPawn_Move_List = new long[64];
    long[] WhitePawn_Attack_List = new long[64];
    long[] BlackPawn_Attack_List = new long[64];

    void init_static_moves(){
        for(int square=0; square<64; square++){
            this.King_Move_List[square] = Bitboards.King_Moves(square);
            this.Knight_Move_List[square] = Bitboards.Knight_Moves(square);
            this.WhitePawn_Move_List[square] = Bitboards.White_Pawn_Move(square);
            this.BlackPawn_Move_List[square] = Bitboards.Black_Pawn_Move(square);
            this.WhitePawn_Attack_List[square] = Bitboards.White_Pawn_Attack(square);
            this.BlackPawn_Attack_List[square] = Bitboards.Black_Pawn_Attack(square);
        }
    }

    ArrayList<Integer> generate_moves(ChessBoard chess, Magics magics){
        long[] pieces = {chess.black_pieces, chess.white_pieces};
        long occupied = pieces[0] | pieces[1];
        long enemy_pieces = pieces[chess.turn^1];
        long not_my_pieces = ~pieces[chess.turn];
        ArrayList<Integer> all_moves = new ArrayList<>();

        all_moves.addAll(EP_move(chess.EP, chess.turn, chess.board, this.WhitePawn_Attack_List, this.BlackPawn_Attack_List, enemy_pieces));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][5], this.King_Move_List, 5, enemy_pieces, not_my_pieces, occupied, magics));
        all_moves.addAll(Castle_Move(occupied, chess.turn, chess.castles));
        if(chess.turn == 1){
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn, this.WhitePawn_Attack_List, enemy_pieces, occupied, true));
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn,  this.WhitePawn_Move_List, enemy_pieces, occupied, false));
        }
        else {
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn, this.BlackPawn_Attack_List, enemy_pieces, occupied, true));
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn, this.BlackPawn_Move_List, enemy_pieces, occupied, false));
        }

        all_moves.addAll(pseudo_moves(chess.board[chess.turn][1], this.Knight_Move_List, 1, enemy_pieces, not_my_pieces, occupied, magics));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][2], new long[0], 2, enemy_pieces, not_my_pieces, occupied, magics));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][3], new long[0], 3, enemy_pieces, not_my_pieces, occupied, magics));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][4], new long[0], 4, enemy_pieces, not_my_pieces, occupied, magics));

        return all_moves;
    }

    ArrayList<Integer> pseudo_moves(
            long bitboard,
            long[] move_bitboards,
            int type,
            long enemy_pieces,
            long not_my_pieces,
            long occupied,
            Magics magics
    ){
        int from, to;
        long pseudo_legals;
        ArrayList<Integer> moves = new ArrayList<>();
        while(bitboard != 0L){
            from = Utils.pop_1st_bit(bitboard);
            bitboard ^= (1L << from);
            if((type == 3) | (type == 2)){
                MagicObject[] magicObjects = type == 2? magics.bishop_magics : magics.rook_magics;
                pseudo_legals = Bitboards.Sliding_Attacks(occupied, from, magicObjects);
            }
            else if(type == 4){
                pseudo_legals = Bitboards.Sliding_Attacks(occupied, from, magics.bishop_magics)
                        | Bitboards.Sliding_Attacks(occupied, from, magics.rook_magics);
            }
            else {
                pseudo_legals = move_bitboards[from];
            }
            pseudo_legals &= not_my_pieces;
            while(pseudo_legals != 0L){
                to = Utils.pop_1st_bit(pseudo_legals);
                pseudo_legals ^= (1L << to);
                moves.add(move_integer(enemy_pieces, from, to, type, 0));
            }
        }
        return moves;
    }

    ArrayList<Integer> pawn_moves(
            long bitboard,
            int turn,
            long[] move_bitboards,
            long enemy_pieces,
            long occupied,
            boolean attack
    ){
        int from, to;
        int special;
        long pseudo_legals;
        long double_jump_rank;
        int end_rank;
        ArrayList<Integer> moves = new ArrayList<>();
        while(bitboard != 0){
            from = Utils.pop_1st_bit(bitboard);
            bitboard ^= (1L << from);
            pseudo_legals = move_bitboards[from];
            if(attack){
                pseudo_legals &= enemy_pieces;
            }
            else{
                pseudo_legals &= ~occupied;
            }

            while(pseudo_legals != 0){
                to = Utils.pop_1st_bit(pseudo_legals);
                pseudo_legals ^= (1L << to);
                end_rank = (turn == 0) ? 0: 7;
                if(Math.abs(from-to) == 16){
                    double_jump_rank = (turn == 0) ?
                            (1L << (from - 8)) & 0xff0000000000L : (1L << (from + 8)) & 0xff0000;
                    if((double_jump_rank & occupied) != 0){
                        continue;
                    }
                    special = 1;
                    moves.add(move_integer(enemy_pieces, from, to, 0, special));

                }
                else if((to >> 3) == end_rank){
                    for(int prom = 0; prom < 4; prom++){
                        special = 8 + prom;
                        moves.add(move_integer(enemy_pieces, from, to, 0, special));
                    }
                }
                else {
                    moves.add(move_integer(enemy_pieces, from, to, 0, 0));
                }

            }
        }
        return moves;
    }

    public ArrayList<Integer> EP_move(int ep_square, int turn, long[][] board,
                                      long[] white_pawn_attacks, long[] black_pawn_attacks,
                                      long enemy_pieces){
        long captures;
        int loc;
        ArrayList<Integer> moves = new ArrayList<>();
        if(ep_square == 0){
            return moves;
        }
        ep_square = (turn == 0)? (8 - ep_square) + 0x10 : (8-ep_square) + 0x28;
        captures = (turn == 0)?
                white_pawn_attacks[ep_square] & board[turn][0]: black_pawn_attacks[ep_square] & board[turn][0];
        while(captures != 0){
            // One or two captures possible

            loc = Utils.pop_1st_bit(captures);
            captures ^= 1L<<loc;
            moves.add(move_integer(enemy_pieces, loc, ep_square,0, 5)); // pawn with EP capture
        }
        return moves;

    }

    public ArrayList<Integer> Castle_Move(long occupied, int turn, int castles){
        ArrayList<Integer> moves = new ArrayList<>();
        int loc = 59; // Black king
        if (turn == 1){
            // White castle rights
            loc = 3; // Location of white king
            castles = castles >>> 2;
        }

        int castle_right = castles & 3;

        if(castle_right == 0){
            return moves; // No castle moves
        }

        if((castle_right & 1) == 1){    // Queen castle
            if((7<<(loc+1) & occupied) == 0){
                // If there is no blocker between king and queen-side rook
                moves.add(3); // Encoding for queen castle
            }
        }
        if((castle_right & 2) == 2){    // King castle
            if((3 << (loc - 2) & occupied) == 0){
                // If there is no blocker between king and king-side rook
                moves.add(2); // Encoding for king castle
            }
        }
        return moves;
    }

    public static int move_integer(long enemy_pieces, int from, int to, int type, int special){
        /**
         * 6 bits FROM
         * 6 bits TO
         * 3 bits FROM_TYPE
         * 3 bits TO_TYPE
         * 4 bits SPECIAL
         * FROM-TO-FROM_TYPE-TO_TYPE-SPECIAL
         */
        int move_int = 0;
        move_int |= (from<<16);
        move_int |= (to<<10);
        move_int |= (type<<7);
        if((enemy_pieces & (1L << to)) != 0) {
            move_int |= 4; // Capture
        }
        move_int |= special;
        return move_int;
    }


}
