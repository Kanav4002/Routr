package com.kanav.routeoptimizer.dto;

import com.kanav.routeoptimizer.model.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathResponse {
    private List<Node> path;
    private int pathLength;
}
