package game_solver;

import java.util.List;

public class GamePolicies {
    List<GameStrategies> policiesGivenOtherOpponent;
    List<GameJointPolicy> jointPolicies;

    public GamePolicies(List<GameStrategies> policiesGivenOtherOpponent, List<GameJointPolicy> jointPolicy){
        this.policiesGivenOtherOpponent = policiesGivenOtherOpponent;
        this.jointPolicies = jointPolicy;
    }

    public List<GameStrategies> getPoliciesGivenOtherOpponent(){
        return policiesGivenOtherOpponent;
    }

    public List<GameJointPolicy> getJointPolicies(){
        return jointPolicies;
    }
}
