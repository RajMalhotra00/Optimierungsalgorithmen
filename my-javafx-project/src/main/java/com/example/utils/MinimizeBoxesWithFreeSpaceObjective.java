package com.example.utils;

import com.example.models.Box;
import java.util.List;

/**
 * Eine Ziel-Funktion, die sowohl die Anzahl der verwendeten Boxen
 * als auch die gesamte freie Fläche in allen Boxen minimiert.
 */
public class MinimizeBoxesWithFreeSpaceObjective implements ObjectiveFunction<List<Box>> {

    private final double alpha; // Gewichtung der freien Fläche

    /**
     * Konstruktor.
     * 
     * @param alpha Der Faktor, mit dem die freie Fläche gewichtet wird.
     *              Ein typischer Wert könnte z.B. 0.1 oder 0.01 sein,
     *              je nachdem, wie stark du die freie Fläche im Vergleich
     *              zur Box-Anzahl bewerten willst.
     */
    public MinimizeBoxesWithFreeSpaceObjective(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double evaluate(List<Box> solution) {
        // Anzahl benutzter Boxen (Boxen, die nicht leer sind)
        long usedBoxes = solution.stream()
                .filter(box -> !box.isEmpty())
                .count();

        // Gesamtfreie Fläche in allen Boxen summieren
        long totalFreeSpace = 0;
        for (Box box : solution) {
            totalFreeSpace += box.getRemainingArea();
        }

        // Objective = (Anzahl benutzter Boxen) + alpha * (Summe freie Fläche)
        return usedBoxes + alpha * totalFreeSpace;
    }
}
