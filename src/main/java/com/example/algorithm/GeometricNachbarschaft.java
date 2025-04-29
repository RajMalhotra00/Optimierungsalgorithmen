package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.Box;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeometricNachbarschaft implements Nachbarschaft<ProblemInstanz> {

    private final Random rng = new Random();

    // Anzahl Nachbarn, die pro Aufruf maximal zurückgeliefert werden
    private int kMax = 10;

    // maximale Verschiebung eines Rechtecks in x / y-Richtung
    private int maxShift = 6;

    public void setKMax(int k) {
        this.kMax = Math.max(1, k);
    }

    public void setMaxShift(int s) {
        this.maxShift = Math.max(1, s);
    }

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz cur) {

        int n = cur.getRechtecke().size();
        if (n == 0)
            return List.of();

        int k = Math.min(kMax, n);
        List<ProblemInstanz> nbrs = new ArrayList<>(k);

        for (int i = 0; i < k; i++) {

            // Kopie aller Rechtecke
            List<Rechteck> kopie = new ArrayList<>(n);
            for (Rechteck r : cur.getRechtecke()) {
                Rechteck c = new Rechteck(r.getWidth(), r.getHeight());
                c.setPosition(r.getX(), r.getY());
                kopie.add(c);
            }

            Rechteck r = kopie.get(rng.nextInt(n));

            /*
             * 50 %: nur leicht innerhalb seiner Box verschieben
             * 50 %: in eine (andere) Box umsetzen
             */
            if (rng.nextBoolean()) { // Variante A: shift

                int dx = rng.nextInt(maxShift * 2 + 1) - maxShift;
                int dy = rng.nextInt(maxShift * 2 + 1) - maxShift;

                int newX = clamp(r.getX() + dx, 0,
                        cur.getBoxLength() - r.getWidth());
                int newY = clamp(r.getY() + dy, 0,
                        cur.getBoxLength() - r.getHeight());
                r.setPosition(newX, newY);

            } else { // Variante B: umsetzen
                int srcBoxIdx = boxIndexOf(cur, r);
                int dstBoxIdx = rng.nextInt(cur.getBoxes().size()); // evtl. gleiche Box
                while (dstBoxIdx == srcBoxIdx && cur.getBoxes().size() > 1)
                    dstBoxIdx = rng.nextInt(cur.getBoxes().size());

                // einfache Platzierung links-oben in Zielbox
                Box dst = cur.getBoxes().get(dstBoxIdx);
                r.setPosition(0, 0);
                // Bei Kollisionen wird das spätere platzieren() umsortieren
            }

            // neue Instanz bilden & platzieren
            ProblemInstanz cand = new ProblemInstanz(cur.getBoxLength(), kopie);
            cand.platzieren(); // sorgt für konsistente Box-Liste
            nbrs.add(cand);
        }
        return nbrs;
    }

    private static int clamp(int v, int lo, int hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }

    // liefert die Index-Position der Box, in der r momentan liegt
    private static int boxIndexOf(ProblemInstanz pi, Rechteck r) {
        List<Box> boxes = pi.getBoxes();
        for (int i = 0; i < boxes.size(); i++)
            if (boxes.get(i).getRechtecke().contains(r))
                return i;
        return 0; // Fallback
    }
}
