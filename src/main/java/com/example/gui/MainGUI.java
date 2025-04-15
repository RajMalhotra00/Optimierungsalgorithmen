package com.example.gui;

import com.example.algorithm.GeometricNachbarschaft;
import com.example.algorithm.LokaleSuche;
import com.example.algorithm.PermutationsNachbarschaft;
import com.example.algorithm.OverlapTolerantNachbarschaft;
import com.example.algorithm.Greedy;
import com.example.algorithm.GreedyStrategyAreaDesc;
import com.example.algorithm.GreedyStrategyWidthAsc;
import com.example.interfaces.AuswahlStrategie;
import com.example.model.Box;
import com.example.model.ProblemInstanz;
import com.example.model.Rechteck;
import com.example.util.InstanzGenerator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Collections;

public class MainGUI extends Application {

    // Eingabefelder und Buttons
    private TextField tfNumRectangles, tfMinWidth, tfMaxWidth, tfMinHeight, tfMaxHeight, tfBoxLength;
    private Button btnGenerate, btnPoorSolution, btnGenerateRandomSolution, btnRunAlgorithm;
    private ComboBox<String> cbAlgorithm;
    private Canvas canvas;
    private TextArea taLog;

    // Aktuelle Probleminstanz
    private ProblemInstanz currentInstance;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Obere Steuerung: GridPane
        GridPane controls = new GridPane();
        controls.setHgap(10);
        controls.setVgap(10);
        controls.setPadding(new Insets(10));

        tfNumRectangles = new TextField("500");
        tfMinWidth = new TextField("10");
        tfMaxWidth = new TextField("50");
        tfMinHeight = new TextField("10");
        tfMaxHeight = new TextField("50");
        tfBoxLength = new TextField("120");

        btnGenerate = new Button("Instanz generieren");
        btnPoorSolution = new Button("Suboptimale Lösung (Shuffle)");
        btnGenerateRandomSolution = new Button("Zufallsfeasible Lösung generieren");
        btnRunAlgorithm = new Button("Algorithmus anwenden");

        cbAlgorithm = new ComboBox<>();
        cbAlgorithm.getItems().addAll(
                "Lokale Suche – Geometriebasiert",
                "Lokale Suche – Regelbasiert",
                "Lokale Suche – Überlappungen teilweise zulassen",
                "Greedy – Strategie A (Fläche absteigend)",
                "Greedy – Strategie B (Breite aufsteigend)");
        cbAlgorithm.getSelectionModel().selectFirst();

        // Steuerelemente in das GridPane einfügen
        controls.add(new Label("Anzahl Rechtecke:"), 0, 0);
        controls.add(tfNumRectangles, 1, 0);
        controls.add(new Label("Min. Breite:"), 0, 1);
        controls.add(tfMinWidth, 1, 1);
        controls.add(new Label("Max. Breite:"), 2, 1);
        controls.add(tfMaxWidth, 3, 1);
        controls.add(new Label("Min. Höhe:"), 0, 2);
        controls.add(tfMinHeight, 1, 2);
        controls.add(new Label("Max. Höhe:"), 2, 2);
        controls.add(tfMaxHeight, 3, 2);
        controls.add(new Label("Boxlänge:"), 0, 3);
        controls.add(tfBoxLength, 1, 3);
        controls.add(new Label("Algorithmus:"), 0, 4);
        controls.add(cbAlgorithm, 1, 4, 2, 1);
        controls.add(btnGenerate, 0, 5);
        controls.add(btnPoorSolution, 1, 5);
        controls.add(btnGenerateRandomSolution, 2, 5);
        controls.add(btnRunAlgorithm, 3, 5);

        root.setTop(controls);

        // Canvas in einem ScrollPane, um großen Inhalt scrollen zu können
        canvas = new Canvas(1200, 800);
        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setPannable(true);
        root.setCenter(scrollPane);

        // Log-/Statusbereich am unteren Rand
        taLog = new TextArea();
        taLog.setEditable(false);
        taLog.setPrefRowCount(5);
        taLog.setWrapText(true);
        VBox bottomBox = new VBox(5, new Label("Status:"), taLog);
        bottomBox.setPadding(new Insets(10));
        root.setBottom(bottomBox);

        // Action-Handler
        btnGenerate.setOnAction(e -> generateInstance());
        btnPoorSolution.setOnAction(e -> generatePoorSolution());
        btnGenerateRandomSolution.setOnAction(e -> generateRandomFeasibleSolution());
        btnRunAlgorithm.setOnAction(e -> runSelectedAlgorithm());

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("OptAlgos Rechteckpackung");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Generiert eine Standardinstanz und zeigt diese
    private void generateInstance() {
        try {
            int numRect = Integer.parseInt(tfNumRectangles.getText());
            int minW = Integer.parseInt(tfMinWidth.getText());
            int maxW = Integer.parseInt(tfMaxWidth.getText());
            int minH = Integer.parseInt(tfMinHeight.getText());
            int maxH = Integer.parseInt(tfMaxHeight.getText());
            int boxLength = Integer.parseInt(tfBoxLength.getText());

            // Debug: Werte ausgeben
            System.out.println("Eingaben: #Rechtecke=" + numRect + ", minW=" + minW +
                    ", maxW=" + maxW + ", minH=" + minH + ", maxH=" + maxH + ", boxLength=" + boxLength);

            InstanzGenerator generator = new InstanzGenerator();
            currentInstance = generator.generateInstance(boxLength, numRect, minW, maxW, minH, maxH);
            currentInstance.generateInitialSolution();
            log("Standardinstanz erzeugt. Boxen (Initial): " + currentInstance.getBoxes().size());
            drawInstance(currentInstance);
        } catch (NumberFormatException ex) {
            showAlert("Ungültige Eingabe", "Bitte stellen Sie sicher, dass alle Werte numerisch sind.");
        }
    }

