package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import com.example.model.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverlapTolerantNachbarschaft implements Nachbarschaft<ProblemInstanz> {
    private double tolerance; // Erlaubter Überlappungsprozentsatz (z.B. 0.3 = 30%)

    public OverlapTolerantNachbarschaft(double tolerance) {
        this.tolerance = tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz currentSolution) {
        List<ProblemInstanz> neighbors = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < currentSolution.getRechtecke().size(); i++) {
            ProblemInstanz copy = deepCopy(currentSolution);
            Rechteck r = copy.getRechtecke().get(i);

            int dx = rand.nextInt(7) - 3; // Verschiebung zwischen -3 und +3
            int dy = rand.nextInt(7) - 3;

            int newX = r.getX() + dx;
            int newY = r.getY() + dy;

            // Nur gültige Koordinaten
            if (newX < 0 || newY < 0 || newX + r.getWidth() > copy.getBoxLength()
                    || newY + r.getHeight() > copy.getBoxLength()) {
                continue;
            }

            r.setPosition(newX, newY);

            // Checke Überlappung gemäß erlaubter Toleranz
            boolean valid = true;
            outerLoop: for (Box box : copy.getBoxes()) {
                for (Rechteck other : box.getRechtecke()) {
                    if (other == r)
                        continue;
                    double overlapRatio = calculateOverlapRatio(r, other);
                    if (overlapRatio > tolerance) {
                        valid = false;
                        break outerLoop;
                    }
                }
            }

            if (valid) {
                neighbors.add(copy);
            }
        }

        return neighbors;
    }

    private double calculateOverlapRatio(Rechteck a, Rechteck b) {
        int xOverlap = Math.max(0,
                Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth()) - Math.max(a.getX(), b.getX()));
        int yOverlap = Math.max(0,
                Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight()) - Math.max(a.getY(), b.getY()));
        int overlapArea = xOverlap * yOverlap;

        if (overlapArea == 0)
            return 0.0;

        int maxArea = Math.max(a.getWidth() * a.getHeight(), b.getWidth() * b.getHeight());
        return (double) overlapArea / maxArea;
    }

    private ProblemInstanz deepCopy(ProblemInstanz original) {
        List<Rechteck> rechteckeCopy = new ArrayList<>();
        for (Rechteck r : original.getRechtecke()) {
            Rechteck copy = new Rechteck(r.getWidth(), r.getHeight());
            copy.setPosition(r.getX(), r.getY());
            rechteckeCopy.add(copy);
        }

        ProblemInstanz copy = new ProblemInstanz(original.getBoxLength(), rechteckeCopy);
        copy.platzieren(); // wichtig, damit die Boxen neu berechnet werden
        return copy;
    }
}
