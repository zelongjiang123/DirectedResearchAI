package game_solver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Random;

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
        List<Integer> bestStrategies = new LinkedList<>();
        bestStrategies.add(0);
        for (int i = 1; i < payoffs.length; i++) {
            // if(print && payoffs[i][0] != SMALL_NUM)
            //     System.out.println(max + " " + payoffs[i][0]);
            if (max < payoffs[i][0]) {
                max = payoffs[i][0];
                bestStrategies = new LinkedList<>();
                bestStrategies.add(i);
            } else if(max == payoffs[i][0]){
                bestStrategies.add(i);
            }
        }

        // Random random = new Random();
        // int index = random.nextInt(bestStrategies.size()); // Generates a random index
        return bestStrategies.get(0);
    }

    /**
     * calculate the adjusted ratio based on both players' ratio and current player's payoff matrix
     * by calculating the expected payoff of each action chosen by the current player given the opponent strategy
     * @param ratio
     * @param payoff
     */
    public double[] calculateAdjustedRatio(double[] ratio, double[][] payoff, double[] opponent_ratio){
        double[] adjusted_ratio = Arrays.copyOf(ratio, ratio.length);
        
        double[][] ratio_temp = new double[opponent_ratio.length][1];
        for(int i=0; i<opponent_ratio.length; i++){
            ratio_temp[i][0] = opponent_ratio[i];
        }
        double[][] payoffs = MathUtils.matrixMultiplication( MathUtils.matrixMultiplication(payoff, ratio_temp), new double[][] {ratio});
        
        double max = payoffs[0][0];
        for(int i=1; i<payoffs.length; i++){
            max = Math.max(max, payoffs[i][0]);
        }

        if (Math.abs(max) <= 0.1) {
            for (int i = 0; i < payoffs.length; i++) {
                if (Math.abs(payoffs[i][0]) >= 1) {
                    adjusted_ratio[i] = 0.0;
                    // System.out.println(i);
                }
            }
        } else {
            final double epsilon = 0.1;
            // set the ratio of small payoff strategy to 0
            for (int i = 0; i < payoffs.length; i++) {
                double difference_ratio = Math.abs((max - payoffs[i][0]) / max);
                // System.out.println(max + " " + payoffs[i][0] + " " + difference_ratio);
                if (difference_ratio > epsilon) {
                    adjusted_ratio[i] = 0.0;
                    // System.out.println(i);
                }
            }
        }

        double sum = 0.0;
        for(int i=0; i<adjusted_ratio.length; i++){
            sum += adjusted_ratio[i];
        }

        // if(sum == 0.0){
        //     printRatio(ratio);
        // }

        for(int i=0; i<adjusted_ratio.length; i++){
            adjusted_ratio[i] /= sum;
        }
        
        // printRatio(adjusted_ratio);
        return adjusted_ratio;
    }

    public void printRatio(double[] ratio){
        for(double r: ratio){
            System.out.print(r + " ");
        }
        System.out.println();
    }

    public void visualizeBestResponse(List<Double> numbers, String title){
        SwingUtilities.invokeLater(() -> {
            LineChart example = new LineChart(title, numbers, new int[] {-1, 3});
            example.setSize(600, 400);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
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
        // double[][] matrix_1 = { { 0, -1, 1 }, { 1, 0, -1 }, { -1, 1, 0 } };
        // double[][] matrix_2 = { { 0, 1, -1 }, { -1, 0, 1 }, { 1, -1, 0 } };


        /**
         * (2, -2), (-1, 1), (-1, 1), 
         * (2, -2), (1, -1), (0, 0), 
         * (0, 0), (0, 0), (1, -1), 
         */
        // double[][] matrix_1 = { { 2, -1, -1 }, { 2, 1, 0 }, { 0, 0, 1 } };
        // double[][] matrix_2 = { { -2, 1, 1 }, { -2, -1, 0 }, { 0, 0, -1 } };

        /**
         * (2, 1), (0, 0)
         * (0, 0), (1, 2)
         */
        // double[][] matrix_1 = { { 3, 1 }, { 0, 4 } };
        // double[][] matrix_2 = { { 0, 1 }, { 0, 3 } };

        /**
         * (0, 0), (0, 0)
         * (0, 0), (10, -10)
         */
        double[][] matrix_1 = { { 0, 0 }, { 0, 10 } };
        double[][] matrix_2 = { { 0, 0 }, { 0, -10 } };

        calculateNash(matrix_1, MathUtils.transpose(matrix_2), true, true);
    }

    /**
     * calculate the nash and return the expected payoff for both players
     * @param matrix_1  row player is player 1
     * @param matrix_2  row player is player 2
     * @param useAdjustedRatio  true if return adjusted ratio
     * @return
     */
    public GameOutcome calculateNash(double[][] matrix_1, double[][] matrix_2, boolean print, boolean useAdjustedRatio) {

        int[] strategy1 = new int[matrix_1.length]; // strategy history of player 1 (row player)
        int[] strategy2 = new int[matrix_2.length]; // strategy history of player 2 (column player)
        int count1 = strategy1.length; // track the number of total history strategies 
        int count2 = strategy2.length;

        // start with a uniform distribution (both players will choose each action with probability 1/n where n is the number of strategies)
        for(int i=0; i<strategy1.length; i++){
            strategy1[i] = 1;
        }
        for(int i=0; i<strategy2.length; i++){
            strategy2[i] = 1;
        }
        
        final int ITERATIONS = 3000;
        for (int i = 0; i < ITERATIONS; i++) {
            // calculate best response for player 1
            int best_response_1 = calculateBestStrategy(matrix_1, strategy2, count2, print);
            // calculate best response for player 2
            int best_response_2 = calculateBestStrategy(matrix_2, strategy1, count1, print);

            strategy1[best_response_1]++;
            strategy2[best_response_2]++;

            count1++;
            count2++;
        }

        double[] ratio_1_adjusted = new double[matrix_1.length];
        double[] ratio_2_adjusted = new double[matrix_2.length];
        double[] ratio_1 = new double[matrix_1.length];
        double[] ratio_2 = new double[matrix_2.length];


        // do not consider the initial strategies where all strategies have 1 count
        count1 -= strategy1.length;
        count2 -= strategy2.length;
        for (int i = 0; i < strategy1.length; i++) {
            strategy1[i] -= 1;
            ratio_1[i] = strategy1[i] / (double) count1;
        }
        for (int i = 0; i < strategy2.length; i++) {
            strategy2[i] -= 1;
            ratio_2[i] = strategy2[i] / (double) count2;
        }

        ratio_1_adjusted = calculateAdjustedRatio(ratio_1, matrix_1, ratio_2);
        ratio_2_adjusted = calculateAdjustedRatio(ratio_2, matrix_2, ratio_1);

        if (print) {
            System.out.println("ratio adjusted");
            printRatio(ratio_1_adjusted);
            printRatio(ratio_2_adjusted);

            System.out.println("ratio");
            printRatio(ratio_1);
            printRatio(ratio_2);
        }
        
        double payoff_1 = 0.0, payoff_2 = 0.0, payoff_1_adjusted = 0.0, payoff_2_adjusted = 0.0;
        for (int i = 0; i < strategy1.length; i++)
            for (int j = 0; j < strategy2.length; j++) {

                if (matrix_1[i][j] != SMALL_NUM) {
                    payoff_1_adjusted += ratio_1_adjusted[i] * ratio_2_adjusted[j] * matrix_1[i][j];
                    payoff_1 += ratio_1[i] * ratio_2[j] * matrix_1[i][j];
                }
                if (matrix_2[j][i] != SMALL_NUM) {
                    payoff_2_adjusted += ratio_1_adjusted[i] * ratio_2_adjusted[j] * matrix_2[j][i];
                    payoff_2 += ratio_1[i] * ratio_2[j] * matrix_2[j][i];
                }
            }

        // System.out.println("payoff 1 " + payoff_1);
        // System.out.println("payoff 2 " + payoff_2);
        if(useAdjustedRatio)
            return new GameOutcome(payoff_1_adjusted, payoff_2_adjusted, ratio_1_adjusted, ratio_2_adjusted);
        return new GameOutcome(payoff_1, payoff_2, ratio_1, ratio_2);
    }

    public static void main(String[] args) {
        NashGameSolver nashGameSolver = new NashGameSolver(-1000.00);
        nashGameSolver.testCalculateNash();
    }
}
