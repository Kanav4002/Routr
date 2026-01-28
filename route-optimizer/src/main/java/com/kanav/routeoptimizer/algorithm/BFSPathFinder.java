package com.kanav.routeoptimizer.algorithm;

import com.kanav.routeoptimizer.model.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFSPathFinder {
    public List<Node> findShortestPath(int[][] grid, Node start, Node end) {
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

        Queue<Node> queue = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();
        Map<Node, Node> parent = new HashMap<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) {
                return buildPath(parent, end);
            }

            for (int i = 0; i < dr.length; i++) {
                int nr = current.getRow() + dr[i];
                int nc = current.getCol() + dc[i];
                if (!isWalkable(grid, nr, nc)) {
                    continue;
                }
                Node neighbor = new Node(nr, nc);
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                parent.put(neighbor, current);
                queue.add(neighbor);
            }
        }

        return new ArrayList<>();
    }

    public static BFSResult findPathWithStats(int[][] grid, Node start, Node end) {
        long startTime = System.nanoTime();
        if (!isValidGridStatic(grid) || start == null || end == null) {
            return new BFSResult(new ArrayList<>(), new ArrayList<>(), 0, elapsedMs(startTime));
        }
        if (!isWalkableStatic(grid, start.getRow(), start.getCol())
                || !isWalkableStatic(grid, end.getRow(), end.getCol())) {
            return new BFSResult(new ArrayList<>(), new ArrayList<>(), 0, elapsedMs(startTime));
        }
        if (start.equals(end)) {
            List<Node> path = new ArrayList<>();
            path.add(start);
            List<Node> visitedOrder = new ArrayList<>();
            visitedOrder.add(start);
            return new BFSResult(path, visitedOrder, 1, elapsedMs(startTime));
        }

        Queue<Node> queue = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();
        Map<Node, Node> parent = new HashMap<>();
        List<Node> visitedOrder = new ArrayList<>();
        int nodesVisited = 0;

        queue.add(start);
        visited.add(start);
        nodesVisited++;
        visitedOrder.add(start);
        parent.put(start, null);

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) {
                List<Node> path = buildPathStatic(parent, end);
                return new BFSResult(path, visitedOrder, nodesVisited, elapsedMs(startTime));
            }

            for (int i = 0; i < dr.length; i++) {
                int nr = current.getRow() + dr[i];
                int nc = current.getCol() + dc[i];
                if (!isWalkableStatic(grid, nr, nc)) {
                    continue;
                }
                Node neighbor = new Node(nr, nc);
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                nodesVisited++;
                visitedOrder.add(neighbor);
                parent.put(neighbor, current);
                queue.add(neighbor);
            }
        }

        return new BFSResult(new ArrayList<>(), visitedOrder, nodesVisited, elapsedMs(startTime));
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
                && grid[row][col] == 1;
    }

    private static boolean isWalkableStatic(int[][] grid, int row, int col) {
        return row >= 0
                && row < grid.length
                && col >= 0
                && col < grid[0].length
                && grid[row][col] == 1;
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

    public static class BFSResult {
        private final List<Node> path;
        private final List<Node> visited;
        private final int nodesVisited;
        private final long executionTimeMs;

        public BFSResult(List<Node> path, List<Node> visited, int nodesVisited, long executionTimeMs) {
            this.path = path;
            this.visited = visited;
            this.nodesVisited = nodesVisited;
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

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }
    }
}
