package com.example;

import com.example.algorithm.Greedy;
import com.example.algorithm.GreedyStrategyAreaDesc;
import com.example.algorithm.GreedyStrategyWidthAsc;
import com.example.interfaces.AuswahlStrategie;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import com.example.util.InstanzGenerator;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TestHarness {

    // Ein Tupel repräsentiert: {Anzahl Instanzen, Anzahl Rechtecke, minWidth,
    // maxWidth, minHeight, maxHeight, BoxLength}
    private static int[][] testParameters = {
            // Kleine Instanzen für schnelle Tests:
            { 5, 50, 10, 30, 10, 30, 100 },
            // Mittlere Instanzen:
            { 5, 200, 10, 50, 10, 50, 120 },
            // Große Instanzen für umfangreichere Durchläufe:
            { 3, 1000, 20, 80, 20, 80, 100 }
    };

    public static void main(String[] args) {
        // Teste nur Greedy-Algorithmen.
        List<String> algorithmNames = new ArrayList<>();
        algorithmNames.add("Greedy (Fläche absteigend)");
        algorithmNames.add("Greedy (Breite aufsteigend)");

        try (PrintWriter pw = new PrintWriter(new FileWriter("testResults.csv"))) {
            pw.println(
                    "AnzahlInstanzen,AnzahlRechtecke,minWidth,maxWidth,minHeight,maxHeight,BoxLength,Algorithmus,AvgBoxes,AvgTimeMs");

            for (int[] params : testParameters) {
                int numInstances = params[0];
                int numRectangles = params[1];
                int minWidth = params[2];
                int maxWidth = params[3];
                int minHeight = params[4];
                int maxHeight = params[5];
                int boxLength = params[6];

                for (String algoName : algorithmNames) {
                    double totalBoxes = 0;
                    double totalTimeMs = 0;

                    for (int i = 0; i < numInstances; i++) {
                        InstanzGenerator generator = new InstanzGenerator();
                        ProblemInstanz instance = generator.generateInstance(boxLength, numRectangles, minWidth,
                                maxWidth, minHeight, maxHeight);

                        // Erzeuge die Standardlösung über die initiale Platzierung.
                        instance.generateInitialSolution();

                        long startTime = System.nanoTime();
                        ProblemInstanz improved;
                        if (algoName.equals("Greedy (Fläche absteigend)")) {
                            AuswahlStrategie<Rechteck> strategy = new GreedyStrategyAreaDesc();
                            improved = new Greedy(instance, strategy).run(instance);
                        } else if (algoName.equals("Greedy (Breite aufsteigend)")) {
                            AuswahlStrategie<Rechteck> strategy = new GreedyStrategyWidthAsc();
                            improved = new Greedy(instance, strategy).run(instance);
                        } else {
                            improved = instance;
                        }
                        long duration = System.nanoTime() - startTime;
                        double timeMs = duration / 1_000_000.0;

                        totalBoxes += improved.getBoxes().size();
                        totalTimeMs += timeMs;
                    }
                    double avgBoxes = totalBoxes / numInstances;
                    double avgTime = totalTimeMs / numInstances;
                    System.out.println("Parameter: " + numRectangles + " Rechtecke, BoxLänge " + boxLength +
                            ", Algorithmus: " + algoName +
                            " -> Durchschnittliche Boxen: " + avgBoxes +
                            ", Durchschnittliche Zeit: " + avgTime + " ms");

                    pw.println(numInstances + "," + numRectangles + "," + minWidth + "," + maxWidth + "," +
                            minHeight + "," + maxHeight + "," + boxLength + "," + algoName + "," +
                            avgBoxes + "," + avgTime);
                }
            }
        } catch (IOException ex) {
            System.err.println("Fehler beim Schreiben der Ergebnisse: " + ex.getMessage());
        }
    }
}
