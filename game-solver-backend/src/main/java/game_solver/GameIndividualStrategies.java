package game_solver;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameIndividualStrategies {
    @JsonProperty("opponentPositions")
    private int[] opponentPositions;

    @JsonProperty("transitions")
    private List<PlayerTransitions> transitions;

    // Constructor
    public GameIndividualStrategies(int[] opponentPositions, List<PlayerTransitions> transitions) {
        this.opponentPositions = opponentPositions;
        this.transitions = transitions;
    }

    // Getters
    public int[] getOpponentPositions() {
        return opponentPositions;
    }

    public List<PlayerTransitions> getTransitions() {
        return transitions;
    }

    // Setters
    public void setOpponentPositions(int[] opponentPositions) {
        this.opponentPositions = opponentPositions;
    }

    public void setTransitions(List<PlayerTransitions>transitions) {
        this.transitions = transitions;
    }
}

