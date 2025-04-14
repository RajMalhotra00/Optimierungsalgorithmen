package com.example.interfaces;

public interface OptimierungsProblem<S> {
    S generateInitialSolution(); // z. B. ein zufälliges Rechteck-Packungsarrangement

    double evaluate(S solution); // Zahl der benutzten Boxen (Ziel: Minimierung)
}
