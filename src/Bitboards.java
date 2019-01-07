public class Bitboards {



    // These functions are for pre-computing the moves from each square for each non-sliding piece
    public static long King_Moves(int pos){
        long king;
        if((pos & 7) == 0){
            if(pos < 9){
                king = (Constants.KING_SPAN >>> (9 - pos)) & ~Constants.FILE_MASKS[7];
            }
            else{
                king = (Constants.KING_SPAN << (pos - 9)) & ~Constants.FILE_MASKS[7];
            }
        }
        else if((pos & 7) == 7){
            if(pos < 9){
                king = (Constants.KING_SPAN >>> (9 - pos)) & ~Constants.FILE_MASKS[0];
            }
            else{
                king = (Constants.KING_SPAN << (pos - 9)) & ~Constants.FILE_MASKS[0];
            }
        }
        else{
            if(pos < 9){
                king = (Constants.KING_SPAN >>> (9 - pos));
            }
            else{
                king = (Constants.KING_SPAN << (pos - 9));
            }
        }

        king &= Constants.FULL_BOARD;

        return king;
    }

    public static long Knight_Moves(int pos){
        long knight;
        if((pos & 7) <= 1){
            if(pos < 18){
                knight = (Constants.KNIGHT_SPAN >>> (18 - pos)) & ~(Constants.FILE_MASKS[7] | Constants.FILE_MASKS[6]);
            }
            else{
                knight = (Constants.KNIGHT_SPAN << (pos - 18)) & ~(Constants.FILE_MASKS[7] | Constants.FILE_MASKS[6]);
            }
        }
        else if((pos & 7) == 7 || (pos & 7) == 6){
            if(pos < 18){
                knight = (Constants.KNIGHT_SPAN >>> (18 - pos)) & ~(Constants.FILE_MASKS[0] | Constants.FILE_MASKS[1]);
            }
            else{
                knight = (Constants.KNIGHT_SPAN << (pos - 18)) & ~(Constants.FILE_MASKS[0] | Constants.FILE_MASKS[1]);
            }
        }
        else{
            if(pos < 18){
                knight = (Constants.KNIGHT_SPAN >>> (18 - pos));
            }
            else{
                knight = (Constants.KNIGHT_SPAN << (pos - 18));
            }
        }

        knight &= Constants.FULL_BOARD;

        return knight;
    }

    public static long White_Pawn_Attack (int pos)
    {
        assert 7 < pos;
        assert 56 > pos;
        long pawn = 0;
        long pos_sq = 1L << pos;

        if((pos & 7) == 0){
            pawn |= (pos_sq << 7) & ~Constants.FILE_MASKS[7];
        }

        else{
            pawn |= (pos_sq << 7);
        }

        if((pos & 7) == 7){

            pawn |= (pos_sq << 9) & ~Constants.FILE_MASKS[0];
        }
        else{
            pawn |= (pos_sq << 9);
        }

        if(pos == 55){
            pawn &= ~(Constants.RANK_MASKS[7] << 8);
        }
        return pawn;
    }


    public static long Black_Pawn_Attack (int pos)
    {
        assert 7 < pos;
        assert 56 > pos;
        long pawn = 0;
        long pos_sq = 1L << pos;
        if((pos & 7) == 7){
            pawn |= ((pos_sq >>> 7) & ~Constants.FILE_MASKS[0]);
        }
        else{
            pawn |= (pos_sq >>> 7);
        }
        if((pos & 7) == 0){
            pawn |= ((pos_sq >>> 9) & ~Constants.FILE_MASKS[7]);
        }
        else{
            pawn |= (pos_sq >>> 9);
        }

        return pawn;
    }

    public static long White_Pawn_Move (int pos)
    {
        assert 7 < pos;
        assert 56 > pos;

        long pawn = 0;
        long pos_sq = 1L << pos;
        if(pos >>> 3 == 1){
            pawn |= pos_sq << 16;
        }
        pawn |= pos_sq << 8;
        return pawn;
    }

    public static long Black_Pawn_Move (int pos)
    {
        assert 7 < pos;
        assert 56 > pos;
        long pawn = 0L;
        long pos_sq = 1L << pos;
        if(pos >>> 3 == 6){
            pawn |= pos_sq >>> 16;
        }
        pawn |= pos_sq >>> 8;
        return pawn;
    }

    public static long Sliding_Attacks(long occ, int square, MagicObject[] magics)
    {
        long block = occ & magics[square].mask;
        long magic = magics[square].magic;
        int shift = magics[square].shift;

        int index = Magics.transform(block, magic, shift);

        return magics[square].attacks[index];
    }


}
