package com.example.model;

import com.example.interfaces.OptimierungsProblem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProblemInstanz implements OptimierungsProblem<ProblemInstanz> {
    private int boxLength;
    private List<Rechteck> rechtecke;
    private List<Box> boxes;

    public ProblemInstanz(int boxLength, List<Rechteck> rechtecke) {
        this.boxLength = boxLength;
        this.rechtecke = rechtecke;
        this.boxes = new ArrayList<>();
    }

    public int getBoxLength() {
        return boxLength;
    }

    public List<Rechteck> getRechtecke() {
        return rechtecke;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    /**
     * Platzierungsalgorithmus mittels First-Fit: Geht durch alle Rechtecke und
     * versucht, sie in bereits existierenden Boxen zu platzieren. Wenn nicht
     * möglich,
     * wird eine neue Box angelegt.
     */
    public void platzieren() {
        boxes.clear();
        for (Rechteck r : rechtecke) {
            boolean placed = false;
            for (Box box : boxes) {
                if (box.addRechteck(r)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                Box newBox = new Box(boxLength);
                newBox.addRechteck(r);
                boxes.add(newBox);
            }
        }
    }

    @Override
    public double evaluate(ProblemInstanz solution) {
        return solution.getBoxes().size();
    }

    @Override
    public ProblemInstanz generateInitialSolution() {
        platzieren();
        return this;
    }

    /**
     * Erzeugt eine zufällige, aber gültige Lösung:
     * Für jedes Rechteck wird versucht, in einer existierenden Box eine zufällige
     * Position
     * zu finden, an der es kollisionsfrei platziert werden kann. Falls nicht, wird
     * eine neue Box angelegt.
     *
     * @param maxAttemptsPerRect maximale Anzahl Versuche pro Rechteck in einer Box
     */
    public void generateRandomFeasibleSolution(int maxAttemptsPerRect) {
        boxes.clear();
        Random rand = new Random();
        // Wir fügen Rechtecke nicht alle in eine einzige Box ein, sondern versuchen,
        // sie in existierenden Boxen einzupacken.
        for (Rechteck rect : rechtecke) {
            boolean placed = false;
            // Versuche, rect in eine der existierenden Boxen zu platzieren
            for (Box box : boxes) {
                boolean success = false;
                for (int attempt = 0; attempt < maxAttemptsPerRect; attempt++) {
                    int x = rand.nextInt(boxLength - rect.getWidth() + 1);
                    int y = rand.nextInt(boxLength - rect.getHeight() + 1);
                    if (canPlaceRectInBox(box, rect, x, y)) {
                        rect.setPosition(x, y);
                        box.getRechtecke().add(rect);
                        success = true;
                        break;
                    }
                }
                if (success) {
                    placed = true;
                    break;
                }
            }
            // Falls rect in keiner existierenden Box platziert werden konnte:
            if (!placed) {
                Box newBox = new Box(boxLength);
                boolean success = false;
                for (int attempt = 0; attempt < maxAttemptsPerRect; attempt++) {
                    int x = rand.nextInt(boxLength - rect.getWidth() + 1);
                    int y = rand.nextInt(boxLength - rect.getHeight() + 1);
                    if (canPlaceRectInBox(newBox, rect, x, y)) {
                        rect.setPosition(x, y);
                        newBox.getRechtecke().add(rect);
                        success = true;
                        break;
                    }
                }
                // Falls auch hier keiner gefunden wird, setze es stur bei (0,0)
                if (!success) {
                    rect.setPosition(0, 0);
                    newBox.getRechtecke().add(rect);
                }
                boxes.add(newBox);
            }
        }
    }

    /**
     * Prüft, ob rect an der Position (x,y) in der gegebenen Box platziert werden
     * kann, ohne mit
     * einem bereits platzierten Rechteck zu überlappen.
     */
    private boolean canPlaceRectInBox(Box box, Rechteck rect, int x, int y) {
        for (Rechteck placed : box.getRechtecke()) {
            if (overlap(x, y, rect.getWidth(), rect.getHeight(),
                    placed.getX(), placed.getY(), placed.getWidth(), placed.getHeight())) {
                return false;
            }
        }
        return true;
    }

    private boolean overlap(int x1, int y1, int w1, int h1,
            int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x2 < x1 + w1 &&
                y1 < y2 + h2 && y2 < y1 + h1;
    }
}
