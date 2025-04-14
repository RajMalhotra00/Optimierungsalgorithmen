package com.example.util;

import com.example.model.Rechteck;
import com.example.model.ProblemInstanz;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InstanzGenerator {
    private Random random = new Random();

    public ProblemInstanz generateInstance(int boxLength, int anzahlRechtecke,
            int minWidth, int maxWidth,
            int minHeight, int maxHeight) {
        List<Rechteck> rechtecke = new ArrayList<>();
        for (int i = 0; i < anzahlRechtecke; i++) {
            int w = minWidth + random.nextInt(maxWidth - minWidth + 1);
            int h = minHeight + random.nextInt(maxHeight - minHeight + 1);
            rechtecke.add(new Rechteck(w, h));
        }
        // Debug-Ausgabe
        System.out.println("InstanzGenerator: Erzeugt " + rechtecke.size() + " Rechtecke. Boxlänge: " + boxLength);
        return new ProblemInstanz(boxLength, rechtecke);
    }
}
