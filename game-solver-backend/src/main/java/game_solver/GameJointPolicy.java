package game_solver;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GameJointPolicy {
    
    @JsonProperty("positions")
    private int[][] positions;

    @JsonProperty("transitions")
    private List<List<PlayerTransitions>> transitions;

    // Default constructor (required for deserialization)
    public GameJointPolicy() {}

    // Constructor
    public GameJointPolicy(int[][] positions, List<List<PlayerTransitions>> transitions) {
        this.positions = positions;
        this.transitions = transitions;
    }

    // Getters and Setters
    public int[][] getPositions() {
        return positions;
    }

    public void setPositions(int[][] positions) {
        this.positions = positions;
    }

    public List<List<PlayerTransitions>> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<List<PlayerTransitions>> transitions) {
        this.transitions = transitions;
    }
}
