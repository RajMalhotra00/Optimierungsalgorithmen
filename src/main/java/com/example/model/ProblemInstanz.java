package com.example.model;

import com.example.interfaces.OptimierungsProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProblemInstanz implements OptimierungsProblem<ProblemInstanz> {

    private final int boxLength;
    private final List<Rechteck> rechtecke;
    private final List<Box> boxes = new ArrayList<>();

    // Toleranz-Management
    private double tolerance = 1.0; // 1.0 == 100 % Überlappung erlaubt
    private boolean toleranzEinhaltung = true;

    public ProblemInstanz(int boxLength, List<Rechteck> rechtecke) {
        this.boxLength = boxLength;
        this.rechtecke = rechtecke;
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

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double t) {
        tolerance = t;
    }

    public boolean isToleranzEinhaltung() {
        return toleranzEinhaltung;
    }

    /*
     * Klassische First-Fit-Platzierung (0 % Überl.)
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
                Box neu = new Box(boxLength);
                neu.addRechteck(r);
                boxes.add(neu);
            }
        }
    }

    /*
     * Platzierung MIT zulässiger Überlappung
     * (wird von tuned Neighborhood & SA genutzt)
     */
    public void platzierenMitToleranz(double tol) {
        boxes.clear();

        for (Rechteck r : rechtecke) {
            boolean placed = false;

            // versuche r in bestehende Boxen einzupacken
            for (Box b : boxes) {
                if (canPlaceWithTolerance(b, r, r.getX(), r.getY(), tol)) {
                    b.getRechtecke().add(r);
                    placed = true;
                    break;
                }
            }

            // wenn es nirgendwo passt --> neue Box
            if (!placed) {
                Box neu = new Box(boxLength);
                neu.getRechtecke().add(r);
                boxes.add(neu);
            }
        }

        /*
         * nachpacken zwar nicht perfekt, reicht aber,
         * damit jede Lösung eine sinnvolle Box-Liste hat
         */
    }

    private boolean tryInsertWithTolerance(Box box, Rechteck rect, double tol) {

        // brute-force einige Zufallspositionen
        Random rnd = new Random();
        for (int attempt = 0; attempt < 15; attempt++) {
            int x = rnd.nextInt(boxLength - rect.getWidth() + 1);
            int y = rnd.nextInt(boxLength - rect.getHeight() + 1);

            if (canPlaceWithTolerance(box, rect, x, y, tol)) {
                rect.setPosition(x, y);
                box.getRechtecke().add(rect);
                return true;
            }
        }

        // keine zulässige Position gefunden
        toleranzEinhaltung = false;
        return false;
    }

    /*
     * Bewertung: Box-Anzahl + harte Strafen
     */
    @Override
    public double evaluate(ProblemInstanz s) {

        double penalty = 0.0;

        for (Box b : s.getBoxes()) {

            if (b instanceof BoxPenaltyCached cached) {
                penalty += cached.penalty(s.getTolerance());
            } else {
                List<Rechteck> r = b.getRechtecke();
                for (int i = 0; i < r.size(); i++) {
                    for (int j = i + 1; j < r.size(); j++) {
                        double ratio = calculateOverlapRatio(r.get(i), r.get(j));
                        if (ratio > s.getTolerance()) {
                            penalty += ratio * 1_000.0;
                        }
                    }
                }
            }

            if (penalty > 10_000.0)
                break;
        }

        return s.getBoxes().size() + penalty;
    }

    @Override
    public ProblemInstanz generateInitialSolution() {
        platzieren();
        return this;
    }

    private boolean canPlaceWithTolerance(Box box, Rechteck rect, int x, int y, double tol) {
        for (Rechteck placed : box.getRechtecke()) {
            if (computeOverlapRatio(x, y, rect, placed) > tol)
                return false;
        }
        return true;
    }

    private double calculateOverlapRatio(Rechteck a, Rechteck b) {
        int xOv = Math.max(0,
                Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth()) - Math.max(a.getX(), b.getX()));
        int yOv = Math.max(0,
                Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight()) - Math.max(a.getY(), b.getY()));
        int ovArea = xOv * yOv;
        if (ovArea == 0)
            return 0.0;

        int maxArea = Math.max(a.getWidth() * a.getHeight(), b.getWidth() * b.getHeight());
        return (double) ovArea / maxArea;
    }

    private double computeOverlapRatio(int x, int y, Rechteck r1, Rechteck r2) {
        int xOv = Math.max(0,
                Math.min(x + r1.getWidth(), r2.getX() + r2.getWidth()) - Math.max(x, r2.getX()));
        int yOv = Math.max(0,
                Math.min(y + r1.getHeight(), r2.getY() + r2.getHeight()) - Math.max(y, r2.getY()));
        int ovArea = xOv * yOv;
        if (ovArea == 0)
            return 0.0;

        int maxArea = Math.max(r1.getWidth() * r1.getHeight(), r2.getWidth() * r2.getHeight());
        return (double) ovArea / maxArea;
    }

    public void generateRandomFeasibleSolution(int maxAttemptsPerRect) {
        boxes.clear();
        Random rand = new Random();

        for (Rechteck rect : rechtecke) {
            boolean placed = false;

            for (Box box : boxes) {
                for (int a = 0; a < maxAttemptsPerRect && !placed; a++) {
                    int x = rand.nextInt(boxLength - rect.getWidth() + 1);
                    int y = rand.nextInt(boxLength - rect.getHeight() + 1);
                    if (canPlaceWithTolerance(box, rect, x, y, 0.0)) { // 0 % Überlappung
                        rect.setPosition(x, y);
                        box.getRechtecke().add(rect);
                        placed = true;
                    }
                }
                if (placed)
                    break;
            }

            if (!placed) { // neue Box öffnen
                Box neu = new Box(boxLength);
                rect.setPosition(0, 0);
                neu.getRechtecke().add(rect);
                boxes.add(neu);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ProblemInstanz other))
            return false;

        List<Rechteck> a = this.rechtecke;
        List<Rechteck> b = other.rechtecke;
        if (a.size() != b.size())
            return false;

        for (int i = 0; i < a.size(); i++) {
            Rechteck r1 = a.get(i), r2 = b.get(i);
            if (r1.getX() != r2.getX() || r1.getY() != r2.getY())
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 17;
        for (Rechteck r : rechtecke) {
            h = 31 * h + r.getX();
            h = 31 * h + r.getY();
        }
        return h;
    }

    // packt nur die beiden vertauschten Rechtecke neu
    public void fastRepack(int i, int j, List<Rechteck> perm) {

        // beide Rechtecke aus ihren Boxen entfernen
        Rechteck a = perm.get(i), b = perm.get(j);
        removeRect(a);
        removeRect(b);

        // setze in neuer Reihenfolge wieder ein
        insertFirstFit(a);
        insertFirstFit(b);
    }

    private void removeRect(Rechteck r) {
        for (Box box : boxes)
            if (box.getRechtecke().remove(r))
                break;
    }

    private void insertFirstFit(Rechteck r) {
        for (Box box : boxes)
            if (box.addRechteck(r))
                return;
        Box neu = new Box(boxLength);
        neu.addRechteck(r);
        boxes.add(neu);
    }

}
