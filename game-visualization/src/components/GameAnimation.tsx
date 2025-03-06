import React, { useRef, useEffect, useState } from "react";

const GRID_SIZE = 3;
const CELL_SIZE = 100;
const DOT_RADIUS = 10;
const DEFAULT_SPEED = 1.5; // Default animation duration in ms

interface GameAnimationProps{
  positions1: [number, number][], 
  positions2: [number, number][],
}


const GameAnimation: React.FC<GameAnimationProps> = ({ positions1, positions2 }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const ElapsedTimeRef = useRef(0); // store the ref of elapsed time to make the animation smoother when the user pause/unpause
  const [index, setIndex] = useState(0);
  const [speed, setSpeed] = useState(DEFAULT_SPEED);
  const [isRunning, setIsRunning] = useState(true);

  useEffect(() => {
    if (!isRunning || index >= positions1.length - 1) return;

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let startTime: number | null = null;
    let animationFrameId: number;

    const animate = (timestamp: number) => {
      if (!isRunning) return;

      let elapsed = ElapsedTimeRef.current;
      if (startTime === null) {
        startTime = timestamp - elapsed;
      } else elapsed = timestamp - startTime;
      ElapsedTimeRef.current = elapsed;
      const adjustedSpeed = 1000 / speed;
      const t = Math.min(elapsed / adjustedSpeed, 1);

      if (index < positions1.length - 1) {
        const [x1Start, y1Start] = positions1[index];
        const [x1End, y1End] = positions1[index + 1];
        const [x2Start, y2Start] = positions2[index];
        const [x2End, y2End] = positions2[index + 1];

        const x1 = (1 - t) * x1Start + t * x1End;
        const y1 = (1 - t) * y1Start + t * y1End;
        const x2 = (1 - t) * x2Start + t * x2End;
        const y2 = (1 - t) * y2Start + t * y2End;

        ctx.clearRect(0, 0, canvas.width, canvas.height);
        drawGrid(ctx);
        drawDot(ctx, x1, y1, "red");
        drawDot(ctx, x2, y2, "blue");

        if (t < 1) {
          animationFrameId = requestAnimationFrame(animate);
        } else {
          setIndex((prev) => prev + 1);
          ElapsedTimeRef.current = 0;
        }
      }
    };

    animationFrameId = requestAnimationFrame(animate);

    return () => cancelAnimationFrame(animationFrameId);
  }, [index, positions1, positions2, isRunning]);

  const drawGrid = (ctx: CanvasRenderingContext2D) => {
    ctx.strokeStyle = "black";
    for (let i = 0; i <= GRID_SIZE; i++) {
      ctx.beginPath();
      ctx.moveTo(i * CELL_SIZE, 0);
      ctx.lineTo(i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
      ctx.moveTo(0, i * CELL_SIZE);
      ctx.lineTo(GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
      ctx.stroke();
    }
  };

  const drawDot = (ctx: CanvasRenderingContext2D, x: number, y: number, color: string) => {
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(x * CELL_SIZE + CELL_SIZE / 2, y * CELL_SIZE + CELL_SIZE / 2, DOT_RADIUS, 0, Math.PI * 2);
    ctx.fill();
  };

  const onRestart = () => {
    setIndex(0); 
    setIsRunning(true); 
    ElapsedTimeRef.current = 0;
  }

  return (
    <div>
      <canvas ref={canvasRef} width={GRID_SIZE * CELL_SIZE} height={GRID_SIZE * CELL_SIZE} />
      
      <div style={{ marginTop: 10 }}>
        <label>Speed: </label>
        <input
          type="range"
          min="1"
          max="2"
          step={0.01}
          value={speed}
          onChange={(e) => setSpeed(Number(e.target.value))}
        />
      </div>

      <button onClick={() => setIsRunning((prev) => !prev)} style={{ marginRight: 10 }}>
        {isRunning ? "Pause" : "Resume"}
      </button>

      <button onClick={() => {onRestart();}}>
        Restart
      </button>
    </div>
  );
};

export default GameAnimation;
