package com.generate_api;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import game_solver.GameSolver;
import game_solver.GameStrategies;

@RestController
public class API {
     // Define a test endpoint
    @GetMapping("/api/test")
    public String testApi() {
        return "Test API is working!";
    }

    @CrossOrigin(origins = "http://localhost:3000") // Allow frontend requests
    @GetMapping("/api/game_result")
    public GetGameResultResponse getGameResult() {
        GameSolver gameSolver = new GameSolver();
        gameSolver.learning();
        int[][][] optimal_strategies = gameSolver.findActions(new int[] {0, 0, 2, 2});
        List<GameStrategies> optimal_policies = gameSolver.calculateStrategies();
        return new GetGameResultResponse(optimal_strategies, optimal_policies);
    }
}


class GetGameResultResponse{
    private int[][][] optimal_strategies;
    private List<GameStrategies> optimal_policies;
    public GetGameResultResponse(int[][][] optimal_strategies, List<GameStrategies> optimal_policies){
        this.optimal_strategies = optimal_strategies;
        this.optimal_policies = optimal_policies;
    }

    public int[][][] getOptimalStrategies(){
        return this.optimal_strategies;
    }

    public List<GameStrategies> getOptimalPolicies(){
        return this.optimal_policies;
    }
}
