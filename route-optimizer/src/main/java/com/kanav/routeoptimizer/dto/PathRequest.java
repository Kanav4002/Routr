package com.kanav.routeoptimizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathRequest {
    private int[][] grid;
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;
}
