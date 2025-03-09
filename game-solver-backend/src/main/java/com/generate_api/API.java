package com.generate_api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import game_solver.GameSolver;
import game_solver.GameStrategies;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class API {
    private int[][][] rewardMatrix = new int[][][] {};


     // Define a test endpoint
    @GetMapping("/api/test")
    public String testApi() {
        return "Test API is working!";
    }

    /**
     * This function will be called first to update the reward matrix
     * @param rewardMatrix
     * @return
     */
    @PostMapping(value = "/api/start_game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> startGame(@RequestBody int[][][] rewardMatrix) {
    
        this.rewardMatrix = rewardMatrix;
        return ResponseEntity.ok().build(); // Return session ID to the frontend
    }

    /**
     * The function is called to calculate the result of the game.
     * The reason why start_game and game_result are two separate APIs is that 
     * the frontend needs to use EventSource to update the status (iteration) of the call,
     * but EventSource can only be used with GET api. I do not want to put the parameters into 
     * the url because it might be too long. Therefore, I create two separate APIs with POST and GET.
     * @return
     */
    @GetMapping(value = "/api/game_result", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getGameResult() {
        SseEmitter emitter = new SseEmitter(1800000L);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                GameSolver gameSolver = new GameSolver(rewardMatrix);
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
