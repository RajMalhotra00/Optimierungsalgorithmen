package com.example.gui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import com.example.models.Box;
import com.example.models.CustomRectangle;

import java.util.ArrayList;
import java.util.List;

public class Visualizer {
    private Pane pane;
    private List<CustomRectangle> rectangles; // Speichert die Rechtecke
    private int boxCounter = 1; // Box Nummerierung

    public Visualizer() {
        pane = new Pane();
        pane.setPrefSize(800, 400);
        rectangles = new ArrayList<>();
    }

    /**
     * Gibt die gespeicherten Rechtecke zurück.
     */
    public List<CustomRectangle> getRectangles() {
        return new ArrayList<>(rectangles); // Schutz gegen direkte Manipulation
    }

    public void setRectangles(List<CustomRectangle> rectangles) {
        this.rectangles = rectangles;
        pane.getChildren().clear();

        int x = 10, y = 10;
        for (CustomRectangle rect : rectangles) {
            Rectangle visualRect = new Rectangle(x, y, rect.width * 10, rect.height * 10);
            visualRect.setFill(Color.LIGHTBLUE);
            visualRect.setStroke(Color.BLACK);

            // Optional: Entweder ganz weglassen oder etwas anderes anzeigen:
            // Text label = new Text(x + 5, y + 15, "Rect");

            pane.getChildren().addAll(visualRect /* , label falls gewünscht */);

            x += rect.width * 10 + 10;
        }
    }

    public void setSolution(List<Box> solution) {
        pane.getChildren().clear();
        int boxOffsetX = 10;
        int boxCounter = 1;

        for (Box box : solution) {
            // Zeichne den Rahmen der Box
            Rectangle boxOutline = new Rectangle(boxOffsetX, 10,
                    box.size * 10, box.size * 10);
            boxOutline.setStroke(Color.BLACK);
            boxOutline.setFill(null);
            Text boxLabel = new Text(boxOffsetX + 5, 5, "Box " + boxCounter++);

            pane.getChildren().addAll(boxOutline, boxLabel);

            // Jetzt jedes Rechteck gem. seiner Koordinaten
            for (CustomRectangle rect : box.rectangles) {
                double drawX = boxOffsetX + rect.x * 10; // rect.x aus der Box, plus Offset
                double drawY = 10 + rect.y * 10; // rect.y aus der Box, plus Offset

                Rectangle visualRect = new Rectangle(drawX, drawY,
                        rect.width * 10, rect.height * 10);
                visualRect.setFill(Color.LIGHTGREEN);
                visualRect.setStroke(Color.BLACK);

                pane.getChildren().add(visualRect);
            }

            // Offset für die nächste Box
            boxOffsetX += box.size * 10 + 20;
        }
    }

    /**
     * Rückgabe des JavaFX-Panels für die GUI.
     */
    public Pane getPane() {
        return pane;
    }
}
