package com.example.gui;

import com.example.algorithm.*;
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

public class MainGUI extends Application {

    // GUI-Elemente
    private TextField tfNumRectangles, tfMinWidth, tfMaxWidth,
            tfMinHeight, tfMaxHeight, tfBoxLength;
    private Button btnGenerateRandomSolution, btnRunAlgorithm;
    private ComboBox<String> cbAlgorithm;
    private Canvas canvas;
    private TextArea taLog;

    private ProblemInstanz currentInstance;

    @Override
    public void start(Stage primaryStage) {

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

        btnGenerateRandomSolution = new Button("Zufalls-Instanz erzeugen");
        btnRunAlgorithm = new Button("Algorithmus anwenden");
        btnRunAlgorithm.setDisable(true); // zunächst deaktiviert

        cbAlgorithm = new ComboBox<>();
        cbAlgorithm.getItems().addAll(
                "Lokale Suche – Geometriebasiert",
                "Lokale Suche – Regelbasiert",
                "Lokale Suche – Überlappungen teilweise zulassen",
                "SA-Geo-Tuned (Geometrisch, Simulated Annealing)",
                "SA-Overlap-Tuned (Überlappung, Simulated Annealing)",
                "Greedy – Strategie A (Fläche absteigend)",
                "Greedy – Strategie B (Breite aufsteigend)");
        cbAlgorithm.getSelectionModel().selectFirst();

        // Layout
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
        controls.add(cbAlgorithm, 1, 4, 3, 1);
        controls.add(btnGenerateRandomSolution, 0, 5, 2, 1);
        controls.add(btnRunAlgorithm, 2, 5, 2, 1);

        // Canvas & Log
        canvas = new Canvas(1200, 800);
        ScrollPane sc = new ScrollPane(canvas);
        sc.setPannable(true);

        taLog = new TextArea();
        taLog.setEditable(false);
        taLog.setPrefRowCount(5);

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(sc);
        root.setBottom(new VBox(5, new Label("Status:"), taLog));

        // starte gewählten Algo
        btnGenerateRandomSolution.setOnAction(e -> generateRandomFeasibleSolution());
        btnRunAlgorithm.setOnAction(e -> runSelectedAlgorithm());

        primaryStage.setTitle("OptAlgos Rechteckpackung");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }

    // erzeuge Instamz
    private void generateRandomFeasibleSolution() {
        try {
            int n = Integer.parseInt(tfNumRectangles.getText());
            int minW = Integer.parseInt(tfMinWidth.getText());
            int maxW = Integer.parseInt(tfMaxWidth.getText());
            int minH = Integer.parseInt(tfMinHeight.getText());
            int maxH = Integer.parseInt(tfMaxHeight.getText());
            int box = Integer.parseInt(tfBoxLength.getText());

            InstanzGenerator gen = new InstanzGenerator();
            currentInstance = gen.generateInstance(box, n, minW, maxW, minH, maxH);
            currentInstance.generateRandomFeasibleSolution(50);

            btnRunAlgorithm.setDisable(false); // jetzt freischalten
            log("Neue Zufalls-Instanz: " + currentInstance.getBoxes().size() + " Boxen");
            drawInstance(currentInstance);

        } catch (NumberFormatException ex) {
            showAlert("Ungültige Eingabe", "Bitte nur numerische Werte eingeben.");
        }
    }

    private void runSelectedAlgorithm() {
        if (currentInstance == null)
            return;

        String sel = cbAlgorithm.getValue();
        log("Starte \"" + sel + "\" …");
        int before = currentInstance.getBoxes().size();

        ProblemInstanz improved = null;
        long t0 = System.nanoTime();

        // Simulated-Annealing
        if (sel.startsWith("SA-Geo-Tuned")) {
            var nb = new GeometricNachbarschaftTuned();
            nb.setMaxNeighbors(14);
            var sa = new SimulatedAnnealingSuche<>(currentInstance, nb);
            improved = sa.run(currentInstance);

        } else if (sel.startsWith("SA-Overlap-Tuned")) {
            var nb = new OverlapTolerantNachbarschaftTuned(1.0);
            var sa = new SimulatedAnnealingSuche<>(currentInstance, nb);
            improved = sa.run(currentInstance);

            // Lokale Suche
        } else if (sel.startsWith("Lokale Suche")) {

            if (sel.contains("Geometriebasiert"))
                improved = new LokaleSuche<>(currentInstance, new GeometricNachbarschaft()).run(currentInstance);

            else if (sel.contains("Regelbasiert"))
                improved = new LokaleSuche<>(currentInstance, new PermutationsNachbarschaft()).run(currentInstance);

            else if (sel.contains("Überlappungen"))
                improved = new LokaleSuche<>(currentInstance,
                        new OverlapTolerantNachbarschaft(1.0)).run(currentInstance);

            // Greedy
        } else if (sel.startsWith("Greedy")) {
            AuswahlStrategie<Rechteck> strat = sel.contains("Strategie A")
                    ? new GreedyStrategyAreaDesc()
                    : new GreedyStrategyWidthAsc();
            improved = new Greedy(currentInstance, strat).run(currentInstance);
        }

        long ms = (System.nanoTime() - t0) / 1_000_000;
        if (improved != null) {
            log("Fertig in " + ms + " ms – Boxen: " + before + " → " + improved.getBoxes().size());
            currentInstance = improved;
            drawInstance(currentInstance);
        } else {
            log("Algorithmus nicht ausgeführt.");
        }
    }

    private void drawInstance(ProblemInstanz inst) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double x = 10, y = 10, gap = 20;
        for (Box b : inst.getBoxes()) {
            g.strokeRect(x, y, b.getSideLength(), b.getSideLength());
            for (Rechteck r : b.getRechtecke())
                g.strokeRect(x + r.getX(), y + r.getY(), r.getWidth(), r.getHeight());

            x += b.getSideLength() + gap;
            if (x + b.getSideLength() > canvas.getWidth()) {
                x = 10;
                y += b.getSideLength() + gap;
            }
        }
    }

    private void log(String msg) {
        taLog.appendText(msg + "\n");
    }

    private void showAlert(String t, String m) {
        new Alert(Alert.AlertType.WARNING, m, ButtonType.OK) {
            {
                setTitle(t);
                showAndWait();
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
