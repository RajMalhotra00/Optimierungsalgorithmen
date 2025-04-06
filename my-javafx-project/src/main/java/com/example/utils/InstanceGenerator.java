package com.example.utils;

import com.example.models.Box;
import com.example.models.CustomRectangle;
import com.example.models.Box;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class InstanceGenerator {

    /**
     * Generates a list of rectangles with random dimensions.
     *
     * @param count   Number of rectangles to generate.
     * @param minSize Minimum size of width and height.
     * @param maxSize Maximum size of width and height.
     * @return A list of randomly generated rectangles.
     */
    public static List<CustomRectangle> generateRectangles(int count, int minSize, int maxSize) {
        Random rand = new Random();
        List<CustomRectangle> rectangles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int width = rand.nextInt(maxSize - minSize + 1) + minSize;
            int height = rand.nextInt(maxSize - minSize + 1) + minSize;
            rectangles.add(new CustomRectangle(width, height));
        }
        return rectangles;
    }

    /**
     * Generates an initial solution where each rectangle is placed in its own box.
     *
     * @param rectangles List of rectangles to be placed.
     * @param boxSize    Size of each box.
     * @return A list of boxes, each containing one rectangle.
     */
    public static List<Box> generateInitialSolution(List<CustomRectangle> rectangles, int boxSize) {
        List<Box> solution = new ArrayList<>();
        for (CustomRectangle rect : rectangles) {
            Box box = new Box(boxSize);
            box.addRectangle(rect);
            solution.add(box);
        }
        return solution;
    }
}
