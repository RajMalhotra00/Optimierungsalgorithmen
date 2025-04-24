package com.example.model;

import java.util.*;

public class BoxPenaltyCached extends Box {

    private final List<Double> overlapCache = new ArrayList<>();

    public BoxPenaltyCached(int side) {
        super(side);
    }

    @Override
    public boolean addRechteck(Rechteck r) {
        if (!super.addRechteck(r))
            return false;

        for (Rechteck other : getRechtecke()) {
            if (other == r)
                continue;
            overlapCache.add(
                    overlapRatio(r.getX(), r.getY(), r.getWidth(), r.getHeight(),
                            other.getX(), other.getY(), other.getWidth(), other.getHeight()));
        }
        return true;
    }

    public double penalty(double tol) {
        double p = 0.0;
        for (double ratio : overlapCache) {
            if (ratio > tol)
                p += ratio;
        }
        return p;
    }

    private static double overlapRatio(int x1, int y1, int w1, int h1,
            int x2, int y2, int w2, int h2) {
        int xOv = Math.max(0, Math.min(x1 + w1, x2 + w2) - Math.max(x1, x2));
        int yOv = Math.max(0, Math.min(y1 + h1, y2 + h2) - Math.max(y1, y2));
        int areaOv = xOv * yOv;
        if (areaOv == 0)
            return 0.0;
        int maxA = Math.max(w1 * h1, w2 * h2);
        return (double) areaOv / maxA;
    }
}
