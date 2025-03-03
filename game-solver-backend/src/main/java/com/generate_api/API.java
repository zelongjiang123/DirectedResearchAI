package com.generate_api;

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

    @GetMapping("/api/game_result")
    public String getGameResult() {
        GameSolver gameSolver = new GameSolver();
        // gameSolver.testCalculateNash();
        String str = gameSolver.learning();
        // gameSolver.visualizeAction();
        return str;
    }
}
