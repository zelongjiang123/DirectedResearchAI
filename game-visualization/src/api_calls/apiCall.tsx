import { Arrow, PoliciesGivenOpponentPosition, Strategy } from "../components/configs";


interface GetGameResultResponse {
    arrows: Arrow[][];
    policies: PoliciesGivenOpponentPosition[][];
    positionsForAllPlayers: [number, number][][];
}

interface GetGameResultAPIResponse{
    optimalStrategies: number[][][];
    optimalPolicies: {
        opponentPositions: number[], 
        transitions: {nextPositions: number[], positions: number[], probability: number}[]
    }[];
}

export async function getGameResult(): Promise<GetGameResultResponse> {
    try {
        const response = await fetch("http://localhost:8080/api/game_result", {
            method: "GET",
            headers: {
                "Accept": "application/json",
            },
        });

        
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        let parsedResponse: GetGameResultAPIResponse = await response.json();
        console.log(parsedResponse);
        let arrows: Arrow[][] = [];
        let positionsForAllPlayers: [number, number] [][] = [[], []];
        
        if(parsedResponse.optimalStrategies !== undefined){
            let positions = parsedResponse.optimalStrategies;
            let positionsPlayer1: [number, number] [] = [], positionsPlayer2: [number, number] [] = [];
        
            for(let i=0; i<positions[0].length-1; i++){
                arrows.push([
                    { fromRow: positions[0][i][0], fromCol: positions[0][i][1], toRow: positions[0][i + 1][0], toCol: positions[0][i + 1][1] }, 
                    { fromRow: positions[1][i][0], fromCol: positions[1][i][1], toRow: positions[1][i + 1][0], toCol: positions[1][i + 1][1] }
                ]);
            }

            for(let i=0; i<positions[0].length; i++){
                positionsPlayer1.push([positions[0][i][0], positions[0][i][1]]);
                positionsPlayer2.push([positions[1][i][0], positions[1][i][1]]);
            }

            positionsForAllPlayers[0] = positionsPlayer1;
            positionsForAllPlayers[1] = positionsPlayer2;
        }


        let policyListPlayer1: PoliciesGivenOpponentPosition[] = []; 
        let policyListPlayer2: PoliciesGivenOpponentPosition[] = [];

        if(parsedResponse.optimalPolicies !== undefined){
            let policies = parsedResponse.optimalPolicies;
            for(let i=0; i<policies.length/2; i++){
                let strategies: Arrow[] = [];
                for(const strategy of policies[i].transitions){
                    strategies.push({fromRow: strategy.positions[0], fromCol: strategy.positions[1], toRow: strategy.nextPositions[0], toCol: strategy.nextPositions[1], probability: strategy.probability});
                }
                policyListPlayer1.push({opponentPos: policies[i].opponentPositions, strategies: strategies});
            }
            for(let i=policies.length/2; i<policies.length; i++){
                let strategies: Arrow[] = [];
                for(const strategy of policies[i].transitions){
                    strategies.push({fromRow: strategy.positions[0], fromCol: strategy.positions[1], toRow: strategy.nextPositions[0], toCol: strategy.nextPositions[1], probability: strategy.probability});
                }
                policyListPlayer2.push({opponentPos: policies[i].opponentPositions, strategies: strategies});
            }
        }
        
        return {arrows, policies: [policyListPlayer1, policyListPlayer2], positionsForAllPlayers};
    } catch (error) {
        console.error("Error fetching data:", error);
        throw error;
    }
}
