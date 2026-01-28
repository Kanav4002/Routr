package com.kanav.routeoptimizer.model;

import java.util.Objects;

public class Node {
    private final int row;
    private final int col;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Node)) {
            return false;
        }
        Node node = (Node) other;
        return row == node.row && col == node.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
