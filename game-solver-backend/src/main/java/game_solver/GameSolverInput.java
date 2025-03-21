package game_solver;

public class GameSolverInput {
    private int[][][] rewardMatrix;
    private int crashValue;
    private double discountRate;
    private String gameType;

    public GameSolverInput(int[][][] rewardMatrix, int crashValue, double discountRate, String gameType) {
        this.rewardMatrix = rewardMatrix;
        this.crashValue = crashValue;
        this.discountRate = discountRate;
        this.gameType = gameType;
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

    public String getGameType() {
        return gameType;
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

    public void setGameType(String gameType){
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "GameSolverInput{" +
                "crash=" + crashValue +
                ", discountRate=" + discountRate +
                ", gameType=" + gameType +
                '}';
    }
}
