package game_solver;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerTransitions {
    @JsonProperty("positions")
    private int[] positions;

    @JsonProperty("nextPositions")
    private int[] nextPositions;

    @JsonProperty("probability")
    private double probability;

    // Constructor
    public PlayerTransitions(int[] positions, int[] nextPositions, double probability) {
        this.positions = positions;
        this.nextPositions = nextPositions;
        this.probability = probability;
    }

    // Getters
    public int[] getPositions() {
        return positions;
    }

    public int[] getNextPositions() {
        return nextPositions;
    }

    public double getProbability() {
        return probability;
    }

    // Setters
    public void setPositions(int[] positions) {
        this.positions = positions;
    }

    public void setNextPositions(int[] nextPositions) {
        this.nextPositions = nextPositions;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}

