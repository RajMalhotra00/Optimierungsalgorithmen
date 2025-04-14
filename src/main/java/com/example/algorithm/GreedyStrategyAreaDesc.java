package com.example.algorithm;

import com.example.interfaces.AuswahlStrategie;
import com.example.model.Rechteck;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GreedyStrategyAreaDesc implements AuswahlStrategie<Rechteck> {
    @Override
    public List<Rechteck> determineOrder(List<Rechteck> elements) {
        Collections.sort(elements, new Comparator<Rechteck>() {
            @Override
            public int compare(Rechteck r1, Rechteck r2) {
                return Integer.compare(r2.getWidth() * r2.getHeight(), r1.getWidth() * r1.getHeight());
            }
        });
        return elements;
    }
}
