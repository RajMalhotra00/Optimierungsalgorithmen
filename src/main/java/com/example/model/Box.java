package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private int sideLength;
    private List<Rechteck> rechtecke;

    public Box(int sideLength) {
        this.sideLength = sideLength;
        this.rechtecke = new ArrayList<>();
    }

    public int getSideLength() {
        return sideLength;
    }

    public List<Rechteck> getRechtecke() {
        return rechtecke;
    }

    public boolean addRechteck(Rechteck r) {
        for (int x = 0; x <= sideLength - r.getWidth(); x++) {
            for (int y = 0; y <= sideLength - r.getHeight(); y++) {
                if (canPlaceAt(r, x, y)) {
                    r.setPosition(x, y);
                    rechtecke.add(r);
                    // Debug-Ausgabe: Welche Position wurde gewählt?
                    System.out.println("Box.addRechteck: Rechteck platziert bei (" + x + ", " + y
                            + ") in Box mit Seite " + sideLength);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canPlaceAt(Rechteck r, int x, int y) {
        for (Rechteck placed : rechtecke) {
            if (overlap(x, y, r.getWidth(), r.getHeight(),
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
