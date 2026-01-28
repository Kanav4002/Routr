package com.kanav.routeoptimizer.service;

import com.kanav.routeoptimizer.algorithm.BFSPathFinder;
import com.kanav.routeoptimizer.algorithm.DijkstraPathFinder;
import com.kanav.routeoptimizer.dto.AlgorithmResult;
import com.kanav.routeoptimizer.dto.ComparisonResponse;
import com.kanav.routeoptimizer.model.Node;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathFindingService {
    private final BFSPathFinder pathFinder = new BFSPathFinder();
    private final DijkstraPathFinder dijkstraPathFinder = new DijkstraPathFinder();

    public List<Node> findShortestPath(int[][] grid,
                                       int startRow,
                                       int startCol,
                                       int endRow,
                                       int endCol) {
        Node start = new Node(startRow, startCol);
        Node end = new Node(endRow, endCol);
        return pathFinder.findShortestPath(grid, start, end);
    }

    public AlgorithmResult findShortestPathWithStats(int[][] grid,
                                                     int startRow,
                                                     int startCol,
                                                     int endRow,
                                                     int endCol) {
        Node start = new Node(startRow, startCol);
        Node end = new Node(endRow, endCol);
        BFSPathFinder.BFSResult bfsResult = BFSPathFinder.findPathWithStats(grid, start, end);
        int pathLength = Math.max(bfsResult.getPath().size() - 1, 0);
        long executionTimeMs = normalizeExecutionTime(
                bfsResult.getExecutionTimeMs(),
                bfsResult.getNodesVisited()
        );
        return new AlgorithmResult(
                bfsResult.getPath(),
                bfsResult.getVisited(),
                pathLength,
                bfsResult.getNodesVisited(),
                executionTimeMs,
                null
        );
    }

    public List<Node> findShortestPathDijkstra(int[][] grid,
                                               int startRow,
                                               int startCol,
                                               int endRow,
                                               int endCol) {
        Node start = new Node(startRow, startCol);
        Node end = new Node(endRow, endCol);
        return dijkstraPathFinder.findLowestCostPath(grid, start, end);
    }

    public AlgorithmResult findShortestPathDijkstraWithStats(int[][] grid,
                                                             int startRow,
                                                             int startCol,
                                                             int endRow,
                                                             int endCol) {
        Node start = new Node(startRow, startCol);
        Node end = new Node(endRow, endCol);
        DijkstraPathFinder.DijkstraResult dijkstraResult =
                DijkstraPathFinder.findPathWithStats(grid, start, end);
        int pathLength = Math.max(dijkstraResult.getPath().size() - 1, 0);
        long executionTimeMs = normalizeExecutionTime(
                dijkstraResult.getExecutionTimeMs(),
                dijkstraResult.getNodesVisited()
        );
        return new AlgorithmResult(
                dijkstraResult.getPath(),
                dijkstraResult.getVisited(),
                pathLength,
                dijkstraResult.getNodesVisited(),
                executionTimeMs,
                dijkstraResult.getTotalCost()
        );
    }

    public ComparisonResponse compareAlgorithms(int[][] grid,
                                                int startRow,
                                                int startCol,
                                                int endRow,
                                                int endCol) {
        Node start = new Node(startRow, startCol);
        Node end = new Node(endRow, endCol);

        BFSPathFinder.BFSResult bfsResult = BFSPathFinder.findPathWithStats(grid, start, end);
        DijkstraPathFinder.DijkstraResult dijkstraResult =
                DijkstraPathFinder.findPathWithStats(grid, start, end);

        AlgorithmResult bfs = new AlgorithmResult(
                bfsResult.getPath(),
                bfsResult.getVisited(),
                Math.max(bfsResult.getPath().size() - 1, 0),
                bfsResult.getNodesVisited(),
                normalizeExecutionTime(bfsResult.getExecutionTimeMs(), bfsResult.getNodesVisited()),
                null
        );

        AlgorithmResult dijkstra = new AlgorithmResult(
                dijkstraResult.getPath(),
                dijkstraResult.getVisited(),
                Math.max(dijkstraResult.getPath().size() - 1, 0),
                dijkstraResult.getNodesVisited(),
                normalizeExecutionTime(
                        dijkstraResult.getExecutionTimeMs(),
                        dijkstraResult.getNodesVisited()
                ),
                dijkstraResult.getTotalCost()
        );

        return new ComparisonResponse(bfs, dijkstra);
    }

    private long normalizeExecutionTime(long executionTimeMs, int nodesVisited) {
        if (nodesVisited <= 0) {
            return 0;
        }
        return Math.max(executionTimeMs, 1);
    }
}
