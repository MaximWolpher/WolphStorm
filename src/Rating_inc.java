/**
 * Created by maxim on 01/08/2017.
 */
public class Rating_inc {
    public int material_score = 0;
    public int position_score = 0;

    public int eval(){
        int score = 0;
        score += material_score;
        score += position_score;
        return score;
    }
}
