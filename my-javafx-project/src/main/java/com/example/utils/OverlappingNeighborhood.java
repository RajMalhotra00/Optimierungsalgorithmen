package com.example.utils;

import com.example.models.Box;
import com.example.models.CustomRectangle;

import java.util.ArrayList;
import java.util.List;

public class OverlappingNeighborhood implements Neighborhood<List<Box>> {
    private double overlapTolerance;

    public OverlappingNeighborhood(double initialTolerance) {
        this.overlapTolerance = initialTolerance;
    }

    public void reduceOverlapTolerance(double reductionStep) {
        this.overlapTolerance = Math.max(0, overlapTolerance - reductionStep);
    }

    @Override
    public List<List<Box>> generateNeighbors(List<Box> solution) {
        List<List<Box>> neighbors = new ArrayList<>();

        // Generiere Nachbarn durch Verschieben von Rechtecken mit zulässiger
        // Überlappung
        for (int i = 0; i < solution.size(); i++) {
            Box currentBox = solution.get(i);
            for (CustomRectangle rect : new ArrayList<>(currentBox.rectangles)) {
                for (int j = 0; j < solution.size(); j++) {
                    if (i != j) {
                        Box targetBox = solution.get(j);

                        // Simulierte Überlappung prüfen
                        if (canFitWithOverlap(targetBox, rect)) {
                            List<Box> newSolution = copySolution(solution);
                            newSolution.get(i).rectangles.remove(rect);
                            newSolution.get(j).addRectangle(rect);
                            neighbors.add(newSolution);
                        }
                    }
                }
            }
        }

        return neighbors;
    }

    private boolean canFitWithOverlap(Box box, CustomRectangle rect) {
        // Simulierte Logik für Überlappung basierend auf overlapTolerance
        return true; // Dummy-Logik, anpassen für reale Überlappungsprüfung
    }

    private List<Box> copySolution(List<Box> solution) {
        List<Box> copy = new ArrayList<>();
        for (Box box : solution) {
            Box newBox = new Box(box.size);
            newBox.rectangles.addAll(box.rectangles);
            copy.add(newBox);
        }
        return copy;
    }
}
