package com.kanav.routeoptimizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResponse {
    private AlgorithmResult bfsResult;
    private AlgorithmResult dijkstraResult;
}
