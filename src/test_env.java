import java.util.ArrayList;

public class test_env {

    public static void main(String[] args) {
        ArrayList<Integer> moves = new ArrayList<>();
        int what = 3030303;
        moves.add(242424);
        moves.add(15151515);
        moves.add(what);
        moves.add(9797979);
        System.out.println(moves.toString());
        moves.remove(moves.indexOf(what));
        System.out.println(moves.toString());
        moves.add(0, what);
        System.out.println(moves.toString());

    }
}