    // Erzeugt eine suboptimale Lösung über einfaches Shuffle und erneutes
    // Platzieren
    private void generatePoorSolution() {
        if (currentInstance == null) {
            generateInstance();
            return;
        }
        log("Suboptimale Lösung (Shuffle) wird erzeugt...");
        Collections.shuffle(currentInstance.getRechtecke());
        currentInstance.platzieren();
        log("Suboptimale Lösung erzeugt. Boxen (Initial): " + currentInstance.getBoxes().size());
        drawInstance(currentInstance);
    }

    /**
     * Erzeugt direkt eine zufällige, aber gültige Lösung, die von Grund auf neu
     * erstellt wird. Dabei wird eine neue Instanz erzeugt und die Methode
     * generateRandomFeasibleSolution
     * aufgerufen, sodass die Startlösung direkt suboptimal ist.
     */
    private void generateRandomFeasibleSolution() {
        try {
            int numRect = Integer.parseInt(tfNumRectangles.getText());
            int minW = Integer.parseInt(tfMinWidth.getText());
            int maxW = Integer.parseInt(tfMaxWidth.getText());
            int minH = Integer.parseInt(tfMinHeight.getText());
            int maxH = Integer.parseInt(tfMaxHeight.getText());
            int boxLength = Integer.parseInt(tfBoxLength.getText());

            InstanzGenerator generator = new InstanzGenerator();
            // Erzeuge eine neue Instanz
            currentInstance = generator.generateInstance(boxLength, numRect, minW, maxW, minH, maxH);
            // Direkt die zufällige, aber zulässige Positionierung anwenden; z.B. mit 50
            // max. Versuchen pro Rechteck
            currentInstance.generateRandomFeasibleSolution(50);
            log("Zufällige, gültige Lösung erzeugt. Boxen: " + currentInstance.getBoxes().size());
            drawInstance(currentInstance);
        } catch (NumberFormatException ex) {
            showAlert("Ungültige Eingabe", "Bitte stellen Sie sicher, dass alle Werte numerisch sind.");
        }
    }

    // Wendet den in der ComboBox gewählten Algorithmus an
    private void runSelectedAlgorithm() {
        if (currentInstance == null) {
            showAlert("Keine Instanz", "Bitte zuerst eine Instanz generieren.");
            return;
        }
        String selection = cbAlgorithm.getSelectionModel().getSelectedItem();
        log("Wende Algorithmus \"" + selection + "\" an...");

        // Speichere den Zustand der Boxen vor dem Aufruf des Algorithmus
        int beforeBoxes = currentInstance.getBoxes().size();

        ProblemInstanz improved = null;
        long startTime = System.nanoTime();

        if (selection.startsWith("Lokale Suche")) {
            if (selection.contains("Geometriebasiert")) {
                improved = new LokaleSuche<>(currentInstance, new GeometricNachbarschaft()).run(currentInstance);
            } else if (selection.contains("Regelbasiert")) {
                improved = new LokaleSuche<>(currentInstance, new PermutationsNachbarschaft()).run(currentInstance);
            } else if (selection.contains("Überlappungen")) {
                improved = new LokaleSuche<>(currentInstance, new OverlapTolerantNachbarschaft(1.0))
                        .run(currentInstance);
            }
        } else if (selection.startsWith("Greedy")) {
            if (selection.contains("Strategie A")) {
                AuswahlStrategie<Rechteck> strategy = new GreedyStrategyAreaDesc();
                improved = new Greedy(currentInstance, strategy).run(currentInstance);
            } else if (selection.contains("Strategie B")) {
                AuswahlStrategie<Rechteck> strategy = new GreedyStrategyWidthAsc();
                improved = new Greedy(currentInstance, strategy).run(currentInstance);
            }
        }

        long duration = System.nanoTime() - startTime;
        if (improved != null) {
            log("Algorithmus abgeschlossen in " + (duration / 1_000_000.0) + " ms. Boxen: Vor " +
                    beforeBoxes + " – Nach " + improved.getBoxes().size());
            currentInstance = improved;
            drawInstance(currentInstance);
        } else {
            log("Kein Algorithmus ausgewählt oder Fehler in der Auswahl.");
        }
    }

    // Zeichnet die aktuelle Instanz (Boxen und Rechtecke) auf dem Canvas
    private void drawInstance(ProblemInstanz instance) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double offsetX = 10;
        double offsetY = 10;
        double spacing = 20;

        for (Box box : instance.getBoxes()) {
            gc.strokeRect(offsetX, offsetY, box.getSideLength(), box.getSideLength());
            for (Rechteck r : box.getRechtecke()) {
                gc.strokeRect(offsetX + r.getX(), offsetY + r.getY(), r.getWidth(), r.getHeight());
            }
            offsetX += box.getSideLength() + spacing;
            if (offsetX + box.getSideLength() > canvas.getWidth()) {
                offsetX = 10;
                offsetY += box.getSideLength() + spacing;
            }
        }
    }

    // Loggt Nachrichten ins TextArea
    private void log(String message) {
        taLog.appendText(message + "\n");
    }

    // Zeigt einen Alert-Dialog an
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
