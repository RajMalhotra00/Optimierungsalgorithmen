package com.example.algorithm;

import java.util.*;
import com.example.interfaces.OptimierungsProblem;
import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;

public class LokaleSuche<S> implements Algorithmus<S> {
    private OptimierungsProblem<S> problem;
    private Nachbarschaft<S> neighborhood;

    public LokaleSuche(OptimierungsProblem<S> problem, Nachbarschaft<S> neighborhood) {
        this.problem = problem;
        this.neighborhood = neighborhood;
    }

    @Override
    public S run(S currentSolution) {
        S bestSolution = currentSolution;
        double bestScore = problem.evaluate(bestSolution);
        Set<S> visited = new HashSet<>();
        visited.add(bestSolution);

        double tolerance = 1.0; // Start bei 100% Überlappung erlaubt

        while (tolerance > 0.0) {
            if (neighborhood instanceof OverlapTolerantNachbarschaft) {
                ((OverlapTolerantNachbarschaft) neighborhood).setTolerance(tolerance);
            }

            boolean improvement = true;
            while (improvement) {
                improvement = false;
                List<S> neighbors = neighborhood.getNeighbors(bestSolution);
                for (S candidate : neighbors) {
                    if (visited.contains(candidate))
                        continue;
                    double candidateScore = problem.evaluate(candidate);
                    if (candidateScore < bestScore) {
                        bestSolution = candidate;
                        bestScore = candidateScore;
                        visited.add(candidate);
                        improvement = true;
                        break; // First improvement
                    }
                }
            }

            // Reduziere die Toleranz schrittweise
            tolerance -= 0.1;
        }

        // Clean-up: Toleranz auf 0 setzen und letzte Optimierung
        if (neighborhood instanceof OverlapTolerantNachbarschaft) {
            ((OverlapTolerantNachbarschaft) neighborhood).setTolerance(0.0);
        }

        boolean improvement = true;
        while (improvement) {
            improvement = false;
            List<S> neighbors = neighborhood.getNeighbors(bestSolution);
            for (S candidate : neighbors) {
                if (visited.contains(candidate))
                    continue;
                double candidateScore = problem.evaluate(candidate);
                if (candidateScore < bestScore) {
                    bestSolution = candidate;
                    bestScore = candidateScore;
                    visited.add(candidate);
                    improvement = true;
                    break;
                }
            }
        }

        // ✨ Finaler Cleanup für garantiert überlappungsfreie Lösung
        if (bestSolution instanceof ProblemInstanz) {
            ((ProblemInstanz) bestSolution).platzieren(); // erzwingt saubere Platzierung ohne Überlappung
        }

        return bestSolution;
    }
}
