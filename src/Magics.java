import java.util.concurrent.ThreadLocalRandom;

public class Magics {

    MagicObject[] rook_magics = new MagicObject[64];
    MagicObject[] bishop_magics = new MagicObject[64];


    int[] RookBits = {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12
    };

    int[] BishopBits = {
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6
    };
    // Finding Magics

    public static long random_long() {
        long u1, u2, u3, u4;
        u1 = (long) (rand());
        u2 = (long) (rand());
        u3 = (long) (rand());
        u4 = (long) (rand());
        return u1 | (u2 << 16) | (u3 << 32) | (u4 << 48);
    }

    public static int rand() {
        return ThreadLocalRandom.current().nextInt(0, 1 << 16);
    }

    public static long rand_few() {
        return random_long() & random_long() & random_long();
    }




    long index_to_long(int index, int bits, long m) {
        int i, j;
        long result = 0L;
        for (i = 0; i < bits; i++) {
            j = Utils.pop_1st_bit(m);
            m ^= 1L << j;
            if ((index & (1L << i)) != 0) {
                result |= (1L << j);
            }
        }
        return result;
    }

    long remove_edge(int ran, int fil, long mask) {
        if (ran != 0) {
            mask &= ~Constants.RANK_MASKS[0];
        }
        if (ran != 7) {
            mask &= ~Constants.RANK_MASKS[7];
        }
        if (fil != 0) {
            mask &= ~Constants.FILE_MASKS[0];
        }
        if (fil != 7) {
            mask &= ~Constants.FILE_MASKS[7];
        }
        return mask;
    }

    long bishop_mask(int square) {
        int ran = square >>> 3;
        int fil = square & 7;

        long result = Constants.DIAG_MASKS[ran + fil] ^ Constants.ANTI_DIAG_MASKS[
                Constants.ANTI_DIAG_MASKS.length-(8 - ran) - fil
                ];
        result = remove_edge(ran, fil, result);
        return result;
    }

    long rook_mask(int square) {

        int ran = square >>> 3;
        int fil = square & 7;

        long result = Constants.FILE_MASKS[fil] ^ Constants.RANK_MASKS[ran];
        result = remove_edge(ran, fil, result);
        return result;
    }

    long rook_attack(int sq, long block) {
        long result = 0L;
        long m;
        int rk = sq/8, fl = sq%8, r, f;
        for(r = rk+1; r <= 7; r++) {
            m = 1L << (fl + r*8);
            result |= m;
            if((block & m) != 0) break;
        }
        for(r = rk-1; r >= 0; r--) {
            m = 1L << (fl + r*8);
            result |= m;
            if((block & m) != 0) break;
        }
        for(f = fl+1; f <= 7; f++) {
            m = 1L << (f + rk*8);
            result |= m;
            if((block & m) != 0) break;
        }
        for(f = fl-1; f >= 0; f--) {
            m = 1L << (f + rk*8);
            result |= m;
            if((block & m) != 0) break;
        }
        return result;
    }

    long bishop_attack(int sq, long block) {
        long result = 0L;
        long m;
        int rk = sq/8, fl = sq%8, r, f;
        for(r = rk+1, f = fl+1; r <= 7 && f <= 7; r++, f++) {
            m = 1L << (f + r*8);
            result |= m;
            if((block & m) != 0) break;
        }
        for(r = rk+1, f = fl-1; r <= 7 && f >= 0; r++, f--) {
            m = 1L << (f + r*8);
            result |= m;
            if((block & m) != 0) break;
        }
        for(r = rk-1, f = fl+1; r >= 0 && f <= 7; r--, f++) {
            m = 1L << (f + r*8);
            result |= m;
            if((block & m) != 0) break;
        }
        for(r = rk-1, f = fl-1; r >= 0 && f >= 0; r--, f--) {
            m = 1L << (f + r*8);
            result |= m;
            if((block & m) != 0) break;
        }
        return result;
    }
    public static int transform(long b, long magic, int bits) {
        return (int)((b * magic) >>> (64 - bits));
    }


    public MagicObject find_magic(int sq, boolean bishop) {
        int bits = bishop ? BishopBits[sq] : RookBits[sq];
        long mask;
        long[] blocked = new long[1 << bits];
        long[] attacks = new long[1 << bits];
        long[] used = new long[1 << bits];
        long magic;

        int i, j, k;
        boolean fail;

        mask = bishop ? bishop_mask(sq) : rook_mask(sq);
        for (i = 0; i < (1 << bits); i++) {
            blocked[i] = index_to_long(i, bits, mask);
            attacks[i] = bishop ? bishop_attack(sq, blocked[i]) : rook_attack(sq, blocked[i]);
        }

        for (k = 0; k < 100000000; k++) {
            magic = rand_few();
            if (Long.bitCount((mask * magic) & 0xFF00000000000000L) < 6) continue;
            for (i = 0; i < used.length; i++) used[i] = 0L;
            for (i = 0, fail = false; !fail && i < (1 << bits); i++) {
                j = transform(blocked[i], magic, bits);
                if (used[j] == 0L) used[j] = attacks[i];
                else if (used[j] != attacks[i]) fail = true;
            }
            if (!fail) {
                return new MagicObject(mask, magic, bits, used);
            }
        }
        return new MagicObject();
    }

    public void generate_magics(){
        for(int square=0; square<64; square++){
            this.rook_magics[square] = find_magic(square, false);
            this.bishop_magics[square] = find_magic(square, true);
        }
    }

}
