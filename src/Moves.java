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
        occupied ^= chess.board[chess.turn^1][5]; // TODO this could cause trouble
        long enemy_pieces = pieces[chess.turn^1];
        long not_my_pieces = ~pieces[chess.turn];

        ArrayList<Integer> all_moves = new ArrayList<>();
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][4], null, 4, enemy_pieces, not_my_pieces, occupied, magics, chess.board, chess.turn));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][3], null, 3, enemy_pieces, not_my_pieces, occupied, magics, chess.board, chess.turn));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][2], null, 2, enemy_pieces, not_my_pieces, occupied, magics, chess.board, chess.turn));
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][1], this.Knight_Move_List, 1, enemy_pieces, not_my_pieces, occupied, magics, chess.board, chess.turn));

        if(chess.turn == 1){
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn, this.WhitePawn_Attack_List, enemy_pieces, occupied, true, chess.board));
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn,  this.WhitePawn_Move_List, enemy_pieces, occupied, false, chess.board));
        }
        else {
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn, this.BlackPawn_Attack_List, enemy_pieces, occupied, true, chess.board));
            all_moves.addAll(pawn_moves(chess.board[chess.turn][0], chess.turn, this.BlackPawn_Move_List, enemy_pieces, occupied, false, chess.board));
        }
        all_moves.addAll(pseudo_moves(chess.board[chess.turn][5], this.King_Move_List, 5, enemy_pieces, not_my_pieces, occupied, magics, chess.board, chess.turn));
        all_moves.addAll(Castle_Move(occupied, chess.turn, chess.castles, chess.board, magics));
        all_moves.addAll(EP_move(chess.EP, chess.turn, chess.board, this.WhitePawn_Attack_List, this.BlackPawn_Attack_List, enemy_pieces));

        return all_moves;
    }



    ArrayList<Integer> pseudo_moves(
            long bitboard,
            long[] move_bitboards,
            int type,
            long enemy_pieces,
            long not_my_pieces,
            long occupied,
            Magics magics,
            long[][] board,
            int turn
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
                moves.add(move_integer(enemy_pieces, from, to, type, 0, board, turn));
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
            boolean attack,
            long[][] board
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
                occupied |= board[turn^1][5];
                pseudo_legals &= ~occupied;
            }

            while(pseudo_legals != 0){
                to = Utils.pop_1st_bit(pseudo_legals);
                pseudo_legals ^= (1L << to);
                end_rank = (turn == 0) ? 0: 7;
                if(Math.abs(from-to) == 16){
                    // Double jump from start
                    double_jump_rank = (turn == 0) ?
                            (1L << (from - 8)) & 0xff0000000000L : (1L << (from + 8)) & 0xff0000;
                    if((double_jump_rank & occupied) != 0){
                        continue;
                    }
                    special = 1;
                    moves.add(move_integer(enemy_pieces, from, to, 0, special, board, turn));
                }
                else if((to >> 3) == end_rank){
                    // Promotion
                    for(int prom = 0; prom < 4; prom++){
                        special = 8 + prom;
                        moves.add(move_integer(enemy_pieces, from, to, 0, special, board, turn));
                    }
                }
                else {
                    // Quiet move
                    moves.add(move_integer(enemy_pieces, from, to, 0, 0, board, turn));
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
        ep_square = (turn == 0)? ep_square + 0xf : ep_square + 0x27;
        captures = (turn == 0)?
                white_pawn_attacks[ep_square] & board[turn][0]: black_pawn_attacks[ep_square] & board[turn][0];

        while(captures != 0){
            // One or two captures possible
            loc = Utils.pop_1st_bit(captures);
            captures ^= 1L << loc;
            moves.add(move_integer(enemy_pieces, loc, ep_square,0, 5, board, turn)); // pawn with EP capture
        }
        return moves;
    }

    public ArrayList<Integer> Castle_Move(long occupied, int turn, int castles, long[][] board, Magics magics){
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
            if(((7L << (loc + 1)) & occupied) == 0 && isNotInCheck((7L << loc),occupied,turn,board,magics)){
                // If there is no blocker between king and queen-side rook
                moves.add(move_integer(0L, loc,0,5,3, null, turn)); // Encoding for queen castle
            }
        }
        if((castle_right & 2) == 2){    // King castle
            if(((3L << (loc - 2)) & occupied) == 0 && isNotInCheck((7L << (loc - 2)),occupied,turn,board,magics)){
                // If there is no blocker between king and king-side rook
                moves.add(move_integer(0L, loc, 0, 5, 2, null, turn)); // Encoding for king castle
            }
        }
        return moves;
    }

    public boolean isNotInCheck(long squares, long occupied, int turn, long[][] board, Magics magics){
        int single;
        while(squares != 0L){
            single = Utils.pop_1st_bit(squares);
            squares ^= (1L << single);

            if((this.Knight_Move_List[single] & board[turn^1][1]) != 0){
                return false;
            }

            // King Needed?
            if((this.King_Move_List[single] & board[turn^1][5]) != 0){
                return false;
            }

            long[] pawn_attacks = (turn == 0)? this.BlackPawn_Attack_List: this.WhitePawn_Attack_List;
            if((pawn_attacks[single] & board[turn^1][0]) != 0){
                return false;
            }

            // Sliding attacks

            long rook_attacks;

            rook_attacks = Bitboards.Sliding_Attacks(occupied, single, magics.rook_magics);
            if((rook_attacks & board[turn^1][3]) != 0){
                return false;
            }

            long bishop_attacks;
            bishop_attacks = Bitboards.Sliding_Attacks(occupied, single, magics.bishop_magics);
            if((bishop_attacks & board[turn^1][2]) != 0){
                return false;
            }

            long queen_attacks;
            queen_attacks = rook_attacks | bishop_attacks;
            if((queen_attacks & board[turn^1][4]) != 0){
                return false;
            }

        }
        return true;

    }

    public static int move_integer(
            long enemy_pieces,
            int from,
            int to,
            int type,
            int special,
            long[][] board,
            int turn){
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
            boolean found = false;
            for(int piece=0; piece<6; piece++){
                if((board[turn^1][piece] & (1L << to)) != 0){
                    move_int |= (piece << 4);
                    found = true;
                    break;
                }
            }
            if(!found){
                System.err.println("No capture piece");
            }
            move_int |= 4; // Capture
        }
        move_int |= special;
        return move_int;
    }

    public ArrayList<Integer> order_moves(ArrayList<Integer> moves, int pv){
        moves.sort((Integer m1, Integer m2) -> -(((m1 & 4)*(1+((m1 >>> 4) & 7))) - ((m2 & 4)*(1+(m2 >>> 4) & 7))));
        if(pv != 0){
            int ind = moves.indexOf(pv);
            if (ind != -1) {
                moves.remove(ind);
                moves.add(0, pv);
            }
        }
        return moves;
    }

}
