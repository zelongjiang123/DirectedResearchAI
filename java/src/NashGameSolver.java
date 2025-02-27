import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
     * calculate the adjusted ratio based on player's ratio and the opponent payoff matrix
     * by calculating the expected payoff of the opponent's each strategy
     * @param ratio
     * @param payoff_matrix_opponent
     */
    public void calculateAdjustedRatio(double[] ratio, double[][] payoff_matrix_opponent){
        double[] adjusted_ratio = Arrays.copyOf(ratio, ratio.length);
        
        double[][] ratio_temp = new double[ratio.length][1];
        for(int i=0; i<ratio.length; i++){
            ratio_temp[i][0] = ratio[i];
        }
        double[][] payoffs = MathUtils.matrixMultiplication(payoff_matrix_opponent, ratio_temp);
        
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

        for(int i=0; i<adjusted_ratio.length; i++){
            adjusted_ratio[i] /= sum;
        }
        
        printRatio(adjusted_ratio);
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

        final int ITERATIONS = 3000;
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
            ratio_1[i] = ratio;
        }
        for (int i = 0; i < strategy2.length; i++) {
            double ratio = strategy2[i] / (double) count;
            ratio_2[i] = ratio;
        }

        // printRatio(ratio_1);
        // printRatio(ratio_2);

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
        // double[][] matrix_1 = { { 0, -1, 1 }, { 1, 0, -1 }, { -1, 1, 0 } };
        // double[][] matrix_2 = { { 0, 1, -1 }, { -1, 0, 1 }, { 1, -1, 0 } };


        /**
         * (2, -2), (-1, 1), (-1, 1), 
         * (2, -2), (1, -1), (0, 0), 
         * (0, 0), (0, 0), (1, -1), 
         */
        double[][] matrix_1 = { { 2, -1, -1 }, { 2, 1, 0 }, { 0, 0, 1 } };
        double[][] matrix_2 = { { -2, 1, 1 }, { -2, -1, 0 }, { 0, 0, -1 } };

        /**
         * (2, 1), (0, 0)
         * (0, 0), (1, 2)
         */
        // double[][] matrix_1 = { { 3, 1 }, { 0, 4 } };
        // double[][] matrix_2 = { { 0, 1 }, { 0, 3 } };

        calculateNashWithAdjustedRatio(matrix_1, MathUtils.transpose(matrix_2), false);
    }

    /**
     * calculate the nash and return the expected payoff for both players with adjusted ratio
     * 
     * @param matrix_1
     * @param matrix_2
     * @return
     */
    public GameOutcome calculateNashWithAdjustedRatio(double[][] matrix_1, double[][] matrix_2, boolean print) {

        // calculate the adjusted strategy ratio
        int[] strategy1_adjusted = new int[matrix_1.length]; // strategy history of player 1 (row player)
        int[] strategy2_adjusted = new int[matrix_2.length]; // strategy history of player 2 (column player)
        Queue<Integer> strategy1 = new LinkedList<>(), strategy2 = new LinkedList<>();
        
        List<Double> best_responses_1 = new LinkedList<>(), best_responses_2 = new LinkedList<>();

        // start with a valid strategy (will not go out of bound)
        outerLoop:
        for(int i=0; i<matrix_1.length; i++)
            for(int j=0; j<matrix_1[0].length; j++){
                if(matrix_1[i][j] != SMALL_NUM && matrix_2[j][i] != SMALL_NUM){
                    best_responses_1.add((double) i);
                    best_responses_2.add((double) j);
                    strategy1_adjusted[i]++;
                    strategy2_adjusted[j]++;
                    strategy1.add(i);
                    strategy2.add(j);
                    break outerLoop;
                }
                    
            }

        final int ITERATIONS = 3000;
        for (int i = 0; i < ITERATIONS; i++) {
            // calculate best response for player 1
            int best_response_1 = calculateBestStrategy(matrix_1, strategy2_adjusted, strategy2.size(), print);
            // calculate best response for player 2
            int best_response_2 = calculateBestStrategy(matrix_2, strategy1_adjusted, strategy2.size(), print);
            
            best_responses_1.add((double) best_response_1);
            best_responses_2.add((double) best_response_2);

            strategy1_adjusted[best_response_1]++;
            strategy2_adjusted[best_response_2]++;

            strategy1.add(best_response_1);
            strategy2.add(best_response_2);
            
            if(strategy1.size() >= 500){
                strategy1_adjusted[strategy1.poll()]--;
                strategy2_adjusted[strategy2.poll()]--;
            }
        }

        visualizeBestResponse(best_responses_1, "best response 1");
        visualizeBestResponse(best_responses_2, "best response 2");

        double[] ratio_1_adjusted = new double[matrix_1.length];
        double[] ratio_2_adjusted = new double[matrix_2.length];

        for (int i = 0; i < strategy1_adjusted.length; i++) {
            ratio_1_adjusted[i] = strategy1_adjusted[i] / (double) strategy1.size();
            // System.out.print(strategy1_adjusted[i] + " ");
        }
        // System.out.println();
        for (int i = 0; i < strategy2_adjusted.length; i++) {
            ratio_2_adjusted[i] = strategy2_adjusted[i] / (double) strategy2.size();
            // System.out.print(strategy1_adjusted[i] + " ");
        }
        // System.out.println();
        // System.out.println(count_adjusted);

        printRatio(ratio_1_adjusted);
        printRatio(ratio_2_adjusted);        

        // calculateAdjustedRatio(ratio_2, matrix_1);
        // calculateAdjustedRatio(ratio_1, matrix_2);


        double payoff_1_adjusted = 0.0, payoff_2_adjusted = 0.0;
        for (int i = 0; i < strategy1_adjusted.length; i++)
            for (int j = 0; j < ratio_2_adjusted.length; j++) {
                if (matrix_1[i][j] != SMALL_NUM) {
                    payoff_1_adjusted += ratio_1_adjusted[i] * ratio_2_adjusted[j] * matrix_1[i][j];
                }
                if (matrix_2[j][i] != SMALL_NUM) {
                    payoff_2_adjusted += ratio_1_adjusted[i] * ratio_2_adjusted[j] * matrix_2[j][i];
                }
            }

        // System.out.println("payoff 1 " + payoff_1);
        // System.out.println("payoff 2 " + payoff_2);
        // return new GameOutcome(payoff_1, payoff_2, ratio_1, ratio_2);
        return new GameOutcome(payoff_1_adjusted, payoff_2_adjusted, ratio_1_adjusted, ratio_2_adjusted);
    }

    public static void main(String[] args) {
        NashGameSolver nashGameSolver = new NashGameSolver(-1000.00);
        nashGameSolver.testCalculateNash();
    }
}
