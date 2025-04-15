package com.example.interfaces;

import java.util.List;

public interface AuswahlStrategie<T> {
    /**
     * Ordnet die Liste der Elemente in einer bestimmten Reihenfolge.
     * Diese Reihenfolge wird dann im Greedy-Algorithmus verwendet.
     */
    List<T> determineOrder(List<T> elements);
}
