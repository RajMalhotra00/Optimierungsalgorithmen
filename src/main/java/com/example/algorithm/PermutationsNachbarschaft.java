package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermutationsNachbarschaft implements Nachbarschaft<ProblemInstanz> {
    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz currentSolution) {
        List<ProblemInstanz> neighbors = new ArrayList<>();
        List<Rechteck> rects = currentSolution.getRechtecke();

        for (int i = 0; i < rects.size(); i++) {
            for (int j = i + 1; j < rects.size(); j++) {
                List<Rechteck> copyList = deepCopyRects(rects);
                Collections.swap(copyList, i, j);
                ProblemInstanz newInstance = new ProblemInstanz(currentSolution.getBoxLength(), copyList);
                newInstance.platzieren(); // Verwende neue Reihenfolge
                neighbors.add(newInstance);
            }
        }
        return neighbors;
    }

    private List<Rechteck> deepCopyRects(List<Rechteck> original) {
        List<Rechteck> copy = new ArrayList<>();
        for (Rechteck r : original) {
            Rechteck neu = new Rechteck(r.getWidth(), r.getHeight());
            copy.add(neu);
        }
        return copy;
    }
}
