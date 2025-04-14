package com.example.algorithm;

import com.example.interfaces.OptimierungsProblem;
import com.example.interfaces.Nachbarschaft;
import java.util.List;

public class LokaleSuche<S> implements Algorithmus<S> {
    private OptimierungsProblem<S> problem;
    private Nachbarschaft<S> neighborhood;

    public LokaleSuche(OptimierungsProblem<S> problem, Nachbarschaft<S> neighborhood) {
        this.problem = problem;
        this.neighborhood = neighborhood;
    }

    @Override
    public S run(S currentSolution) {
        boolean improvement = true;
        S bestSolution = currentSolution;
        double bestScore = problem.evaluate(bestSolution);
        // Solange ein benachbarter Zustand eine Verbesserung bringt, wird die Suche
        // fortgesetzt.
        while (improvement) {
            improvement = false;
            List<S> neighbors = neighborhood.getNeighbors(bestSolution);
            for (S candidate : neighbors) {
                double candidateScore = problem.evaluate(candidate);
                if (candidateScore < bestScore) { // Minimierungsproblem
                    bestScore = candidateScore;
                    bestSolution = candidate;
                    improvement = true;
                }
            }
        }
        return bestSolution;
    }
}
