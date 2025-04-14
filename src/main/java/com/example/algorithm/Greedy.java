package com.example.algorithm;

import com.example.interfaces.AuswahlStrategie;
import com.example.interfaces.OptimierungsProblem;
import com.example.model.ProblemInstanz;

public class Greedy implements Algorithmus<ProblemInstanz> {
    private OptimierungsProblem<ProblemInstanz> problem;
    private AuswahlStrategie<com.example.model.Rechteck> strategy;

    public Greedy(OptimierungsProblem<ProblemInstanz> problem,
            AuswahlStrategie<com.example.model.Rechteck> strategy) {
        this.problem = problem;
        this.strategy = strategy;
    }

    @Override
    public ProblemInstanz run(ProblemInstanz initialSolution) {
        // Annahme: Die ProblemInstanz enthält eine Liste von Rechtecken.
        // Strategie: Ordne die Rechtecke neu – hier zum Beispiel nach absteigender
        // Fläche.
        strategy.determineOrder(initialSolution.getRechtecke());
        // Anschließend wird eine einfache "first-fit" Platzierung verwendet:
        initialSolution.platzieren();
        return initialSolution;
    }
}
