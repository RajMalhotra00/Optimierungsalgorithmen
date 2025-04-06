package com.example.utils;

import java.util.List;

public interface Neighborhood<T> {
    /**
     * Generates a list of neighbors for the given solution.
     * 
     * @param solution The current solution.
     * @return A list of neighboring solutions.
     */
    List<T> generateNeighbors(T solution);
}
