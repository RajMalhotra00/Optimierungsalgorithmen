package com.example.interfaces;

public interface OptimierungsProblem<S> {
    S generateInitialSolution();

    double evaluate(S solution);
}
