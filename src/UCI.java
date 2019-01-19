import java.util.*;
public class UCI {
    static String ENGINENAME="WolphStorm";
    static String AUTHOR="Maxim Wolpher";
    public static void uciCommunication() {
        Scanner input = new Scanner(System.in);
        ChessBoard chess = new ChessBoard();
        boolean new_game_flag = true;
        while (true)
        {
            String inputString=input.nextLine();
            if ("uci".equals(inputString))
            {
                inputUCI();
            }
            else if (inputString.startsWith("setoption"))
            {
                inputSetOption(inputString);
            }
            else if ("isready".equals(inputString))
            {
                inputIsReady();
            }
            else if ("ucinewgame".equals(inputString))
            {
                inputUCINewGame(chess);

            }
            else if (inputString.startsWith("position") && new_game_flag)
            {
                inputPosition(inputString,chess);
                new_game_flag = false;
            }
            else if (inputString.startsWith("go"))
            {
                //inputGo(chess_ai, chess);
            }
            else if (inputString.equals("quit"))
            {
                inputQuit();
            }
            else if ("print".equals(inputString))
            {
                inputPrint();
            }
            else if(inputString.equals("quit")){
                break;
            }
        }
    }
    public static void inputUCI() {
        System.out.println("id name "+ENGINENAME);
        System.out.println("id author "+AUTHOR);
        //options go here
        System.out.println("uciok");
    }
    public static void inputSetOption(String inputString) {
        //set options
    }
    public static void inputIsReady() {
        System.out.println("readyok");
    }
    public static void inputUCINewGame(ChessBoard chess) {
        chess.fen_to_board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }
    public static void inputPosition(String input, ChessBoard chess) {
        String[] string_parts = input.split(" ");
        if (string_parts[1].equals("startpos")) {
            chess.fen_to_board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            //chess.fen_to_board("2rr3k/pp3pp1/1nnqbN1p/3pN3/2pP4/2P3Q1/PPB4P/R4RK1 w - - 0 1");
        }


    }


    public static void inputQuit() {
        System.exit(0);
    }
    public static void inputPrint() {
    }
}