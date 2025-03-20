package game_solver;

import java.util.List;

public class GameStrategies {
    List<GameIndividualStrategies> strategiesGivenOtherOpponent;
    List<GameJointStrategy> jointStrategies;

    public GameStrategies(List<GameIndividualStrategies> strategiesGivenOtherOpponent, List<GameJointStrategy> jointStrategies){
        this.strategiesGivenOtherOpponent = strategiesGivenOtherOpponent;
        this.jointStrategies = jointStrategies;
    }

    public List<GameIndividualStrategies> getStrategiesGivenOtherOpponent(){
        return strategiesGivenOtherOpponent;
    }

    public List<GameJointStrategy> getJointStrategies(){
        return jointStrategies;
    }
}
