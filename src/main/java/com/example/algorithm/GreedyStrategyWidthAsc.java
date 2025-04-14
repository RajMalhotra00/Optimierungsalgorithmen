package com.example.algorithm;

import com.example.interfaces.AuswahlStrategie;
import com.example.model.Rechteck;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GreedyStrategyWidthAsc implements AuswahlStrategie<Rechteck> {
    @Override
    public List<Rechteck> determineOrder(List<Rechteck> elements) {
        Collections.sort(elements, new Comparator<Rechteck>() {
            @Override
            public int compare(Rechteck r1, Rechteck r2) {
                if (r1.getWidth() == r2.getWidth()) {
                    return Integer.compare(r2.getHeight(), r1.getHeight());
                }
                return Integer.compare(r1.getWidth(), r2.getWidth());
            }
        });
        return elements;
    }
}
