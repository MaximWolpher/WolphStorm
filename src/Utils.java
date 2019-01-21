import java.util.Arrays;

public class Utils {
    public static void view_bitboard(long bitboard)
    {
        String board = Long.toUnsignedString(bitboard,2);
        char[][] str_board = new char[8][8];
        for (int i=0;i<64;i++) {
            str_board[i >>> 3][i & 7] = ' ';
        }
        for(int i=0; i<board.length(); i++){
            char bit = board.charAt(board.length()-(1+i));
            if(Character.getNumericValue(bit) == 1) {
                str_board[7-(i >>> 3)][7-(i & 7)] = bit;
            }
        }
        for(int x=0; x<8; x++){
            System.out.println(Arrays.toString(str_board[x]));
        }
    }
    public static char[][] board_to_array(long[][] board){
        long[][] board_copy = new long[2][6];
        for(int x=0;x<2;x++){
            for(int y=0;y<6;y++){
                board_copy[x][y] = board[x][y];
            }
        }
        int loc;
        char[][] str_board = new char[8][8];
        for(int x=0; x<8; x++){
            for(int y=0; y<8; y++){
                str_board[x][y] = ' ';
            }
        }
        String[] pieces = {"pnbrqk", "PNBRQK"};
        for(int side=0; side<2;side++){
            for(int piece=0; piece<6; piece++){
                while(board_copy[side][piece]!=0) {
                    loc = Utils.pop_1st_bit(board_copy[side][piece]);
                    board_copy[side][piece] ^= (1L << loc);
                    str_board[7 - (loc >> 3)][7 - (loc & 7)] = pieces[side].charAt(piece);
                }
            }
        }
        return str_board;
    }

    public static void view_board(long[][] board){
        char[][] str_board = board_to_array(board);
        for(int x=0;x<8;x++){
            System.out.println(Arrays.toString(str_board[x]));
        }
    }

    public static int pop_1st_bit(long bb) {
        long b = bb ^ (bb - 1);
        long fold = b ^ (b >>> 32); // TODO: need the "and"?
        return Constants.BitTable[(int) (fold * 0x783a9b23) >>> 26];
    }
}
