package com.example.interfaces;

import java.util.List;
import com.example.model.Rechteck;

/*
 * in welcher Reihenfolge werden Rechtecke ausgewählt
 * Liefert Liste in neuer sortierten reihenfolge zurück 
 */
public interface AuswahlStrategie<T> {
    List<T> determineOrder(List<T> elements);
}
