package com.example.interfaces;

import java.util.List;

public interface AuswahlStrategie<T> {
    List<T> determineOrder(List<T> elements);
}
