package com.example.algorithm;

import com.example.interfaces.Nachbarschaft;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;

import java.util.*;

// Permutations-Nachbarschaft (Basis + Tuned)
public class PermutationsNachbarschaft implements Nachbarschaft<ProblemInstanz> {

    private int maxNeighbors = 20;
    private int maxShift = 3;
    private Move moveType = Move.SWAP;
    private final Random rng = new Random();

    public enum Move {
        SWAP, INSERT, BOTH
    }

    public void setMaxNeighbors(int k) {
        maxNeighbors = Math.max(2, k);
    }

    public void setMaxShift(int s) {
        maxShift = Math.max(1, s);
    }

    public void setMoveType(Move m) {
        moveType = m;
    }

    @Override
    public List<ProblemInstanz> getNeighbors(ProblemInstanz cur) {

        int n = cur.getRechtecke().size();
        if (n < 2)
            return List.of();

        List<Integer> idxPool = new ArrayList<>();
        for (int i = 0; i < n; i++)
            idxPool.add(i);
        Collections.shuffle(idxPool, rng);

        // nicht mehr als maxNeighb und nicht mehr als alle möglichen Paare
        int k = Math.min(maxNeighbors, n * (n - 1) / 2);
        List<ProblemInstanz> out = new ArrayList<>(k);

        for (int c = 0; c < k; c++) {

            List<Rechteck> perm = new ArrayList<>(cur.getRechtecke()); // shallow copy
            int i = idxPool.get(rng.nextInt(n)), j;

            if (moveType == Move.SWAP || (moveType == Move.BOTH && rng.nextBoolean())) {
                do {
                    j = rng.nextInt(n);
                } while (j == i);
                Collections.swap(perm, i, j);

            } else { // INSERT
                int shift = rng.nextInt(maxShift * 2 + 1) - maxShift;
                j = clamp(i + shift, 0, n - 1);
                if (i != j)
                    perm.add(j, perm.remove(i));
            }

            ProblemInstanz cand = new ProblemInstanz(cur.getBoxLength(), perm);
            cand.getBoxes().addAll(cur.getBoxes()); // Box-Liste übernehmen
            cand.fastRepack(i, j, perm); // nur zwei Rechtecke neu packen
            out.add(cand);
        }
        return out;
    }

    private static int clamp(int v, int lo, int hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }
}
