package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.Box;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;

import java.util.*;

/**
 * Schnelle geometrie-Nachbarschaft
 *
 * arbeitet nur mit shallow-Kopien (keine neuen Rechteck-Objekte)
 * keine Aufrufe von platzieren(); Box-Liste wird direkt nachgeführt
 */
public class GeometricNachbarschaftTuned
        implements Nachbarschaft<ProblemInstanz> {

    private final Random rng = new Random();

    private int maxNeighbors = 12;
    private int rasterStep = 4; // Raster für Zielposition
    private int maxShift = 6;

    private List<Rechteck> cachedPool = null;

    public void setPreSortedPool(List<Rechteck> pool) { // einmalig vorm SA setzen
        cachedPool = pool;
    }

    public void setMaxNeighbors(int k) {
        maxNeighbors = Math.max(2, k);
    }

    public void setRasterStep(int s) {
        rasterStep = Math.max(1, s);
    }

    public void setMaxShift(int s) {
        maxShift = Math.max(1, s);
    }

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz cur) {

        if (cur.getRechtecke().isEmpty())
            return List.of();

        List<Rechteck> pool;
        if (cachedPool == null) {
            pool = new ArrayList<>(cur.getRechtecke());
            pool.sort(Comparator.<Rechteck>comparingInt(r -> boxLoad(cur, r)).reversed()); // sortiere absteigend nach
                                                                                           // anzahl rechtecke in akt.
                                                                                           // Box --> volle Boxen zuerst
                                                                                           // berarbeiten
        } else { // einmalig vorsortiert
            pool = cachedPool;
        }

        int k = Math.min(maxNeighbors, pool.size());
        List<ProblemInstanz> out = new ArrayList<>(k);

        for (int idx = 0; idx < k; idx++) {

            // Shallow Kopien
            List<Rechteck> rectRefCopy = new ArrayList<>(cur.getRechtecke()); // O(1)
            List<Box> boxRefCopy = shallowCopyBoxes(cur); // O(#Boxen)

            Rechteck movedOrig = pool.get(idx); // Original-Ref
            Rechteck moved = movedOrig;

            if (rng.nextBoolean()) { // Variante A: kleiner Shift
                int dx = rng.nextInt(maxShift * 2 + 1) - maxShift;
                int dy = rng.nextInt(maxShift * 2 + 1) - maxShift;

                moved.setPosition(
                        clamp(moved.getX() + dx, 0, cur.getBoxLength() - moved.getWidth()),
                        clamp(moved.getY() + dy, 0, cur.getBoxLength() - moved.getHeight()));

            } else { // Variante B: andere Box
                Box src = boxOf(boxRefCopy, moved);
                Box dst = randomOtherBox(boxRefCopy, src);

                src.getRechtecke().remove(moved);
                rasterPlace(moved, dst.getSideLength(), cur.getBoxLength(), rasterStep);
                dst.getRechtecke().add(moved);
            }

            // neue Instanz zusammenstecken
            ProblemInstanz cand = new ProblemInstanz(cur.getBoxLength(), rectRefCopy);
            cand.getBoxes().addAll(boxRefCopy); // kein platzieren()
            out.add(cand);
        }
        return out;
    }

    // Hilfsfunktionen
    private static List<Box> shallowCopyBoxes(ProblemInstanz cur) {
        List<Box> copy = new ArrayList<>(cur.getBoxes().size());
        for (Box b : cur.getBoxes()) {
            Box nb = new Box(b.getSideLength());
            nb.getRechtecke().addAll(b.getRechtecke()); // nur Referenzen
            copy.add(nb);
        }
        return copy;
    }

    private static Box boxOf(List<Box> boxes, Rechteck r) {
        for (Box b : boxes)
            if (b.getRechtecke().contains(r))
                return b;
        return boxes.get(0);
    }

    private Box randomOtherBox(List<Box> boxes, Box src) {
        if (boxes.size() == 1)
            return src;
        Box dst;
        do
            dst = boxes.get(rng.nextInt(boxes.size()));
        while (dst == src);
        return dst;
    }

    private static int boxLoad(ProblemInstanz pi, Rechteck r) {
        for (Box b : pi.getBoxes())
            if (b.getRechtecke().contains(r))
                return b.getRechtecke().size();
        return 0;
    }

    private static boolean rasterPlace(Rechteck r, int side, int boxLen, int step) {
        int offX = new Random().nextInt(step), offY = new Random().nextInt(step);
        for (int y = offY; y <= side - r.getHeight(); y += step)
            for (int x = offX; x <= side - r.getWidth(); x += step) {
                if (x + r.getWidth() <= boxLen && y + r.getHeight() <= boxLen) {
                    r.setPosition(x, y);
                    return true;
                }
            }
        return false;
    }

    private static int clamp(int v, int lo, int hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }
}
