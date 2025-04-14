package com.example.interfaces;

import java.util.List;

public interface Nachbarschaft<S> {
    List<S> getNeighbors(S currentSolution);
}
