import React, { useState } from "react";

const directions = ["up", "down", "left", "right", "none"] as const;
type Direction = (typeof directions)[number];

type MatrixGridProps = {
  initialRows?: number;
  initialCols?: number;
};

const MatrixGrid: React.FC<MatrixGridProps> = ({ initialRows = 3, initialCols = 3 }) => {
  const [rows, setRows] = useState<number>(initialRows);
  const [cols, setCols] = useState<number>(initialCols);
  const [arrowMatrix, setArrowMatrix] = useState<Direction[][]>(
    Array.from({ length: rows }, () => Array.from({ length: cols }, () => "none"))
  );

  const handleArrowChange = (row: number, col: number) => {
    setArrowMatrix((prev) => {
      const newMatrix = prev.map((r) => [...r]);
      const currentDirection = newMatrix[row][col];
      const nextDirection = directions[(directions.indexOf(currentDirection) + 1) % directions.length];
      newMatrix[row][col] = nextDirection;
      return newMatrix;
    });
  };

  const getArrowSVG = (direction: Direction) => {
    switch (direction) {
      case "up":
        return "↑";
      case "down":
        return "↓";
      case "left":
        return "←";
      case "right":
        return "→";
      default:
        return "";
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", padding: "20px" }}>
      <h1 style={{ fontSize: "24px", fontWeight: "bold" }}>n × m Matrix with Arrows</h1>
      <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
        <input
          type="number"
          min="1"
          max="10"
          value={rows}
          onChange={(e) => setRows(Math.max(1, Math.min(10, Number(e.target.value))))}
          style={{ width: "50px", textAlign: "center" }}
        />
        <input
          type="number"
          min="1"
          max="10"
          value={cols}
          onChange={(e) => setCols(Math.max(1, Math.min(10, Number(e.target.value))))}
          style={{ width: "50px", textAlign: "center" }}
        />
        <button onClick={() => setArrowMatrix(Array.from({ length: rows }, () => Array.from({ length: cols }, () => "none")))} style={{ padding: "5px 10px", cursor: "pointer" }}>Update</button>
      </div>
      <div style={{ display: "grid", gridTemplateColumns: `repeat(${cols}, 50px)`, gap: "0px" }}>
        {arrowMatrix.map((row, rowIndex) =>
          row.map((cell, colIndex) => (
            <div
              key={`${rowIndex}-${colIndex}`}
              onClick={() => handleArrowChange(rowIndex, colIndex)}
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                width: "50px",
                height: "50px",
                border: "1px solid gray",
                cursor: "pointer",
                fontSize: "20px",
                position: "relative",
              }}
            >
              {getArrowSVG(cell)}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default MatrixGrid;
