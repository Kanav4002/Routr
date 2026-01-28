package com.kanav.routeoptimizer.dto;

import com.kanav.routeoptimizer.model.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmResult {
    private List<Node> path;
    private List<Node> visited;
    private int pathLength;
    private int nodesVisited;
    private long executionTimeMs;
    private Integer totalCost;
}
