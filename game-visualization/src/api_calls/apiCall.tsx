import { Arrow } from "../components/configs";

// Example Usage:
interface GetGameResultResponse {
    arrows: Arrow[][];
}

interface GetGameResultAPIResponse{
    optimalStrategies: number[][][]
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
        
        if(parsedResponse.optimalStrategies !== undefined){
            let positions = parsedResponse.optimalStrategies;
        
            for(let i=0; i<positions[0].length-1; i++){
                arrows.push([
                    { fromRow: positions[0][i][0], fromCol: positions[0][i][1], toRow: positions[0][i + 1][0], toCol: positions[0][i + 1][1] }, 
                    { fromRow: positions[1][i][0], fromCol: positions[1][i][1], toRow: positions[1][i + 1][0], toCol: positions[1][i + 1][1] }
                ]);
            }
        }
        
        return {arrows};
    } catch (error) {
        console.error("Error fetching data:", error);
        throw error;
    }
}
