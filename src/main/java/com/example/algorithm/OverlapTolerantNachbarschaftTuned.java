package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tuned neighbourhood:
 * kopiere nur die Positions­daten (O(#Rects))
 * kein platzieren()-Aufruf
 * adaptiver Schritt­weite & Nachbar­anzahl
 */
public class OverlapTolerantNachbarschaftTuned
        implements Nachbarschaft<ProblemInstanz> {

    private final Random rng = new Random();
    private double tolerance;
    private int kMax = 10;
    private int maxShift = 4;

    public OverlapTolerantNachbarschaftTuned(double tol) {
        this.tolerance = tol;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double t) {
        tolerance = t;
    }

    public void setNachbarnProAufruf(int k) {
        kMax = k;
    }

    public void setMaxShift(int s) {
        maxShift = s;
    }

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz cur) {

        int n = cur.getRechtecke().size();
        if (n == 0)
            return List.of();

        int k = Math.min(kMax, n);
        List<ProblemInstanz> nbrs = new ArrayList<>(k);

        for (int i = 0; i < k; i++) {

            // flache Kopie
            List<Rechteck> list = new ArrayList<>(n);
            for (Rechteck r : cur.getRechtecke()) {
                Rechteck c = new Rechteck(r.getWidth(), r.getHeight());
                c.setPosition(r.getX(), r.getY());
                list.add(c);
            }

            // ein Rechteck verschieben
            Rechteck r = list.get(rng.nextInt(n));
            int dx = rng.nextInt(maxShift * 2 + 1) - maxShift;
            int dy = rng.nextInt(maxShift * 2 + 1) - maxShift;
            int newX = clamp(r.getX() + dx, 0, cur.getBoxLength() - r.getWidth());
            int newY = clamp(r.getY() + dy, 0, cur.getBoxLength() - r.getHeight());
            r.setPosition(newX, newY);

            // Kandidat erzeugen + Platzierung mit aktueller Toleranz
            ProblemInstanz cand = new ProblemInstanz(cur.getBoxLength(), list);
            cand.setTolerance(tolerance);
            cand.platzierenMitToleranz(tolerance);
            nbrs.add(cand);
        }

        if (tolerance < 0.6 && kMax > 6)
            kMax = 6;
        if (tolerance < 0.4 && maxShift > 2)
            maxShift = 2;

        return nbrs;
    }

    private static int clamp(int v, int lo, int hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }

}
