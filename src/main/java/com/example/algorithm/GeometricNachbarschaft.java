package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import java.util.ArrayList;
import java.util.List;

public class GeometricNachbarschaft implements Nachbarschaft<ProblemInstanz> {
    private int delta = 5; // Verschiebung, kann variiert werden

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz currentSolution) {
        List<ProblemInstanz> neighbors = new ArrayList<>();

        if (!currentSolution.getRechtecke().isEmpty()) {
            // Erzeuge einen Kandidaten durch Kopie und Verschiebung eines Rechtecks
            ProblemInstanz copy = deepCopy(currentSolution);
            // Wähle beispielsweise das erste Rechteck
            Rechteck r = copy.getRechtecke().get(0);
            int newX = r.getX() + delta;
            int newY = r.getY() + delta;
            // Prüfe, ob es in die Box passt
            if (newX + r.getWidth() <= copy.getBoxLength() && newY + r.getHeight() <= copy.getBoxLength()) {
                r.setPosition(newX, newY);
                copy.platzieren();
                neighbors.add(copy);
            }
        }
        return neighbors;
    }

    // Eine einfache DeepCopy-Methode: Kopiert alle Rechtecke und erzeugt eine neue
    // ProblemInstanz
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
