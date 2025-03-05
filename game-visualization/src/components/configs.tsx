export interface Arrow {
    fromRow: number;
    fromCol: number;
    toRow: number;
    toCol: number;
    probability?: number;
}

export const player1Color = "red";
export const player2Color = "blue";

export type PoliciesGivenOpponentPosition = {
    opponentPos: number[],
    strategies: Arrow[],
}

export type Strategy = {
    position: number[];
    ratios: number[];
}

export type HighlightCell = {
  color: string;
  row: number;
  col: number;
}