package com.generate_api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import game_solver.GameSolver;

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
        return new GetGameResultResponse(optimal_strategies);
    }
}


class GetGameResultResponse{
    private int[][][] optimal_strategies;

    public GetGameResultResponse(int[][][] optimal_strategies){
        this.optimal_strategies = optimal_strategies;
    }

    public int[][][] getOptimalStrategies(){
        return this.optimal_strategies;
    }
}
