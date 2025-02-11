
public class NashGameSolver {
    double SMALL_NUM;

    public NashGameSolver(double small_num){
        this.SMALL_NUM = small_num;
    }

    /**
     * calculate the best strategy for the row player
     * 
     * @param matrix            the matrix must be constructed such that the row
     *                          player is the current player
     * @param opponent_strategy column player strategy history
     * @param count             total count of the history, in order to calculate
     *                          the ratio of the strategy
     * @return
     */
    public int calculateBestStrategy(double[][] matrix, int[] opponent_strategy, int count, boolean print) {
        double[][] opponent_strategy_ratio = new double[opponent_strategy.length][1];
        for (int i = 0; i < opponent_strategy.length; i++) {
            opponent_strategy_ratio[i][0] = (double) opponent_strategy[i] / count;
        }

        double[][] payoffs = MathUtils.matrixMultiplication(matrix, opponent_strategy_ratio);
        double max = payoffs[0][0];
        int index = 0;
        for (int i = 1; i < payoffs.length; i++) {
            // if(print && payoffs[i][0] != SMALL_NUM)
            //     System.out.println(max + " " + payoffs[i][0]);
            if (max < payoffs[i][0]) {
                max = payoffs[i][0];
                index = i;
            }
        }

        // System.out.println(index);

        return index;
    }

    /**
     * calculate the nash and return the expected payoff for both players
     * 
     * @param matrix_1
     * @param matrix_2
     * @return
     */
    public GameOutcome calculateNash(double[][] matrix_1, double[][] matrix_2, boolean print) {
        int[] strategy1 = new int[matrix_1.length]; // strategy history of player 1 (row player)
        int[] strategy2 = new int[matrix_2.length]; // strategy history of player 2 (column player)
        int count = 1; // track the number of total history strategies (both player should have the
                       // same count)

        // start with a valid strategy (will not go out of bound)
        outerLoop:
        for(int i=0; i<matrix_1.length; i++)
            for(int j=0; j<matrix_1[0].length; j++){
                if(matrix_1[i][j] != SMALL_NUM && matrix_2[j][i] != SMALL_NUM){
                    strategy1[i] = 1;
                    strategy2[j] = 1;
                    break outerLoop;
                }
                    
            }

        final int ITERATIONS = 1000;
        for (int i = 0; i < ITERATIONS; i++) {
            // calculate best response for player 1
            int best_response_1 = calculateBestStrategy(matrix_1, strategy2, count, print);
            // calculate best response for player 2
            int best_response_2 = calculateBestStrategy(matrix_2, strategy1, count, print);

            strategy1[best_response_1]++;
            strategy2[best_response_2]++;

            count++;
        }

        double[] ratio_1 = new double[matrix_1.length];
        double[] ratio_2 = new double[matrix_2.length];
        for (int i = 0; i < strategy1.length; i++) {
            double ratio = strategy1[i] / (double) count;
            // System.out.print(ratio + " ");
            ratio_1[i] = ratio;
        }
        // System.out.println();
        for (int i = 0; i < strategy2.length; i++) {
            double ratio = strategy2[i] / (double) count;
            // System.out.print(ratio + " ");
            ratio_2[i] = ratio;
        }
        // System.out.println();

        double payoff_1 = 0.0, payoff_2 = 0.0;
        for (int i = 0; i < strategy1.length; i++)
            for (int j = 0; j < strategy2.length; j++) {

                if (matrix_1[i][j] != SMALL_NUM) {
                    payoff_1 += ratio_1[i] * ratio_2[j] * matrix_1[i][j];
                }
                if (matrix_2[j][i] != SMALL_NUM) {
                    payoff_2 += ratio_1[i] * ratio_2[j] * matrix_2[j][i];
                }
            }

        // System.out.println("payoff 1 " + payoff_1);
        // System.out.println("payoff 2 " + payoff_2);
        return new GameOutcome(payoff_1, payoff_2, ratio_1, ratio_2);
    }

    /**
     * test the calculateNash function
     */
    public void testCalculateNash() {
        /**
         * (0, 0), (-1, 1), (1, -1)
         * (1, -1), (0, 0), (-1, 1)
         * (-1, 1), (1, -1), (0, 0)
         */
        double[][] matrix_1 = { { 0, -1, 1 }, { 1, 0, -1 }, { -1, 1, 0 } };
        double[][] matrix_2 = { { 0, 1, -1 }, { -1, 0, 1 }, { 1, -1, 0 } };

        /**
         * (2, 1), (0, 0)
         * (0, 0), (1, 2)
         */
        // double[][] matrix_1 = { { 3, 1 }, { 0, 4 } };
        // double[][] matrix_2 = { { 0, 1 }, { 0, 3 } };

        calculateNash(matrix_1, MathUtils.transpose(matrix_2), false);
    }

    public static void main(String[] args) {
        NashGameSolver nashGameSolver = new NashGameSolver(-1000.00);
        nashGameSolver.testCalculateNash();
    }
}
