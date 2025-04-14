package com.example;

import com.example.algorithm.GeometricNachbarschaft;
import com.example.algorithm.Greedy;
import com.example.algorithm.GreedyStrategyAreaDesc;
import com.example.algorithm.LokaleSuche;
import com.example.interfaces.AuswahlStrategie;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import com.example.util.InstanzGenerator;

/*
 * starte den Test mit dem Befehl mvn exec:java -Dexec.mainClass="com.example.TestHarness"
 */
public class TestHarness {

    public static void main(String[] args) {
        // Hier verwendest du anspruchsvollere Parameter:
        int numTests = 50; // Mehr Durchläufe für statistische Aussagekraft
        int boxLength = 120; // Kleinere Box -> weniger Platz
        int anzahlRechtecke = 1000; // Mehr Rechtecke, um den Suchraum zu vergrößern
        int minWidth = 10, maxWidth = 50; // Rechtecke sind größer
        int minHeight = 10, maxHeight = 50;

        InstanzGenerator generator = new InstanzGenerator();

        // --- Test für Standardinitialisierung (first-fit Platzierung) mit
        // anspruchsvolleren Parametern ---
        double totalInitialLS = 0.0, totalImprovedLS = 0.0;
        long totalTimeLS = 0;
        for (int i = 0; i < numTests; i++) {
            ProblemInstanz instanceLS = generator.generateInstance(boxLength, anzahlRechtecke, minWidth, maxWidth,
                    minHeight, maxHeight);
            // Verwende die Standardinitialisierung (first-fit), die hier bewusst suboptimal
            // sein kann
            instanceLS.generateInitialSolution();
            double initialQuality = instanceLS.evaluate(instanceLS);
            totalInitialLS += initialQuality;

            long startTime = System.nanoTime();
            LokaleSuche<ProblemInstanz> ls = new LokaleSuche<>(instanceLS, new GeometricNachbarschaft());
            ProblemInstanz improvedLS = ls.run(instanceLS);
            long duration = System.nanoTime() - startTime;
            totalTimeLS += duration;

            double improvedQuality = improvedLS.evaluate(improvedLS);
            totalImprovedLS += improvedQuality;
        }
        System.out.println("Lokale Suche (Standardinitialisierung):");
        System.out.println("  Durchschnittliche initiale Anzahl Boxen: " + (totalInitialLS / numTests));
        System.out.println("  Durchschnittliche verbesserte Anzahl Boxen: " + (totalImprovedLS / numTests));
        System.out.println("  Durchschnittliche Laufzeit (ms): " + ((totalTimeLS / numTests) / 1_000_000.0));

        // --- Test für Worst-Case-Initialisierung: jedes Rechteck in eigene Box ---
        double totalInitialLS_bad = 0.0, totalImprovedLS_bad = 0.0;
        long totalTimeLS_bad = 0;
        for (int i = 0; i < numTests; i++) {
            ProblemInstanz instanceBad = generator.generateInstance(boxLength, anzahlRechtecke, minWidth, maxWidth,
                    minHeight, maxHeight);
            instanceBad = generateWorstCaseSolution(instanceBad); // Worst-Case erzwingen
            double initialQuality_bad = instanceBad.evaluate(instanceBad);
            totalInitialLS_bad += initialQuality_bad;

            long startTime = System.nanoTime();
            LokaleSuche<ProblemInstanz> lsBad = new LokaleSuche<>(instanceBad, new GeometricNachbarschaft());
            ProblemInstanz improvedLS_bad = lsBad.run(instanceBad);
            long duration = System.nanoTime() - startTime;
            totalTimeLS_bad += duration;

            double improvedQuality_bad = improvedLS_bad.evaluate(improvedLS_bad);
            totalImprovedLS_bad += improvedQuality_bad;
        }
        System.out.println("\nLokale Suche (Worst-Case Initialisierung):");
        System.out.println("  Durchschnittliche initiale Anzahl Boxen: " + (totalInitialLS_bad / numTests));
        System.out.println("  Durchschnittliche verbesserte Anzahl Boxen: " + (totalImprovedLS_bad / numTests));
        System.out.println("  Durchschnittliche Laufzeit (ms): " + ((totalTimeLS_bad / numTests) / 1_000_000.0));
    }

    // Methode zur Erzeugung einer Worst-Case-Lösung, bei der jedes Rechteck in eine
    // eigene Box kommt.
    private static ProblemInstanz generateWorstCaseSolution(ProblemInstanz instance) {
        instance.getBoxes().clear();
        for (Rechteck r : instance.getRechtecke()) {
            // Erzeuge für jedes Rechteck eine neue Box
            instance.getBoxes().add(new com.example.model.Box(instance.getBoxLength()));
            // Füge das Rechteck in diese eigene Box ein.
            instance.getBoxes().get(instance.getBoxes().size() - 1).addRechteck(r);
        }
        return instance;
    }
}
