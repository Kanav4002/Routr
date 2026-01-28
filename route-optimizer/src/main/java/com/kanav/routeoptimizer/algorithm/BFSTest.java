package com.kanav.routeoptimizer.algorithm;

import com.kanav.routeoptimizer.model.Node;

import java.util.List;

public class BFSTest {
    public static void main(String[] args) {
        int[][] grid = {
                {1, 1, 1, 0, 1},
                {0, 1, 1, 0, 1},
                {1, 1, 0, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 1, 0, 1}
        };

        Node start = new Node(0, 0);
        Node end = new Node(4, 4);

        BFSPathFinder pathFinder = new BFSPathFinder();
        List<Node> path = pathFinder.findShortestPath(grid, start, end);

        if (path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        System.out.println("Shortest path:");
        for (Node node : path) {
            System.out.println("(" + node.getRow() + ", " + node.getCol() + ")");
        }
    }
}
