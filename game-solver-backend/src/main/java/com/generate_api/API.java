package com.generate_api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import game_solver.GameJointPolicy;
import game_solver.GamePolicies;
import game_solver.GameSolver;
import game_solver.GameSolverInput;
import game_solver.GameStrategies;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class API {
    private final Map<String, GameSolverInput> gameSolverInputMap = new ConcurrentHashMap<>();


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
    public ResponseEntity<String> startGame(@RequestBody GameSolverInput gameSolverInput) {
        String sessionId = UUID.randomUUID().toString();
        this.gameSolverInputMap.put(sessionId, gameSolverInput);
        return ResponseEntity.ok(sessionId); // Return session ID to the frontend
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
    public SseEmitter getGameResult(@RequestParam("sessionId") String sessionId) {
        GameSolverInput gameSolverInput = gameSolverInputMap.getOrDefault(sessionId, null);
        SseEmitter emitter = new SseEmitter(1800000L);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        if(gameSolverInput == null){
            try {
                emitter.send("{\"End\": \"Game Process End\"}");
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
            emitter.complete();
            executor.shutdown();
            return emitter;
        }

        gameSolverInputMap.remove(sessionId);

        executor.execute(() -> {
            try {
                GameSolver gameSolver = new GameSolver(gameSolverInput.getRewardMatrix(), gameSolverInput.getCrashValue(), gameSolverInput.getDiscountRate());
                gameSolver.learning(emitter);
                int[][][] optimal_strategies = gameSolver.findActions(new int[] {0, 0, 2, 2});
                GamePolicies policies = gameSolver.calculateStrategies();
                GetGameResultResponse response = new GetGameResultResponse(optimal_strategies, policies.getPoliciesGivenOtherOpponent(), policies.getJointPolicies());
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
    private List<GameJointPolicy> jointPolicies;
    public GetGameResultResponse(int[][][] optimal_strategies, List<GameStrategies> optimal_policies, List<GameJointPolicy> jointPolicies){
        this.optimal_strategies = optimal_strategies;
        this.optimal_policies = optimal_policies;
        this.jointPolicies = jointPolicies;
    }

    public List<GameJointPolicy> getJointPolicies(){
        return jointPolicies;
    }

    public int[][][] getOptimalStrategies(){
        return this.optimal_strategies;
    }

    public List<GameStrategies> getOptimalPolicies(){
        return this.optimal_policies;
    }
}
