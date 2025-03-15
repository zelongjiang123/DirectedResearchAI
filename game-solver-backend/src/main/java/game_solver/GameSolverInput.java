package game_solver;

public class GameSolverInput {
    private int[][][] rewardMatrix;
    private int crashValue;
    private double discountRate;

    public GameSolverInput(int[][][] rewardMatrix, int crashValue, double discountRate) {
        this.rewardMatrix = rewardMatrix;
        this.crashValue = crashValue;
        this.discountRate = discountRate;
    }

    public int[][][] getRewardMatrix() {
        return rewardMatrix;
    }

    public void setRewardMatrix(int[][][] rewardMatrix) {
        this.rewardMatrix = rewardMatrix;
    }

    public int getCrashValue() {
        return crashValue;
    }

    public void setCrashValue(int crash) {
        this.crashValue = crash;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public String toString() {
        return "GameSolverInput{" +
                "crash=" + crashValue +
                ", discountRate=" + discountRate +
                '}';
    }
}
