package com.example.utils;

import com.example.models.Box;
import com.example.models.CustomRectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Beispiel-Implementierung eines Rule-Based-Nachbarschaftskonzepts
 * für das Packing-Problem. Verschiebt Rechtecke von einer Box zur anderen.
 */
public class RuleBasedNeighborhood implements Neighborhood<List<Box>> {

    /**
     * Erzeugt Nachbarn, indem Rechtecke von einer Box i in eine andere Box j
     * verschoben werden, sofern sie dort noch reinpassen.
     *
     * @param solution Die aktuelle Lösung (Liste von Boxen).
     * @return Eine Liste mit neuen Lösungen (Nachbarn).
     */
    @Override
    public List<List<Box>> generateNeighbors(List<Box> solution) {
        List<List<Box>> neighbors = new ArrayList<>();

        for (int i = 0; i < solution.size(); i++) {
            Box sourceBox = solution.get(i);
            for (CustomRectangle rect : new ArrayList<>(sourceBox.rectangles)) {
                for (int j = 0; j < solution.size(); j++) {
                    if (i != j) {
                        Box targetBox = solution.get(j);

                        if (targetBox.canFit(rect)) {
                            // Neue Kopie erzeugen, in der rect NICHT in Box i auftaucht
                            List<Box> newSolution = copySolutionWithoutRect(solution, i, rect);

                            // Dann rect in Box j einfügen
                            newSolution.get(j).addRectangle(new CustomRectangle(rect));

                            // Check: leere Box entfernen
                            if (newSolution.get(i).isEmpty()) {
                                newSolution.remove(i);
                            }

                            neighbors.add(newSolution);
                        }
                    }
                }
            }
        }
        return neighbors;
    }

    private List<Box> copySolutionWithoutRect(
            List<Box> originalSolution,
            int removeFromBoxIndex,
            CustomRectangle rectToRemove) {
        List<Box> copy = new ArrayList<>();

        for (int boxIndex = 0; boxIndex < originalSolution.size(); boxIndex++) {
            Box oldBox = originalSolution.get(boxIndex);
            Box newBox = new Box(oldBox.size);

            // Wir kopieren alle Rechtecke, außer das eine, das wir verschieben wollen
            for (CustomRectangle oldRect : oldBox.rectangles) {
                if (!(boxIndex == removeFromBoxIndex && oldRect == rectToRemove)) {
                    newBox.addRectangle(new CustomRectangle(oldRect));
                }
            }
            copy.add(newBox);
        }

        return copy;
    }

}
