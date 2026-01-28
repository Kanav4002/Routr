package com.kanav.routeoptimizer.algorithm;

import com.kanav.routeoptimizer.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class DijkstraPathFinder {
    public List<Node> findLowestCostPath(int[][] grid, Node start, Node end) {
        if (!isValidGrid(grid) || start == null || end == null) {
            return new ArrayList<>();
        }
        if (!isWalkable(grid, start.getRow(), start.getCol())
                || !isWalkable(grid, end.getRow(), end.getCol())) {
            return new ArrayList<>();
        }
        if (start.equals(end)) {
            List<Node> path = new ArrayList<>();
            path.add(start);
            return path;
        }

        PriorityQueue<NodeCost> queue = new PriorityQueue<>(
                (a, b) -> Integer.compare(a.cost, b.cost)
        );
        Map<Node, Integer> distance = new HashMap<>();
        Map<Node, Node> parent = new HashMap<>();

        distance.put(start, 0);
        parent.put(start, null);
        queue.add(new NodeCost(start, 0));

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            NodeCost current = queue.poll();
            Node node = current.node;
            int bestKnown = distance.getOrDefault(node, Integer.MAX_VALUE);
            if (current.cost != bestKnown) {
                continue;
            }
            if (node.equals(end)) {
                return buildPath(parent, end);
            }

            for (int i = 0; i < dr.length; i++) {
                int nr = node.getRow() + dr[i];
                int nc = node.getCol() + dc[i];
                if (!isWalkable(grid, nr, nc)) {
                    continue;
                }
                Node neighbor = new Node(nr, nc);
                int stepCost = cellCost(grid[nr][nc]);
                int newCost = current.cost + stepCost;
                int existing = distance.getOrDefault(neighbor, Integer.MAX_VALUE);
                if (newCost < existing) {
                    distance.put(neighbor, newCost);
                    parent.put(neighbor, node);
                    queue.add(new NodeCost(neighbor, newCost));
                }
            }
        }

        return new ArrayList<>();
    }

    public static DijkstraResult findPathWithStats(int[][] grid, Node start, Node end) {
        long startTime = System.nanoTime();
        if (!isValidGridStatic(grid) || start == null || end == null) {
            return new DijkstraResult(new ArrayList<>(), new ArrayList<>(), 0, 0, elapsedMs(startTime));
        }
        if (!isWalkableStatic(grid, start.getRow(), start.getCol())
                || !isWalkableStatic(grid, end.getRow(), end.getCol())) {
            return new DijkstraResult(new ArrayList<>(), new ArrayList<>(), 0, 0, elapsedMs(startTime));
        }
        if (start.equals(end)) {
            List<Node> path = new ArrayList<>();
            path.add(start);
            List<Node> visitedOrder = new ArrayList<>();
            visitedOrder.add(start);
            return new DijkstraResult(path, visitedOrder, 1, 0, elapsedMs(startTime));
        }

        PriorityQueue<NodeCost> queue = new PriorityQueue<>(
                (a, b) -> Integer.compare(a.cost, b.cost)
        );
        Map<Node, Integer> distance = new HashMap<>();
        Map<Node, Node> parent = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        List<Node> visitedOrder = new ArrayList<>();
        int nodesVisited = 0;

        distance.put(start, 0);
        parent.put(start, null);
        queue.add(new NodeCost(start, 0));

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            NodeCost current = queue.poll();
            Node node = current.node;
            int bestKnown = distance.getOrDefault(node, Integer.MAX_VALUE);
            if (current.cost != bestKnown) {
                continue;
            }
            if (visited.add(node)) {
                nodesVisited++;
                visitedOrder.add(node);
            }
            if (node.equals(end)) {
                List<Node> path = buildPathStatic(parent, end);
                int totalCost = distance.getOrDefault(end, 0);
                return new DijkstraResult(path, visitedOrder, nodesVisited, totalCost, elapsedMs(startTime));
            }

            for (int i = 0; i < dr.length; i++) {
                int nr = node.getRow() + dr[i];
                int nc = node.getCol() + dc[i];
                if (!isWalkableStatic(grid, nr, nc)) {
                    continue;
                }
                Node neighbor = new Node(nr, nc);
                int stepCost = cellCostStatic(grid[nr][nc]);
                int newCost = current.cost + stepCost;
                int existing = distance.getOrDefault(neighbor, Integer.MAX_VALUE);
                if (newCost < existing) {
                    distance.put(neighbor, newCost);
                    parent.put(neighbor, node);
                    queue.add(new NodeCost(neighbor, newCost));
                }
            }
        }

        return new DijkstraResult(new ArrayList<>(), visitedOrder, nodesVisited, 0, elapsedMs(startTime));
    }

    private List<Node> buildPath(Map<Node, Node> parent, Node end) {
        LinkedList<Node> path = new LinkedList<>();
        Node current = end;
        while (current != null) {
            path.addFirst(current);
            current = parent.get(current);
        }
        return path;
    }

    private static List<Node> buildPathStatic(Map<Node, Node> parent, Node end) {
        LinkedList<Node> path = new LinkedList<>();
        Node current = end;
        while (current != null) {
            path.addFirst(current);
            current = parent.get(current);
        }
        return path;
    }

    private boolean isWalkable(int[][] grid, int row, int col) {
        return row >= 0
                && row < grid.length
                && col >= 0
                && col < grid[0].length
                && grid[row][col] != 0;
    }

    private static boolean isWalkableStatic(int[][] grid, int row, int col) {
        return row >= 0
                && row < grid.length
                && col >= 0
                && col < grid[0].length
                && grid[row][col] != 0;
    }

    private int cellCost(int cell) {
        switch (cell) {
            case 1:
                return 1;
            case 2:
                return 5;
            case 3:
                return 10;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private static int cellCostStatic(int cell) {
        switch (cell) {
            case 1:
                return 1;
            case 2:
                return 5;
            case 3:
                return 10;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private boolean isValidGrid(int[][] grid) {
        return grid.length > 0 && grid[0].length > 0;
    }

    private static boolean isValidGridStatic(int[][] grid) {
        return grid.length > 0 && grid[0].length > 0;
    }

    private static long elapsedMs(long startTimeNs) {
        return (System.nanoTime() - startTimeNs) / 1_000_000L;
    }

    private static class NodeCost {
        private final Node node;
        private final int cost;

        private NodeCost(Node node, int cost) {
            this.node = node;
            this.cost = cost;
        }
    }

    public static class DijkstraResult {
        private final List<Node> path;
        private final List<Node> visited;
        private final int nodesVisited;
        private final int totalCost;
        private final long executionTimeMs;

        public DijkstraResult(List<Node> path, List<Node> visited, int nodesVisited, int totalCost, long executionTimeMs) {
            this.path = path;
            this.visited = visited;
            this.nodesVisited = nodesVisited;
            this.totalCost = totalCost;
            this.executionTimeMs = executionTimeMs;
        }

        public List<Node> getPath() {
            return path;
        }

        public List<Node> getVisited() {
            return visited;
        }

        public int getNodesVisited() {
            return nodesVisited;
        }

        public int getTotalCost() {
            return totalCost;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }
    }
}
