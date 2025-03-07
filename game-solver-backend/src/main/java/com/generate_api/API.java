package com.generate_api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import game_solver.GameSolver;
import game_solver.GameStrategies;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class API {
     // Define a test endpoint
    @GetMapping("/api/test")
    public String testApi() {
        return "Test API is working!";
    }

    @GetMapping(value = "/api/game_result", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getGameResult() {
        SseEmitter emitter = new SseEmitter(1800000L);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                GameSolver gameSolver = new GameSolver();
                gameSolver.learning(emitter);
                int[][][] optimal_strategies = gameSolver.findActions(new int[] {0, 0, 2, 2});
                List<GameStrategies> optimal_policies = gameSolver.calculateStrategies();
                GetGameResultResponse response = new GetGameResultResponse(optimal_strategies, optimal_policies);
                emitter.send(response, MediaType.APPLICATION_JSON);
                emitter.send("{\"End\": \"Game Process End\"}");
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
        executor.shutdown();
        return emitter;
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
