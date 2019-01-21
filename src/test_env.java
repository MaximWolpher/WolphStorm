
public class test_env {

    public static void main(String[] args) {
        int to = 18;
        int from = 27;
        Utils.view_bitboard((Constants.FILE_MASKS[to % 8] & Constants.RANK_MASKS[from / 8])|(1L<<to)|(1L<<from));
        System.out.println("");
        Utils.view_bitboard((1L<<to)|(1L<<from));
    }
}


