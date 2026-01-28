package com.kanav.routeoptimizer.algorithm;

import com.kanav.routeoptimizer.model.Node;

import java.util.List;

public class DijkstraTest {
    public static void main(String[] args) {
        int[][] grid = {
                {1, 1, 2, 0, 1},
                {1, 0, 2, 1, 1},
                {1, 1, 3, 1, 0},
                {0, 1, 1, 1, 1},
                {1, 1, 0, 1, 1}
        };

        Node start = new Node(0, 0);
        Node end = new Node(4, 4);

        DijkstraPathFinder pathFinder = new DijkstraPathFinder();
        List<Node> path = pathFinder.findLowestCostPath(grid, start, end);

        if (path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        System.out.println("Lowest-cost path:");
        for (Node node : path) {
            System.out.println("(" + node.getRow() + ", " + node.getCol() + ")");
        }

        int totalCost = calculatePathCost(grid, path);
        System.out.println("Total cost: " + totalCost);
    }

    private static int calculatePathCost(int[][] grid, List<Node> path) {
        if (path.isEmpty()) {
            return 0;
        }
        int cost = 0;
        for (int i = 1; i < path.size(); i++) {
            Node node = path.get(i);
            cost += cellCost(grid[node.getRow()][node.getCol()]);
        }
        return cost;
    }

    private static int cellCost(int cell) {
        switch (cell) {
            case 1:
                return 1;
            case 2:
                return 5;
            case 3:
                return 10;
            default:
                return 0;
        }
    }
}
