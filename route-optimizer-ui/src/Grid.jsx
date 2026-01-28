import { useRef, useState } from "react";

const ROWS = 10;
const COLS = 10;

const SPEED_PRESETS = {
  slow: { visited: 300, path: 500 },
  normal: { visited: 150, path: 250 },
  fast: { visited: 50, path: 80 },
};

export default function Grid() {
  const [grid, setGrid] = useState(() => createEmptyGrid());
  const [start, setStart] = useState({ row: 0, col: 0 });
  const [end, setEnd] = useState({ row: ROWS - 1, col: COLS - 1 });
  const [stats, setStats] = useState(null);
  const [speed, setSpeed] = useState("normal");
  const [isRunning, setIsRunning] = useState(false);

  const [visitedCells, setVisitedCells] = useState(() => new Set());
  const [pathCells, setPathCells] = useState(() => new Set());
  const animationTimers = useRef([]);

  function createEmptyGrid() {
    return Array.from({ length: ROWS }, () =>
      Array.from({ length: COLS }, () => 1)
    );
  }

  const cellKey = (r, c) => `${r}-${c}`;

  const clearAnimations = () => {
    animationTimers.current.forEach(clearTimeout);
    animationTimers.current = [];
    setVisitedCells(new Set());
    setPathCells(new Set());
  };

  const resetGrid = () => {
    clearAnimations();
    setGrid(createEmptyGrid());
    setStart({ row: 0, col: 0 });
    setEnd({ row: ROWS - 1, col: COLS - 1 });
    setStats(null);
    setIsRunning(false);
  };

  const schedule = (callback, delayMs) => {
    const timerId = setTimeout(callback, delayMs);
    animationTimers.current.push(timerId);
  };

  const handleCellClick = (r, c, e) => {
    if (isRunning) return;

    const isModifier = e.ctrlKey || e.metaKey;
    const isStart = r === start.row && c === start.col;
    const isEnd = r === end.row && c === end.col;

    if (isModifier && e.shiftKey) {
      if (isStart || isEnd) return;
      setGrid((prev) =>
        prev.map((row, i) =>
          row.map((cell, j) => (i === r && j === c ? 3 : cell))
        )
      );
      return;
    }

    if (isModifier) {
      if (isStart || isEnd) return;
      setGrid((prev) =>
        prev.map((row, i) =>
          row.map((cell, j) => (i === r && j === c ? 2 : cell))
        )
      );
      return;
    }

    if (e.shiftKey) {
      setStart({ row: r, col: c });
      setGrid((prev) =>
        prev.map((row, i) =>
          row.map((cell, j) => (i === r && j === c ? 1 : cell))
        )
      );
      return;
    }

    if (e.altKey) {
      setEnd({ row: r, col: c });
      setGrid((prev) =>
        prev.map((row, i) =>
          row.map((cell, j) => (i === r && j === c ? 1 : cell))
        )
      );
      return;
    }

    setGrid((prev) =>
      prev.map((row, i) =>
        row.map((cell, j) => {
          if (i === r && j === c) {
            if (isStart || isEnd) return cell;
            return cell === 0 ? 1 : 0;
          }
          return cell;
        })
      )
    );
  };

  const getCellColor = (r, c) => {
    const key = cellKey(r, c);
    if (r === start.row && c === start.col) return "bg-emerald-500";
    if (r === end.row && c === end.col) return "bg-rose-500";
    if (pathCells.has(key)) return "bg-amber-400";
    if (visitedCells.has(key)) return "bg-sky-400";
    if (grid[r][c] === 0) return "bg-slate-900";
    if (grid[r][c] === 2) return "bg-orange-500";
    if (grid[r][c] === 3) return "bg-orange-700";
    return "bg-slate-100";
  };

  const animateVisited = (visited, stepMs, onComplete) => {
    visited.forEach((node, index) => {
      schedule(() => {
        const key = cellKey(node.row, node.col);
        if (
          (node.row === start.row && node.col === start.col) ||
          (node.row === end.row && node.col === end.col) ||
          grid[node.row][node.col] === 0
        ) {
          return;
        }
        setVisitedCells((prev) => {
          const next = new Set(prev);
          next.add(key);
          return next;
        });
      }, index * stepMs);
    });
    schedule(onComplete, visited.length * stepMs);
  };

  const animatePath = (path, stepMs, startDelayMs, onComplete) => {
    path.forEach((node, index) => {
      schedule(() => {
        const key = cellKey(node.row, node.col);
        if (
          (node.row === start.row && node.col === start.col) ||
          (node.row === end.row && node.col === end.col) ||
          grid[node.row][node.col] === 0
        ) {
          return;
        }
        setPathCells((prev) => {
          const next = new Set(prev);
          next.add(key);
          return next;
        });
      }, startDelayMs + index * stepMs);
    });
    schedule(onComplete, startDelayMs + path.length * stepMs);
  };

  const animateTraversal = (visited, path, onComplete) => {
    const { visited: visitedStepMs, path: pathStepMs } = SPEED_PRESETS[speed];
    const visitedDelayMs = visited.length * visitedStepMs;

    animateVisited(visited, visitedStepMs, () => {});
    animatePath(path, pathStepMs, visitedDelayMs, onComplete);
  };

  const resolvePathLength = (data) => {
    if (typeof data.pathLength === "number") {
      return data.pathLength;
    }
    if (!Array.isArray(data.path)) {
      return 0;
    }
    return Math.max(data.path.length - 1, 0);
  };

  const runBFS = async () => {
    const payload = {
      grid,
      startRow: start.row,
      startCol: start.col,
      endRow: end.row,
      endCol: end.col,
    };

    clearAnimations();
    setIsRunning(true);
    try {
      const response = await fetch("/api/path/bfs", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error("BFS request failed");
      }

      let data = {};
      try {
        data = await response.json();
      } catch {
        data = {};
      }

      const visited = Array.isArray(data.visited) ? data.visited : [];
      const path = Array.isArray(data.path) ? data.path : [];

      animateTraversal(visited, path, () => setIsRunning(false));
      setStats({
        algorithm: "BFS",
        nodesVisited: data.nodesVisited ?? 0,
        pathLength: resolvePathLength(data),
        executionTimeMs: data.executionTimeMs ?? 0,
      });
    } catch {
      setStats({
        algorithm: "BFS",
        nodesVisited: 0,
        pathLength: 0,
        executionTimeMs: 0,
        error: "Unable to fetch stats.",
      });
      setIsRunning(false);
    }
  };

  const runDijkstra = async () => {
    const payload = {
      grid,
      startRow: start.row,
      startCol: start.col,
      endRow: end.row,
      endCol: end.col,
    };

    clearAnimations();
    setIsRunning(true);
    try {
      const response = await fetch("/api/path/dijkstra", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error("Dijkstra request failed");
      }

      let data = {};
      try {
        data = await response.json();
      } catch {
        data = {};
      }

      const visited = Array.isArray(data.visited) ? data.visited : [];
      const path = Array.isArray(data.path) ? data.path : [];

      animateTraversal(visited, path, () => setIsRunning(false));
      setStats({
        algorithm: "Dijkstra",
        nodesVisited: data.nodesVisited ?? 0,
        pathLength: resolvePathLength(data),
        executionTimeMs: data.executionTimeMs ?? 0,
        totalCost: data.totalCost,
      });
    } catch {
      setStats({
        algorithm: "Dijkstra",
        nodesVisited: 0,
        pathLength: 0,
        executionTimeMs: 0,
        totalCost: null,
        error: "Unable to fetch stats.",
      });
      setIsRunning(false);
    }
  };

  const legendItems = [
    { label: "Start", color: "bg-emerald-500" },
    { label: "End", color: "bg-rose-500" },
    { label: "Wall", color: "bg-slate-900" },
    { label: "Visited", color: "bg-sky-400" },
    { label: "Path", color: "bg-amber-400" },
    { label: "Traffic (cost 2)", color: "bg-orange-500" },
    { label: "Heavy Traffic (cost 3)", color: "bg-orange-700" },
  ];

  return (
    <div className="w-full flex justify-center">
      <div className="bg-white/95 backdrop-blur-sm p-8 rounded-2xl shadow-2xl border border-slate-200">
        {/* Grid */}
        <div
          className="grid gap-[3px] mx-auto"
          style={{ gridTemplateColumns: `repeat(${COLS}, 36px)` }}
        >
          {grid.map((row, r) =>
            row.map((_, c) => (
              <div
                key={`${r}-${c}`}
                onClick={(e) => handleCellClick(r, c, e)}
                onContextMenu={(e) => e.preventDefault()}
                className={`w-9 h-9 rounded-md border border-slate-300 cursor-pointer 
                  transition-all duration-200 ease-out
                  hover:scale-105 hover:shadow-md
                  active:scale-95
                  ${getCellColor(r, c)}`}
              />
            ))
          )}
        </div>

        {/* Speed Control */}
        <div className="mt-6 flex items-center justify-center gap-3">
          <span className="text-sm font-semibold text-slate-600 tracking-wide">
            Speed:
          </span>
          {["slow", "normal", "fast"].map((s) => (
            <button
              key={s}
              onClick={() => setSpeed(s)}
              disabled={isRunning}
              className={`px-4 py-1.5 text-sm font-medium rounded-lg
                transition-all duration-200 ease-out
                ${
                  speed === s
                    ? "bg-slate-800 text-white shadow-lg shadow-slate-400/30"
                    : "bg-slate-100 text-slate-600 hover:bg-slate-200 hover:shadow-md"
                }
                ${isRunning ? "opacity-50 cursor-not-allowed" : "active:scale-95"}`}
            >
              {s.charAt(0).toUpperCase() + s.slice(1)}
            </button>
          ))}
        </div>

        {/* Action Buttons */}
        <div className="mt-5 flex flex-wrap justify-center gap-3">
          <button
            onClick={runBFS}
            disabled={isRunning}
            className={`px-5 py-2.5 rounded-xl font-semibold text-sm
              transition-all duration-200 ease-out
              ${
                isRunning
                  ? "bg-blue-400 text-white cursor-not-allowed"
                  : "bg-gradient-to-r from-blue-500 to-blue-600 text-white hover:from-blue-600 hover:to-blue-700 hover:shadow-lg hover:shadow-blue-500/30 active:scale-95"
              }`}
          >
            {isRunning ? "Running..." : "Run BFS"}
          </button>
          <button
            onClick={runDijkstra}
            disabled={isRunning}
            className={`px-5 py-2.5 rounded-xl font-semibold text-sm
              transition-all duration-200 ease-out
              ${
                isRunning
                  ? "bg-violet-400 text-white cursor-not-allowed"
                  : "bg-gradient-to-r from-violet-500 to-purple-600 text-white hover:from-violet-600 hover:to-purple-700 hover:shadow-lg hover:shadow-violet-500/30 active:scale-95"
              }`}
          >
            {isRunning ? "Running..." : "Run Dijkstra"}
          </button>
          <button
            onClick={resetGrid}
            className="px-5 py-2.5 rounded-xl font-semibold text-sm
              bg-slate-100 text-slate-700 border border-slate-300
              transition-all duration-200 ease-out
              hover:bg-slate-200 hover:shadow-md active:scale-95"
          >
            Reset Grid
          </button>
        </div>

        {/* Stats Panel */}
        {stats && (
          <div className="mt-6 rounded-xl border border-slate-200 bg-gradient-to-br from-slate-50 to-slate-100 p-5 text-sm text-slate-700 shadow-inner max-w-sm mx-auto">
            <div className="text-base font-bold text-slate-800 text-center tracking-wide">
              Algorithm Stats
            </div>
            <div className="mt-3 space-y-2">
              <div className="flex justify-between">
                <span className="font-medium text-slate-500">Algorithm</span>
                <span className="font-semibold">{stats.algorithm}</span>
              </div>
              <div className="flex justify-between">
                <span className="font-medium text-slate-500">Nodes Visited</span>
                <span className="font-semibold">{stats.nodesVisited ?? 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="font-medium text-slate-500">Path Length</span>
                <span className="font-semibold">{stats.pathLength ?? 0}</span>
              </div>
              {stats.totalCost !== null && stats.totalCost !== undefined && (
                <div className="flex justify-between">
                  <span className="font-medium text-slate-500">Total Cost</span>
                  <span className="font-semibold">{stats.totalCost}</span>
                </div>
              )}
              <div className="flex justify-between">
                <span className="font-medium text-slate-500">Execution Time</span>
                <span className="font-semibold">{stats.executionTimeMs ?? 0} ms</span>
              </div>
            </div>
          </div>
        )}

        {/* Legend */}
        <div className="mt-5 p-4 bg-slate-50 rounded-xl border border-slate-200 max-w-sm mx-auto">
          <div className="text-sm font-bold text-slate-700 mb-3 text-center tracking-wide">
            Legend
          </div>
          <div className="grid grid-cols-2 gap-x-6 gap-y-2 text-xs text-slate-600">
            {legendItems.map((item) => (
              <div key={item.label} className="flex items-center gap-2">
                <span
                  className={`w-5 h-5 rounded-md border border-slate-300 shadow-sm ${item.color}`}
                />
                <span className="font-medium">{item.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Controls Help */}
        <div className="mt-5 text-xs text-slate-400 text-center max-w-sm mx-auto leading-relaxed">
          <p>
            <span className="font-medium text-slate-500">Click:</span> Toggle wall
            <span className="mx-2">•</span>
            <span className="font-medium text-slate-500">Shift + Click:</span> Set Start
          </p>
          <p>
            <span className="font-medium text-slate-500">Alt + Click:</span> Set End
            <span className="mx-2">•</span>
            <span className="font-medium text-slate-500">Ctrl/Cmd + Click:</span> Traffic
          </p>
          <p>
            <span className="font-medium text-slate-500">Ctrl/Cmd + Shift + Click:</span> Heavy Traffic
          </p>
        </div>
      </div>
    </div>
  );
}
