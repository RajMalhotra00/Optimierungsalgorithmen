package com.example.interfaces;

import java.util.List;
import com.example.model.Rechteck;

public interface AuswahlStrategie<T> {
    List<T> determineOrder(List<T> elements);
}
