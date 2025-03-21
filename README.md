# **GameSolverBackendSourceCode**

GameSolverBackendSourceCode contains different versions of the game solver. 

## **Content**
- `game-solver-backend`: The source code of the deployed [backend](https://github.com/zelongjiang123/GameSolverDeployment). It uses Java, Java Spring Boot, and Maven.
    - `game-solver-backend/target/demo-0.0.1-SNAPSHOT.jar`: the jar file that is used in the [backend](https://github.com/zelongjiang123/GameSolverDeployment) deployment. 
    - `game-solver-backend/src/main/java/game_solver`: the folder that contains all relevant java files that computes the game result
    - `game-solver-backend/src/main/java/com/generate_api`: the folder that contains all the settings and the contents of the APIs hosted by Java Spring Boot. 

- `python`: the python version of the game solver. It is very slow compared to the Java version.

## **Features**
- Provides different versions of the game solver
- Provides the APIs for the [frontend](https://github.com/zelongjiang123/GameVisualization)