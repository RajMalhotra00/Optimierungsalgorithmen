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
        List<Rechteck> rechtecke = currentSolution.getRechtecke();
        int n = rechtecke.size();
        if (n < 2)
            return neighbors;

        // Erzeuge Nachbarn durch den Austausch zweier Rechtecke
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                ProblemInstanz copy = deepCopy(currentSolution);
                Collections.swap(copy.getRechtecke(), i, j);
                copy.platzieren();
                neighbors.add(copy);
            }
        }
        return neighbors;
    }

    private ProblemInstanz deepCopy(ProblemInstanz original) {
        List<Rechteck> rechteckeCopy = new ArrayList<>();
        for (Rechteck r : original.getRechtecke()) {
            Rechteck copy = new Rechteck(r.getWidth(), r.getHeight());
            copy.setPosition(r.getX(), r.getY());
            rechteckeCopy.add(copy);
        }
        return new ProblemInstanz(original.getBoxLength(), rechteckeCopy);
    }
}
