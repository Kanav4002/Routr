package com.kanav.routeoptimizer.controller;

import com.kanav.routeoptimizer.dto.AlgorithmResult;
import com.kanav.routeoptimizer.dto.ComparisonResponse;
import com.kanav.routeoptimizer.dto.PathRequest;
import com.kanav.routeoptimizer.service.PathFindingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/path")
public class PathFindingController {
    private final PathFindingService pathFindingService;

    public PathFindingController(PathFindingService pathFindingService) {
        this.pathFindingService = pathFindingService;
    }

    @PostMapping("/bfs")
    public AlgorithmResult findShortestPath(@RequestBody PathRequest request) {
        return pathFindingService.findShortestPathWithStats(
                request.getGrid(),
                request.getStartRow(),
                request.getStartCol(),
                request.getEndRow(),
                request.getEndCol()
        );
    }

    @PostMapping("/dijkstra")
    public AlgorithmResult dijkstra(@RequestBody PathRequest request) {
        return pathFindingService.findShortestPathDijkstraWithStats(
                request.getGrid(),
                request.getStartRow(),
                request.getStartCol(),
                request.getEndRow(),
                request.getEndCol()
        );
    }

    @PostMapping("/compare")
    public ComparisonResponse compare(@RequestBody PathRequest request) {
        return pathFindingService.compareAlgorithms(
                request.getGrid(),
                request.getStartRow(),
                request.getStartCol(),
                request.getEndRow(),
                request.getEndCol()
        );
    }
}
