package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverlapTolerantNachbarschaftTuned
        implements Nachbarschaft<ProblemInstanz> {

    private final Random rng = new Random();

    /** Momentan erlaubtes Überlappungs-Verhältnis (0‥1) */
    private double tolerance;

    /** Anzahl Nachbarn, die pro Aufruf maximal erzeugt werden */
    private int nachbarnProAufruf = 12;

    // ────────── ctor / Getter / Setter ──────────
    public OverlapTolerantNachbarschaftTuned(double tol) {
        this.tolerance = tol;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tol) {
        this.tolerance = tol;
    }

    public void setNachbarnProAufruf(int k) {
        this.nachbarnProAufruf = k;
    }

    // ────────── Kernfunktion ──────────
    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz current) {

        List<ProblemInstanz> neighbors = new ArrayList<>();
        int nRects = current.getRechtecke().size();
        if (nRects == 0)
            return neighbors;

        int tries = Math.min(nachbarnProAufruf, nRects);

        for (int t = 0; t < tries; t++) {

            /* 1) tiefe Kopie … */
            ProblemInstanz cand = deepCopy(current);

            /* 2) … genau ein Rechteck leicht verschieben */
            Rechteck r = cand.getRechtecke().get(rng.nextInt(nRects));
            int dx = rng.nextInt(11) - 5; // −5 … +5
            int dy = rng.nextInt(11) - 5;

            int newX = Math.max(0,
                    Math.min(cand.getBoxLength() - r.getWidth(), r.getX() + dx));
            int newY = Math.max(0,
                    Math.min(cand.getBoxLength() - r.getHeight(), r.getY() + dy));
            r.setPosition(newX, newY);

            /* 3) nur die Toleranz setzen – KEIN Neu­platzieren! */
            cand.setTolerance(tolerance);

            neighbors.add(cand); // SA entscheidet später
        }
        return neighbors;
    }

    // ────────── Hilfsfunktion ──────────
    private ProblemInstanz deepCopy(ProblemInstanz orig) {
        List<Rechteck> kopie = new ArrayList<>();
        for (Rechteck r : orig.getRechtecke()) {
            Rechteck c = new Rechteck(r.getWidth(), r.getHeight());
            c.setPosition(r.getX(), r.getY());
            kopie.add(c);
        }
        ProblemInstanz pi = new ProblemInstanz(orig.getBoxLength(), kopie);
        pi.setTolerance(orig.getTolerance());
        pi.platzieren(); // Box-Struktur übernehmen
        return pi;
    }
}
