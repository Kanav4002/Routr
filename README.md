# Route Optimizer

A visual pathfinding algorithm visualizer built with React and Vite. This interactive tool demonstrates BFS (Breadth-First Search) and Dijkstra's algorithm on a customizable grid.

## Features

- **Algorithm Visualization**: Watch BFS and Dijkstra's algorithm explore the grid in real-time
- **Weighted Cells**: Add traffic (cost 2) and heavy traffic (cost 3) cells to see how Dijkstra handles weighted graphs
- **Interactive Grid**:
  - Click to toggle walls
  - Shift + Click to set start position
  - Alt + Click to set end position
  - Ctrl/Cmd + Click to add traffic cells
  - Ctrl/Cmd + Shift + Click to add heavy traffic cells
- **Animation Speed Control**: Choose between Slow, Normal, and Fast animation speeds
- **Algorithm Stats**: View nodes visited, path length, total cost (Dijkstra), and execution time
- **Clean UI**: Modern design with Inter font, smooth transitions, and responsive layout

## Tech Stack

- **React 19** - UI framework
- **Vite 7** - Build tool with HMR
- **Tailwind CSS 4** - Utility-first styling
- **Spring Boot Backend** - REST API for pathfinding algorithms

## Getting Started

### Prerequisites

- Node.js 18+
- Backend server running on `localhost:8080`

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

The app will be available at `http://localhost:5173`.

### Build for Production

```bash
npm run build
npm run preview
```

## API Endpoints

The UI communicates with the backend via:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/path/bfs` | POST | Run BFS pathfinding |
| `/api/path/dijkstra` | POST | Run Dijkstra's algorithm |

### Request Body

```json
{
  "grid": [[1, 1, 0], [1, 2, 1], [1, 1, 1]],
  "startRow": 0,
  "startCol": 0,
  "endRow": 2,
  "endCol": 2
}
```

### Response

```json
{
  "path": [{"row": 0, "col": 0}, {"row": 1, "col": 0}, ...],
  "visited": [{"row": 0, "col": 0}, ...],
  "pathLength": 4,
  "nodesVisited": 8,
  "executionTimeMs": 1,
  "totalCost": 6
}
```

## Grid Cell Values

| Value | Meaning | Color |
|-------|---------|-------|
| 0 | Wall (impassable) | Black |
| 1 | Empty (cost 1) | Light gray |
| 2 | Traffic (cost 2) | Orange |
| 3 | Heavy Traffic (cost 3) | Dark orange |

## License

MIT
