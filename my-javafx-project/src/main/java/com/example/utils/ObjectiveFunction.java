package com.example.utils;

public interface ObjectiveFunction<T> {
    /**
     * Evaluates the given solution and returns a score.
     * Lower scores are assumed to be better.
     * 
     * @param solution The solution to evaluate.
     * @return The evaluation score of the solution.
     */
    double evaluate(T solution);
}
