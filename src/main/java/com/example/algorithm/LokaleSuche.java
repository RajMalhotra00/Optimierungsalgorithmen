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

        double tolerance = 1.0; // für Überlappungs-Nachbarschaft

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

        // setze Toleranz auf 0 und ein letztes Mal optimieren
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

        // garantiere überlappungsfreie Lösung mit reguläre First Fit am Ende
        if (bestSolution instanceof ProblemInstanz) {
            ((ProblemInstanz) bestSolution).platzieren(); // für saubere Platzierung ohne Überlappung
        }

        return bestSolution;
    }
}
