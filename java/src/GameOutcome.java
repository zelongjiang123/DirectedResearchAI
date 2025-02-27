public class GameOutcome{
    double payoff_1, payoff_2;
    double[] ratio_1;
    double[] ratio_2;

    public GameOutcome(double payoff_1, double payoff_2, double[] ratio_1, double[] ratio_2){
        this.payoff_1 = payoff_1;
        this.payoff_2 = payoff_2;
        this.ratio_1 = ratio_1;
        this.ratio_2 = ratio_2;
    }
}
