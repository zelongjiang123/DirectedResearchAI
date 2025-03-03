package game_solver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameSolver {

    // 0: up, 1: down, 2: left, 3: right
    // both Qs have the same format: [the state (both players position in trinary form and then convert it to integer)][player 1 action][player 2 action]
    double[][][] Q1; // matrix for player 1
    double[][][] Q2; // matrix for player 2

    // reward matrix is
    /*
     * 1 2 3
     * 1 1 2
     * 3 1 2
     */
    final int[][] REWARD = new int[][] { { 1, 2, 3 }, { 1, 1, 2 }, { 3, 1, 2 } };

    // reward matrix is
    /*
     * 1 1 1
     * 1 1 1
     * 1 1 1
     */
    // final int[][] REWARD = new int[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
    final int[][] REWARD_SPECIAL = new int[][] { { -4, 5, 1 }, { 0, -1, -2 }, { 4, 1, 2 } };; // for player 2


    final int CRASH = 10;
    final double DISCOUNT = 0.9;
    final double SMALL_NUM = -10000.0;
    
    final int STATES = 81;
    final int ACTIONS = 4;

    public GameSolver() {
        Q1 = new double[STATES][ACTIONS][ACTIONS];
        Q2 = new double[STATES][ACTIONS][ACTIONS];
    }

    /**
     * find the nex positions given the current positions and players' actions
     * @param positions
     * @param actions
     * @return
     */
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
        int num_actions_1 = Q1[0].length, num_actions_2 = Q1[0][0].length;
        double[][] payoff_1 = new double[num_actions_1][num_actions_2];
        double[][] payoff_2 = new double[num_actions_1][num_actions_2];
        int state = MathUtils.trinaryToDecimal(positions);
        for (int i = 0; i < num_actions_1; i++)
            for (int j = 0; j < num_actions_2; j++) {
                payoff_1[i][j] = Q1[state][i][j];
                payoff_2[i][j] = Q2[state][i][j];
            }

        return new double[][][] { payoff_1, payoff_2 };
    }

    /**
     * generate a temporary copy for a given Q
     * @param Q
     * @return
     */
    public double[][][] generateTempCopy(double[][][] Q) {
        double[][][] temp_Q = new double[Q.length][Q[0].length][Q[0][0].length];
        for (int state = 0; state < Q.length; state++)
            for (int action1 = 0; action1 < Q[0].length; action1++)
                for (int action2 = 0; action2 < Q[0][0].length; action2++) {
                    temp_Q[state][action1][action2] = Q[state][action1][action2];
                }

        return temp_Q;
    }

    /**
     * print the values in Q
     * @param Q
     */
    public void printQ(double[][][] Q) {
        for (int state = 0; state < Q.length; state++) {
            int[] positions = MathUtils.decimalToTrinary(state, 4);
            System.out.println("row1 " + positions[0] + " col1 " + positions[1] + " row2 " + positions[2] + " col2 "
                    + positions[3]);
            for (int action1 = 0; action1 < Q[0].length; action1++)
                for (int action2 = 0; action2 < Q[0][0].length; action2++) {
                    System.out.print(Q[state][action1][action2] + " ");
                }
            System.out.println();
        }

    }

    /**
     * check whether the positions are valid (cannot be out of bound)
     * @param positions
     * @return
     */
    public boolean isValidPositions(int[] positions){
        int temp = (int) Math.sqrt(STATES);
        int boundary = (int) Math.sqrt(temp);
        for (int pos = 0; pos < positions.length; pos++) {
            if (positions[pos] < 0 || positions[pos] >= boundary) {
                return false;
            }
        }
        return true;
    }


    /**
     * update Q based on Nash
     * @param Q
     * @param player
     * @return
     */
    public double[][][] updateQ(double[][][] Q, int player) {
        double[][][] temp_Q = generateTempCopy(Q);
        for (int state = 0; state < Q.length; state++) {
            int[] positions = MathUtils.decimalToTrinary(state, 4);
            int row1 = positions[0], col1 = positions[1], row2 = positions[2], col2 = positions[3];
            for (int action1 = 0; action1 < Q[0].length; action1++)
                for (int action2 = 0; action2 < Q[0][0].length; action2++) {
                    int reward = REWARD[row1][col1] - REWARD[row2][col2];
                    // int reward = REWARD[row1][col1];
                    if (player == 2) {
                        reward = REWARD[row2][col2] - REWARD[row1][col1];
                        // reward = REWARD_SPECIAL[row2][col2];
                    }
                    if (row1 == row2 && col1 == col2) {
                        if (player == 1)
                            reward += CRASH;
                        else
                            reward -= CRASH;
                    }

                    int[] nextPositions = calculateNextPositions(new int[] { row1, col1, row2, col2 },
                            new int[] { action1, action2 });

                    
                    

                    if (!isValidPositions(nextPositions)) { // the next positions are out of bound (not a valid state)
                        temp_Q[state][action1][action2] = SMALL_NUM;
                    } else {
                        double[][][] payoff_matrix = constructPayoffMatrix(nextPositions);
                        double[][] payoff_matrix_1 = payoff_matrix[0],
                                payoff_matrix_2 = payoff_matrix[1];

                        NashGameSolver nashGameSolver = new NashGameSolver(SMALL_NUM);
                        GameOutcome outcome = nashGameSolver.calculateNash(payoff_matrix_1,
                                MathUtils.transpose(payoff_matrix_2), false);
                        double payoff = (player == 1) ? outcome.payoff_1 : outcome.payoff_2;
                        temp_Q[state][action1][action2] = reward
                                + DISCOUNT * payoff;
                    }

                }

        }
        return temp_Q;
    }

    public double calculateAverageQDifference(double[][][] curr_Q, double[][][] prev_Q){
        int count = STATES * ACTIONS * ACTIONS;
        double total_diff = 0.0;
        for (int state = 0; state < curr_Q.length; state++) {
            for (int action1 = 0; action1 < curr_Q[0].length; action1++)
                for (int action2 = 0; action2 < curr_Q[0][0].length; action2++) {
                    total_diff += Math.abs(curr_Q[state][action1][action2] - prev_Q[state][action1][action2]);
                }
        }
        return total_diff / (double) count;
    }

    /**
     * learn the Q value
     */
    public String learning() {
        final int ITERATIONS = 100;
        List<Double> losses1 = new LinkedList<>(), losses2 = new LinkedList<>();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            // update Q1 for player 1
            double[][][] temp_Q1 = updateQ(Q1, 1);
            // update Q2 for player 2
            double[][][] temp_Q2 = updateQ(Q2, 2);
            double loss1 = calculateAverageQDifference(temp_Q1, Q1);
            double loss2 = calculateAverageQDifference(temp_Q2, Q2);

            Q1 = temp_Q1;
            Q2 = temp_Q2;

            losses1.add(loss1);
            losses2.add(loss2);

            str.append("Iteration " + i + " is complete, loss is " + loss1 + " " + loss2);
            System.out.println("Iteration " + i + " is complete, loss is " + loss1 + " " + loss2);
        }

        SwingUtilities.invokeLater(() -> {
            LineChart chart = new LineChart("Difference for Q1", losses1, new int[] {-1, 5});
            chart.setSize(600, 400);
            chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chart.setVisible(true);
        });

        // SwingUtilities.invokeLater(() -> {
        //     LineChart chart = new LineChart("Difference for Q2", losses2, new int[] {-1, 5 });
        //     chart.setSize(600, 400);
        //     chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //     chart.setVisible(true);
        // });

        // visualizeQ(Q1, 1);
        // visualizeQ(Q1, 2);

        // System.out.println("Q1");
        // printQ(Q1);

        // System.out.println("Q2");
        // printQ(Q1);
        return str.toString();
    }


    /**
     * choose player action based on the probabilities
     * @param probabilities
     * @return
     */
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

    public void visualizeQ(double[][][] Q, int player){
        double [][][] payoffs = new double[9][3][3];
        for (int state = 0; state < Q.length; state++) {
            int[] positions = MathUtils.decimalToTrinary(state, 4);            
            double[][][] payoff_matrix = constructPayoffMatrix(positions);
            double[][] payoff_matrix_1 = payoff_matrix[0],
                    payoff_matrix_2 = payoff_matrix[1];

            NashGameSolver nashGameSolver = new NashGameSolver(SMALL_NUM);
            GameOutcome outcome = nashGameSolver.calculateNash(payoff_matrix_1, 
                MathUtils.transpose(payoff_matrix_2), false);
            
            if(player == 1)
                payoffs[positions[2] * 3 + positions[3]][positions[0]][positions[1]] = outcome.payoff_1;
            else if(player == 2)
                payoffs[positions[0] * 3 + positions[1]][positions[2]][positions[3]] = outcome.payoff_2;
                
        }
    }

    /**
     * find the players' actions based on their start position
     * it should be called after learning process is complete
     * @param startPosition
     * @return array of positions
     */
    public int[][] findActions(int[] startPosition){
        printPositions(startPosition);
        final int ITERATIONS = 10;
        int[][] positions = new int[ITERATIONS+1][startPosition.length];
        positions[0] = Arrays.copyOf(startPosition, startPosition.length);
        for (int i = 0; i < ITERATIONS; i++) {
            double[][][] payoff_matrix = constructPayoffMatrix(startPosition);
            double[][] payoff_matrix_1 = payoff_matrix[0], payoff_matrix_2 = payoff_matrix[1];
           // printPayoffMatrices(payoff_matrix_1, payoff_matrix_2);
            NashGameSolver nashGameSolver = new NashGameSolver(SMALL_NUM);
            GameOutcome outcome = nashGameSolver.calculateNash(payoff_matrix_1, MathUtils.transpose(payoff_matrix_2), false);
            int strategy1 = chooseActionBasedOnProbability(outcome.ratio_1), 
            strategy2 = chooseActionBasedOnProbability(outcome.ratio_2);

            startPosition = calculateNextPositions(startPosition, new int[] {strategy1, strategy2});
            printPositions(startPosition);
            positions[i+1] = Arrays.copyOf(startPosition, startPosition.length);
        }
        return positions;
    }

    public void visualizeAction(){
        int[][] positions = findActions(new int[] {0, 0, 2, 2});

        List<int[]> path1 = new LinkedList<>();
        List<int[]> path2 = new LinkedList<>();
        for(int i=0; i<positions.length; i++){
            path1.add(new int[] {positions[i][0], positions[i][1]});
            path2.add(new int[] {positions[i][2], positions[i][3]});
        }
        
        JFrame frame = new JFrame("3x3 Grid with Moving Cars");
        GameVisualization panel = new GameVisualization(positions, REWARD);
        frame.add(panel);
        frame.setSize(GameVisualization.GRID_SIZE * GameVisualization.CELL_SIZE + 15, GameVisualization.GRID_SIZE * GameVisualization.CELL_SIZE + 40);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GameSolver gameSolver = new GameSolver();
        // gameSolver.testCalculateNash();
        gameSolver.learning();
        gameSolver.visualizeAction();
    }
}