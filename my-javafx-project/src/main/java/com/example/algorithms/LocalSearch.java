package com.example.algorithms;

import com.example.utils.Neighborhood;
import com.example.utils.ObjectiveFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * Generischer Lokale-Suche-Algorithmus für beliebige Optimierungsprobleme.
 */
public class LocalSearch<T> {
    private final ObjectiveFunction<T> objective;
    private final Neighborhood<T> neighborhood;

    /**
     * Konstruktor für die lokale Suche.
     * 
     * @param objective    Die Zielfunktion zur Bewertung der Lösung.
     * @param neighborhood Die Nachbarschaftsfunktion zur Erzeugung von
     *                     Nachbarlösungen.
     */
    public LocalSearch(ObjectiveFunction<T> objective, Neighborhood<T> neighborhood) {
        this.objective = objective;
        this.neighborhood = neighborhood;
    }

    /**
     * Optimiert die Lösung basierend auf der lokalen Suche.
     * 
     * @param initialSolution Startlösung.
     * @param maxIterations   Maximale Anzahl an Iterationen.
     * @return Optimierte Lösung.
     */
    public T optimize(T initialSolution, int maxIterations) {
        T currentSolution = initialSolution;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<T> neighbors = neighborhood.generateNeighbors(currentSolution);
            T bestNeighbor = neighbors.stream()
                    .min((a, b) -> Double.compare(objective.evaluate(a), objective.evaluate(b)))
                    .orElse(currentSolution);

            // Stopkriterium: Keine Verbesserung mehr
            if (objective.evaluate(bestNeighbor) >= objective.evaluate(currentSolution)) {
                break;
            }
            currentSolution = bestNeighbor;
        }
        return currentSolution;
    }

    /**
     * Gibt die Optimierungsschritte zurück, nützlich für Visualisierungen.
     */
    public List<T> getOptimizationSteps(T initialSolution, int maxIterations) {
        List<T> steps = new ArrayList<>();
        T currentSolution = initialSolution;
        steps.add(copySolution(currentSolution));

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<T> neighbors = neighborhood.generateNeighbors(currentSolution);
            T bestNeighbor = neighbors.stream()
                    .min((a, b) -> Double.compare(objective.evaluate(a), objective.evaluate(b)))
                    .orElse(currentSolution);

            if (objective.evaluate(bestNeighbor) >= objective.evaluate(currentSolution)) {
                break;
            }
            currentSolution = bestNeighbor;
            steps.add(copySolution(currentSolution));
        }
        return steps;
    }

    /**
     * Hilfsfunktion: Erzeugt eine Kopie der Lösung (muss implementiert werden).
     */
    private T copySolution(T solution) {
        // WARNUNG: Muss abhängig von der Datenstruktur implementiert werden!
        return solution; // Platzhalter, sollte überschrieben werden!
    }
}
