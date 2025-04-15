package com.example.algorithm;

import com.example.interfaces.AuswahlStrategie;
import com.example.interfaces.OptimierungsProblem;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;

import java.util.ArrayList;
import java.util.List;

public class Greedy implements Algorithmus<ProblemInstanz> {
    private OptimierungsProblem<ProblemInstanz> problem;
    private AuswahlStrategie<Rechteck> strategy;

    public Greedy(ProblemInstanz problemInstance, AuswahlStrategie<Rechteck> strategy) {
        this.problem = problemInstance;
        this.strategy = strategy;
    }

    @Override
    public ProblemInstanz run(ProblemInstanz initialSolution) {
        // Bestimme die Reihenfolge der Rechtecke anhand der Strategie.
        List<Rechteck> ordered = new ArrayList<>(strategy.determineOrder(initialSolution.getRechtecke()));
        // Ersetze die ursprüngliche Liste mit der sortierten Reihenfolge.
        initialSolution.getRechtecke().clear();
        initialSolution.getRechtecke().addAll(ordered);
        // Setze alle Rechteckpositionen zurück, damit die Platzierung von einem
        // unplatzierten Zustand startet.
        for (Rechteck r : initialSolution.getRechtecke()) {
            r.setPosition(0, 0);
        }
        // Starte den Platzierungsalgorithmus, der nun die sortierte Reihenfolge
        // berücksichtigt.
        initialSolution.platzieren();
        return initialSolution;
    }
}
