package com.example.utils;

import com.example.models.Box;
import com.example.models.CustomRectangle;

import java.util.ArrayList;
import java.util.List;

public class GeometricNeighborhood implements Neighborhood<List<Box>> {

    @Override
    public List<List<Box>> generateNeighbors(List<Box> currentSolution) {
        List<List<Box>> neighbors = new ArrayList<>();

        for (int i = 0; i < currentSolution.size(); i++) {
            Box currentBox = currentSolution.get(i);
            for (CustomRectangle rect : new ArrayList<>(currentBox.rectangles)) {
                for (int j = 0; j < currentSolution.size(); j++) {
                    if (i != j) { // Nicht in die gleiche Box verschieben
                        Box targetBox = currentSolution.get(j);

                        if (targetBox.getRemainingArea() >= rect.width * rect.height) {
                            // 1) Kopiere die Lösung, aber lass das Original-"rect" in Box i direkt raus
                            List<Box> newSolution = copySolutionWithoutRect(currentSolution, i, rect);

                            // 2) In der kopierten Box j fügen wir eine *Kopie* des Rechtecks ein
                            newSolution.get(j).addRectangle(new CustomRectangle(rect));

                            // 3) Fertig: newSolution hat das verschobene Rechteck nun in Box j
                            neighbors.add(newSolution);
                        }

                    }
                }
            }
        }
        return neighbors;
    }

    private List<Box> copySolutionWithoutRect(List<Box> originalSolution, int removeBoxIndex,
            CustomRectangle rectToRemove) {
        List<Box> copy = new ArrayList<>();

        for (int boxIndex = 0; boxIndex < originalSolution.size(); boxIndex++) {
            Box oldBox = originalSolution.get(boxIndex);
            Box newBox = new Box(oldBox.size);

            // Kopiere alle Rechtecke - außer rectToRemove aus Box removeBoxIndex
            for (CustomRectangle oldRect : oldBox.rectangles) {
                if (!(boxIndex == removeBoxIndex && oldRect == rectToRemove)) {
                    newBox.addRectangle(new CustomRectangle(oldRect));
                }
            }
            copy.add(newBox);
        }

        return copy;
    }

}
