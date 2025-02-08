import java.util.Arrays;
import java.util.Random;

class GameOutcome{
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

public class GameSolver {

    // 0: up, 1: down, 2: left, 3: right
    double[][][][][][] Q1; // matrix for player 1
    double[][][][][][] Q2; // matrix for player 2

    // reward matrix is
    /*
     * 1 2 3
     * 1 1 2
     * 3 1 2
     */
    // final int[][] REWARD = new int[][] { { 1, 2, 3 }, { 1, 1, 2 }, { 3, 1, 2 } };

    // reward matrix is
    /*
     * 1 1 1
     * 1 1 1
     * 1 1 1
     */
    final int[][] REWARD = new int[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };

    
    final int CRASH = 10;
    final double DISCOUNT = 0.9;
    final double SMALL_NUM = -10000.0;

    public GameSolver() {
        Q1 = new double[3][3][3][3][4][4];
        Q2 = new double[3][3][3][3][4][4];
    }

    /**
     * returns the result of A * B
     * 
     * @param A
     * @param B
     * @return
     */
    public double[][] matrixMultiplication(double[][] A, double[][] B) {
        int m = A.length; // Rows in A
        int n = A[0].length; // Columns in A (must match rows in B)
        int p = B[0].length; // Columns in B

        if (B.length != n) {
            throw new IllegalArgumentException("Invalid matrix dimensions for multiplication.");
        }

        double[][] C = new double[m][p]; // Result matrix

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                C[i][j] = 0.0; // Initialize cell
                for (int k = 0; k < n; k++) {
                    // if(A[i][k] == Double.NEGATIVE_INFINITY || B[k][j] == Double.NEGATIVE_INFINITY){
                    //     C[i][j] = Double.NEGATIVE_INFINITY;
                    //     break;
                    // }
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
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

        double[][] payoffs = matrixMultiplication(matrix, opponent_strategy_ratio);
        double max = payoffs[0][0];
        int index = 0;
        for (int i = 1; i < payoffs.length; i++) {
            if(print)
                System.out.println(max + " " + payoffs[i][0]);
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

        // start with an arbitrary strategy 0, 0
        strategy1[0] = 1;
        strategy2[0] = 1;

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

    public static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows]; // Swap dimensions

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j]; // Swap elements
            }
        }
        return transposed;
    }

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

        calculateNash(matrix_1, transpose(matrix_2), false);
    }

    public int[] calculateNextPositions(int[] positions, int actions[]) {
        int[] nextPositions = Arrays.copyOf(positions, positions.length);
        for (int i = 0; i < actions.length; i++) {
            switch (actions[i]) {
                case 0: // up
                    nextPositions[i * 2] -= 1;
                    break;
                case 1: // down
                    nextPositions[i * 2] += 1;
                    break;
                case 2: // left
                    nextPositions[i * 2 + 1] -= 1;
                    break;
                case 3: // right
                    nextPositions[i * 2 + 1] += 1;
                    break;

                default:
                    break;
            }
        }

        return nextPositions;
    }

    /**
     * construct the payoff matrices for both players based on their current positions
     * @param positions has a format of {player1_row, player1_col, player2_row, player2_col}
     * @return  two payoff matrices with same format: row player is player 1 and column player is player 2
     */
    public double[][][] constructPayoffMatrix(int[] positions) {
        int num_actions_1 = Q1[0][0][0][0].length, num_actions_2 = Q1[0][0][0][0][0].length;
        double[][] payoff_1 = new double[num_actions_1][num_actions_2];
        double[][] payoff_2 = new double[num_actions_1][num_actions_2];

        for (int i = 0; i < num_actions_1; i++)
            for (int j = 0; j < num_actions_2; j++) {
                payoff_1[i][j] = Q1[positions[0]][positions[1]][positions[2]][positions[3]][i][j];
                payoff_2[i][j] = Q2[positions[0]][positions[1]][positions[2]][positions[3]][i][j];
            }

        return new double[][][] { payoff_1, payoff_2 };
    }

    public double[][][][][][] generateTempCopy(double[][][][][][] Q) {
        double[][][][][][] temp_Q = new double[Q.length][Q[0].length][Q[0][0].length][Q[0][0][0].length][Q[0][0][0][0].length][Q[0][0][0][0][0].length];
        for (int row1 = 0; row1 < Q.length; row1++)
            for (int col1 = 0; col1 < Q[0].length; col1++)
                for (int row2 = 0; row2 < Q[0][0].length; row2++)
                    for (int col2 = 0; col2 < Q[0][0][0].length; col2++)

                        for (int action1 = 0; action1 < Q[0][0][0][0].length; action1++)
                            for (int action2 = 0; action2 < Q[0][0][0][0][0].length; action2++) {
                                temp_Q[row1][col1][row2][col2][action1][action2] = Q[row1][col1][row2][col2][action1][action2];
                            }

        return temp_Q;
    }

    public void printQ(double[][][][][][] Q) {
        for (int row1 = 0; row1 < Q.length; row1++)
            for (int col1 = 0; col1 < Q[0].length; col1++)
                for (int row2 = 0; row2 < Q[0][0].length; row2++)
                    for (int col2 = 0; col2 < Q[0][0][0].length; col2++) {
                        System.out.println("row1 " + row1 + " col2 " + col1 + " row2 " + row2 + " col2 " + col2);
                        for (int action1 = 0; action1 < Q[0][0][0][0].length; action1++)
                            for (int action2 = 0; action2 < Q[0][0][0][0][0].length; action2++) {
                                System.out.print(Q[row1][col1][row2][col2][action1][action2] + " ");
                            }
                        System.out.println();
                    }

    }

    public double[][][][][][] updateQ(double[][][][][][] Q, int player) {
        double[][][][][][] temp_Q = generateTempCopy(Q);
        for (int row1 = 0; row1 < Q.length; row1++)
            for (int col1 = 0; col1 < Q[0].length; col1++)
                for (int row2 = 0; row2 < Q[0][0].length; row2++)
                    for (int col2 = 0; col2 < Q[0][0][0].length; col2++) {

                        for (int action1 = 0; action1 < Q[0][0][0][0].length; action1++)
                            for (int action2 = 0; action2 < Q[0][0][0][0][0].length; action2++) {
                                int reward = REWARD[row1][col1] - REWARD[row2][col2];
                                if (row1 == row2 && col1 == col2) {
                                    if (player == 1)
                                        reward += CRASH;
                                    else
                                        reward -= CRASH;
                                }

                                int[] nextPositions = calculateNextPositions(new int[] { row1, col1, row2, col2 },
                                        new int[] { action1, action2 });

                                // check whether the next positions are out of bound
                                boolean outOfBound = false;
                                for (int pos = 0; pos < nextPositions.length; pos += 2) {
                                    if (nextPositions[pos] < 0 || nextPositions[pos] >= Q.length) {
                                        outOfBound = true;
                                        break;
                                    }
                                }
                                for (int pos = 1; pos < nextPositions.length; pos += 2) {
                                    if (nextPositions[pos] < 0 || nextPositions[pos] >= Q[0].length) {
                                        outOfBound = true;
                                        break;
                                    }
                                }

                                if (outOfBound) { // the next positions are out of bound (not a valid state)
                                    temp_Q[row1][col1][row2][col2][action1][action2] = SMALL_NUM;
                                } else {
                                    double[][][] payoff_matrix = constructPayoffMatrix(nextPositions);
                                    double[][] payoff_matrix_1 = payoff_matrix[0],
                                            payoff_matrix_2 = payoff_matrix[1];

                                    GameOutcome outcome = calculateNash(payoff_matrix_1, transpose(payoff_matrix_2), false);
                                    double payoff = (player == 1) ? outcome.payoff_1 : outcome.payoff_2;
                                    temp_Q[row1][col1][row2][col2][action1][action2] = reward
                                            + DISCOUNT * payoff;
                                }

                            }

                    }
        return temp_Q;
    }

    public void learning() {
        final int ITERATIONS = 100;
        for (int i = 0; i < ITERATIONS; i++) {
            // update Q1 for player 1
            double[][][][][][] temp_Q1 = updateQ(Q1, 1);
            // update Q2 for player 2
            double[][][][][][] temp_Q2 = updateQ(Q2, 2);
            Q1 = temp_Q1;
            Q2 = temp_Q2;

            System.out.println("Iteration " + i + " is complete");
        }

        System.out.println("Q1");
        printQ(Q1);

        System.out.println("Q2");
        printQ(Q1);
    }

    public int chooseActionBasedOnProbability(double[] probabilities){
        // Compute cumulative probabilities
        double[] cumulative = new double[probabilities.length];
        cumulative[0] = probabilities[0];
        for (int i = 1; i < probabilities.length; i++) {
            cumulative[i] = cumulative[i - 1] + probabilities[i];
        }

        // for(double p: probabilities)
        //     System.out.print(p + " ");
        // System.out.println();

        // Generate a random number between 0 and 1
        Random rand = new Random();
        double randomValue = rand.nextDouble();

        // Find the corresponding player
        for (int i = 0; i < cumulative.length; i++) {
            if (randomValue < cumulative[i]) {
                return i;
            }
        }
        return probabilities.length-1; // Fallback (shouldn't reach)
    }

    public void printPositions(int[] positions){
        System.out.print("player 1: (" + positions[0] + "," + positions[1] + ")" + " ");
        System.out.println("player 2: (" + positions[2] + "," + positions[3] + ")" + " ");
    }   

    public void printPayoffMatrices(double[][] payoff_matrix_1, double[][] payoff_matrix_2){
        for(int i=0; i<payoff_matrix_1.length; i++){
            for(int j=0; j<payoff_matrix_1[0].length; j++){
                System.out.print("(" + payoff_matrix_1[i][j] + "," + payoff_matrix_2[i][j] + ") ");
            }
            System.out.println();
        }
    }

    public void findActions(int[] startPosition){
        printPositions(startPosition);
        final int ITERATIONS = 10;
        for (int i = 0; i < ITERATIONS; i++) {
            double[][][] payoff_matrix = constructPayoffMatrix(startPosition);
            double[][] payoff_matrix_1 = payoff_matrix[0], payoff_matrix_2 = payoff_matrix[1];
           // printPayoffMatrices(payoff_matrix_1, payoff_matrix_2);
            GameOutcome outcome = calculateNash(payoff_matrix_1, transpose(payoff_matrix_2), false);
            int strategy1 = chooseActionBasedOnProbability(outcome.ratio_1), 
            strategy2 = chooseActionBasedOnProbability(outcome.ratio_2);

            startPosition = calculateNextPositions(startPosition, new int[] {strategy1, strategy2});
            printPositions(startPosition);
        }
    }

    public static void main(String[] args) {
        GameSolver gameSolver = new GameSolver();
        // gameSolver.testCalculateNash();
        gameSolver.learning();
        gameSolver.findActions(new int[] {0, 0, 2, 2});
    }
}