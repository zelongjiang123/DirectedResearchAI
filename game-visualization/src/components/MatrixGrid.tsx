import React, { useState } from "react";
import './index.less';

interface Arrow {
  fromRow: number;
  fromCol: number;
  toRow: number;
  toCol: number;
}

type MatrixGridProps = {
  initialRows?: number;
  initialCols?: number;
};

const MatrixGrid: React.FC<MatrixGridProps> = ({ initialRows = 3, initialCols = 3 }) => {
  const [rows, setRows] = useState<number>(initialRows);
  const [cols, setCols] = useState<number>(initialCols);
  const [arrows, setArrows] = useState<Arrow[]>([]);
  const [selectedCell, setSelectedCell] = useState<{ row: number; col: number } | null>(null);

  const handleCellClick = (row: number, col: number) => {
    if (selectedCell) {
      setArrows([...arrows, { fromRow: selectedCell.row, fromCol: selectedCell.col, toRow: row, toCol: col }]);
      setSelectedCell(null);
    } else {
      setSelectedCell({ row, col });
    }
  };

  const cellSize = 50;
  const svgWidth = cols * cellSize;
  const svgHeight = rows * cellSize;

  return (
    <div className="matrix-grid">
      <h1 className="matrix-grid-heading">n × m Matrix with Arrows</h1>
      <div className="matrix-grid-content">
        <div className="matrix-grid-content-grid" style={{ gridTemplateColumns: `repeat(${cols}, ${cellSize}px)`}}>
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
                }}
              ></div>
            ))
          )}
        </div>
        <svg className="matrix-grid-content-svg" width={svgWidth} height={svgHeight}>
          {arrows.map((arrow, index) => {
            const x1 = arrow.fromCol * cellSize + cellSize / 2;
            const y1 = arrow.fromRow * cellSize + cellSize / 2;
            const x2 = arrow.toCol * cellSize + cellSize / 2;
            const y2 = arrow.toRow * cellSize + cellSize / 2;
            return (
              <line
                key={index}
                x1={x1}
                y1={y1}
                x2={x2}
                y2={y2}
                stroke="black"
                strokeWidth="2"
                markerEnd="url(#arrowhead)"
              />
            );
          })}
          <defs>
            <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="7" refY="3.5" orient="auto" markerUnits="strokeWidth">
              <polygon points="0 0, 10 3.5, 0 7" fill="black" />
            </marker>
          </defs>
        </svg>
      </div>
    </div>
  );
};

export default MatrixGrid;
