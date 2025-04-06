package com.example.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.models.Box;
import com.example.models.CustomRectangle;
import com.example.utils.InstanceGenerator;
import com.example.utils.GeometricNeighborhood;
import com.example.utils.RuleBasedNeighborhood;
import com.example.utils.OverlappingNeighborhood;
import com.example.utils.Neighborhood;
import com.example.utils.MinimizeBoxesWithFreeSpaceObjective; // <-- Neue Objective-Klasse importieren
import com.example.utils.ObjectiveFunction; // Interface für die Objective
import com.example.algorithms.LocalSearch;

import java.util.List;

public class MainApplication extends Application {
    private Visualizer visualizer;
    private ComboBox<String> neighborhoodSelector;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rectangle Packing Optimizer");

        // Hauptlayout
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        // Eingabefelder
        TextField numRectanglesField = new TextField("10");
        TextField minSizeField = new TextField("1");
        TextField maxSizeField = new TextField("10");
        TextField boxSizeField = new TextField("10");

        // Dropdown für Nachbarschaften
        neighborhoodSelector = new ComboBox<>();
        neighborhoodSelector.getItems().addAll("Geometriebasiert", "Regelbasiert", "Überlappungen");
        neighborhoodSelector.setValue("Geometriebasiert"); // Standard

        // Buttons
        Button generateButton = new Button("Generate Rectangles");
        Button localSearchButton = new Button("Run Local Search");

        // Visualisierung
        visualizer = new Visualizer();

        // Event-Handling: Rechtecke generieren
        generateButton.setOnAction(e -> {
            int numRectangles = Integer.parseInt(numRectanglesField.getText());
            int minSize = Integer.parseInt(minSizeField.getText());
            int maxSize = Integer.parseInt(maxSizeField.getText());
            int boxSize = Integer.parseInt(boxSizeField.getText());

            // Erzeuge zufällige Rechtecke
            List<CustomRectangle> rectangles = InstanceGenerator.generateRectangles(numRectangles, minSize, maxSize);

            // Zeige sie (blau) in einer einfachen Reihe an
            visualizer.setRectangles(rectangles);
        });

        // Event-Handling: Lokale Suche starten
        localSearchButton.setOnAction(e -> {
            int boxSize = Integer.parseInt(boxSizeField.getText());
            List<CustomRectangle> rectangles = visualizer.getRectangles(); // Die generierten Rechtecke holen

            // Gewählte Neighborhood-Strategie bestimmen
            Neighborhood<List<Box>> neighborhood;
            String selectedStrategy = neighborhoodSelector.getValue();
            switch (selectedStrategy) {
                case "Geometriebasiert":
                    neighborhood = new GeometricNeighborhood();
                    break;
                case "Regelbasiert":
                    neighborhood = new RuleBasedNeighborhood();
                    break;
                case "Überlappungen":
                    neighborhood = new OverlappingNeighborhood(0.2);
                    break;
                default:
                    throw new IllegalArgumentException("Unbekannte Strategie");
            }

            // Lokale Suche konfigurieren: Statt MinimizeBoxesObjective nun die neue
            // Objective verwenden
            ObjectiveFunction<List<Box>> objective = new MinimizeBoxesWithFreeSpaceObjective(0.1);
            LocalSearch<List<Box>> localSearch = new LocalSearch<>(objective, neighborhood);

            // Initiallösung generieren
            List<Box> initialSolution = InstanceGenerator.generateInitialSolution(rectangles, boxSize);

            // Optimieren
            List<Box> finalSolution = localSearch.optimize(initialSolution, 100);
            for (int b = 0; b < finalSolution.size(); b++) {
                System.out.println("Box " + b
                        + " has " + finalSolution.get(b).rectangles.size()
                        + " rectangles");
            }
            // Ergebnis visualisieren
            visualizer.setSolution(finalSolution);

            // Debug-Ausgabe: verbleibende Fläche
            for (Box box : finalSolution) {
                System.out.println("Verbleibende Fläche in der Box: " + box.getRemainingArea());
            }
        });

        // Zusammenbau des Eingabe-Bereichs
        HBox inputFields = new HBox(10,
                new Label("Num Rectangles:"), numRectanglesField,
                new Label("Min Size:"), minSizeField,
                new Label("Max Size:"), maxSizeField,
                new Label("Box Size:"), boxSizeField,
                new Label("Nachbarschaft:"), neighborhoodSelector,
                generateButton, localSearchButton);

        root.getChildren().addAll(inputFields, visualizer.getPane());

        // Scene und Stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
