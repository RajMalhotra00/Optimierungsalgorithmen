package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import java.util.ArrayList;
import java.util.List;

public class OverlapTolerantNachbarschaft implements Nachbarschaft<ProblemInstanz> {
    private double tolerance; // Toleranz in Prozent (1.0 = 100% Toleranz)

    public OverlapTolerantNachbarschaft(double tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz currentSolution) {
        List<ProblemInstanz> neighbors = new ArrayList<>();
        // Hier ähnlich wie bei GeometricNachbarschaft: Verschiebe z. B. ein Rechteck,
        // aber in der Bewertungsfunktion von ProblemInstanz fließt tolerance ein.
        if (!currentSolution.getRechtecke().isEmpty()) {
            ProblemInstanz copy = deepCopy(currentSolution);
            Rechteck r = copy.getRechtecke().get(0);
            int newX = r.getX() + 3;
            int newY = r.getY() + 3;
            if (newX + r.getWidth() <= copy.getBoxLength() && newY + r.getHeight() <= copy.getBoxLength()) {
                r.setPosition(newX, newY);
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
