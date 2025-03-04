import React, { useEffect, useState } from "react";
import './index.less';
import { Arrow, player1Color, player2Color } from "./configs";

type MatrixGridProps = {
  initialRows?: number;
  initialCols?: number;
  arrowsPlayer1: Arrow[];
  arrowsPlayer2?: Arrow[];
  header?: string;
  cellClick?: boolean;
  highlightedCell?: HighlightCell;
};

type HighlightCell = {
  color: string;
  row: number;
  col: number;
}

const MatrixGrid: React.FC<MatrixGridProps> = ({
  initialRows = 3,
  initialCols = 3,
  arrowsPlayer1,
  arrowsPlayer2,
  header,
  cellClick = false,
  highlightedCell,
}) => {
  const [rows, setRows] = useState<number>(initialRows);
  const [cols, setCols] = useState<number>(initialCols);
  const [arrow1, setArrow1] = useState<Map<String, Arrow[]>>();
  const [arrow2, setArrow2] = useState<Map<String, Arrow[]>>();

  useEffect(() => {
    let arrowMap: Map<String, Arrow[]> = new Map();
    for(const arrow of arrowsPlayer1){
      const arrowStr = `${arrow.fromRow},${arrow.fromCol}`;
      let arrowArray = arrowMap.get(arrowStr);
      if(arrowArray === undefined)
        arrowArray = [];
      arrowArray.push(arrow);
      arrowMap.set(arrowStr, arrowArray);
    }
    console.log(arrowMap)
    setArrow1(arrowMap);
  }, [arrowsPlayer1]); 

  useEffect(() => {
    let arrowMap: Map<String, Arrow[]> = new Map();
    if(arrowsPlayer2 !== undefined){
      for(const arrow of arrowsPlayer2){
        const arrowStr = `${arrow.fromRow},${arrow.fromCol}`;
        let arrowArray = arrowMap.get(arrowStr);
        if(arrowArray === undefined)
          arrowArray = [];
        arrowArray.push(arrow);
        arrowMap.set(arrowStr, arrowArray);
      }
    }
    setArrow2(arrowMap);
  }, [arrowsPlayer2]); 


  const [selectedCell, setSelectedCell] = useState<{ row: number; col: number } | undefined>(undefined);

  const handleCellClick = (row: number, col: number) => {
    if(cellClick){
      if (selectedCell && selectedCell.row === row && selectedCell.col === col) {
        setSelectedCell(undefined);
      } else {
        setSelectedCell({ row, col });
      }
    }
  };

  const cellSize = 100;
  const svgWidth = cols * cellSize;
  const svgHeight = rows * cellSize;

  // Function to nudge overlapping arrows
  const applyNudge = (x1: number, y1: number, x2: number, y2: number, offset: number, usedPositions: Map<string, number>) => {
    let xMax = Math.max(x1, x2), yMax = Math.max(y1, y2);
    let xMin = Math.min(x1, x2), yMin = Math.min(y1, y2);
    let positionStr = `${xMax},${yMax},${xMin},${yMin}`;

    let count = usedPositions.get(positionStr);
    if (count !== undefined) {
      let nudge = offset * Math.ceil(count / 2) * (count % 2 == 0 ? -1 : 1);
      if (x1 == x2) {
        x1 += nudge;
        x2 += nudge;
      } else {
        y1 += nudge;
        y2 += nudge;
      }
      usedPositions.set(positionStr, count + 1);
    } else {
      usedPositions.set(positionStr, 1);
    }
    return {x1, y1, x2, y2 };
  };

  const svgRenderOpponentPosition = () => {
    if(highlightedCell !== undefined){
      const x = highlightedCell.col * cellSize + cellSize / 4;
      const y = highlightedCell.row * cellSize + cellSize / 4;
      const radius = 5;
      return <circle cx={x} cy={y} r={radius} fill={highlightedCell.color} />
    }
  }

  const svgRenderArrows = (arrows: Arrow[], color: string, player: number) => {
    let arrowArray: Arrow[] | undefined = arrows;
    if(selectedCell !== undefined){
      const selectedCellStr = `${selectedCell.row},${selectedCell.col}`;
      if(player === 1)
        arrowArray = arrow1?.get(selectedCellStr);
      else 
        arrowArray = arrow2?.get(selectedCellStr);

      if(arrowArray === undefined) arrowArray = arrows;
    }
    const usedPositions = new Map<string, number>(); // Set to track used positions
    return arrowArray.map((arrow, index) => {
      const x1 = arrow.fromCol * cellSize + cellSize / 2;
      const y1 = arrow.fromRow * cellSize + cellSize / 2;
      const x2 = arrow.toCol * cellSize + cellSize / 2;
      const y2 = arrow.toRow * cellSize + cellSize / 2;
      const strokeWidth = 2;
      const probability = arrow.probability === undefined ? 1 : arrow.probability;
      if(probability === 0) return;
      const adjustedStrokeWidth =  probability * strokeWidth;
      
      // Apply nudge if the position has already been used
      // const { x1: nudgeX1, y1: nudgeY1, x2: nudgeX2, y2: nudgeY2 } = applyNudge(x1, y1, x2, y2, 10, usedPositions);

      const adjustLength = (num1: number, num2: number, probability: number, maxLength: number): number => {
        let difference = Math.min(Math.abs(num1 - num2), maxLength * probability);
        return num1 + difference * (num2 > num1 ? 1 : -1); 
      }

      const adjustedX2 = adjustLength(x1, x2, probability, cellSize / 2 - 5);
      const adjustedY2 = adjustLength(y1, y2, probability, cellSize / 2 - 5);

      return (
        <g key={`${color}-${index}`}>
          <line
            x1={x1}
            y1={y1}
            x2={adjustedX2}
            y2={adjustedY2}
            stroke={color}
            strokeWidth={strokeWidth}
            markerEnd={`url(#arrowhead-${color})`}
          />
          <defs>
            <marker
              id={`arrowhead-${color}`}
              markerWidth="4"  
              markerHeight="3" 
              refX="3"         
              refY="1.5"
              orient="auto"
              markerUnits="strokeWidth"
            >
              <polygon points="0 0, 4 1.5, 0 3" fill={color} />
            </marker>
          </defs>
        </g>
      );
    });
  };

  return (
    <div className="matrix-grid">
      <h1 className="matrix-grid-heading">{header}</h1>
      <div className="matrix-grid-content">
        <div className="matrix-grid-content-grid" style={{ gridTemplateColumns: `repeat(${cols}, ${cellSize}px)` }}>
          {Array.from({ length: rows }).map((_, rowIndex) =>
            Array.from({ length: cols }).map((_, colIndex) => (
              <div
                className="matrix-grid-content-grid-cell"
                key={`${rowIndex}-${colIndex}`}
                onClick={() => handleCellClick(rowIndex, colIndex)}
                style={{
                  width: `${cellSize}px`,
                  height: `${cellSize}px`,
                  backgroundColor: selectedCell?.row === rowIndex && selectedCell?.col === colIndex ? "lightblue" : "white",
                  borderColor: highlightedCell?.row === rowIndex && highlightedCell?.col === colIndex ? highlightedCell?.color : "black"
                }}
              ></div>
            ))
          )}
        </div>
        <svg className="matrix-grid-content-svg" width={svgWidth} height={svgHeight}>
          {/* Render red arrows */}
          {svgRenderArrows(arrowsPlayer1, player1Color, 1)}

          {/* Render blue arrows */}
          {arrowsPlayer2 !== undefined && svgRenderArrows(arrowsPlayer2, player2Color, 2)}

          {svgRenderOpponentPosition()}
        </svg>
      </div>
    </div>
  );
};

export default MatrixGrid;
