package com.example.utils;

import com.example.models.Box;
import java.util.List;

public class MinimizeBoxesObjective implements ObjectiveFunction<List<Box>> {

    @Override
    public double evaluate(List<Box> solution) {
        return solution.stream().filter(box -> !box.isEmpty()).count();
    }
}
