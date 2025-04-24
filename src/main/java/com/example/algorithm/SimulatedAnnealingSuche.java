package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.interfaces.OptimierungsProblem;
import com.example.model.ProblemInstanz;

import java.util.List;
import java.util.Random;

/**
 * Sehr leichtgewichtige Simulated-Annealing-Implementation,
 * ausgelegt auf das Rechteck-Packing-Problem.
 */
public class SimulatedAnnealingSuche<S> implements Algorithmus<S> {

    private final OptimierungsProblem<S> problem;
    private final Nachbarschaft<S> neighborhood;

    // klassische SA-Parameter – gerne experimentieren
    private double startTemp = 100.0;
    private double coolingRate = 0.92;
    private int itersPerTemp = 40;

    private final Random rng = new Random();

    public SimulatedAnnealingSuche(OptimierungsProblem<S> problem,
            Nachbarschaft<S> neighborhood) {
        this.problem = problem;
        this.neighborhood = neighborhood;
    }

    @Override
    public S run(S current) {

        double currScore = problem.evaluate(current);
        S best = current;
        double bestScore = currScore;

        double T = startTemp;

        while (T > 0.5) {

            /*----------- Nachbarn generieren --------------------*/
            List<S> neighbors = neighborhood.getNeighbors(current);
            if (neighbors.isEmpty())
                break; // Sackgasse → Abbruch

            /*----------- zufälligen Kandidaten wählen -----------*/
            S cand = neighbors.get(rng.nextInt(neighbors.size()));

            // Aktuelle Toleranz (nur wenn beide Klassen es können)
            if (cand instanceof ProblemInstanz candPI &&
                    neighborhood instanceof OverlapTolerantNachbarschaftTuned nb) {
                candPI.setTolerance(nb.getTolerance());
            }

            double candScore = problem.evaluate(cand);
            double delta = candScore - currScore;

            boolean accept = delta < 0 ||
                    Math.exp(-delta / T) > rng.nextDouble();

            if (accept) {
                current = cand;
                currScore = candScore;

                if (candScore < bestScore) {
                    best = cand;
                    bestScore = candScore;
                }
            }

            /*----------- Temperatur & Toleranz anpassen ----------*/
            T *= coolingRate;

            if (neighborhood instanceof OverlapTolerantNachbarschaftTuned nb) {
                double newTol = Math.max(0.0, nb.getTolerance() - 0.05);
                nb.setTolerance(newTol);
            }
        }
        return best;
    }
}
