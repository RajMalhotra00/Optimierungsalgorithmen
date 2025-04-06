package com.example.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Box, die Rechtecke mithilfe des Shelf-Algorithmus anordnet.
 * Statt nur currentX/currentY linear hochzuzählen, organisieren wir sog.
 * "Regale".
 */
public class Box {
    public final int size;
    public List<CustomRectangle> rectangles;

    // Unsere "Regale"
    private final List<Shelf> shelves;

    public Box(int size) {
        this.size = size;
        this.rectangles = new ArrayList<>();
        this.shelves = new ArrayList<>();
    }

    /**
     * Fügt ein Rechteck nach dem Shelf-Prinzip ein, sofern möglich.
     * - Kann in das letzte vorhandene Regal passen
     * - Oder wir legen ein neues Regal darunter an
     * - Falls es insgesamt nicht (mehr) passt, return false
     */
    public boolean addRectangle(CustomRectangle rect) {
        // Falls noch kein Shelf existiert, lege das erste an
        if (shelves.isEmpty()) {
            Shelf shelf = new Shelf(0); // Y-Start = 0
            shelves.add(shelf);
        }

        // Versuchen, im "letzten" Regal noch Platz zu finden
        Shelf lastShelf = shelves.get(shelves.size() - 1);

        // Passt es noch in der Breite?
        if (lastShelf.currentX + rect.width <= size) {
            // Ja, also platzieren wir es
            placeOnShelf(rect, lastShelf);
            // Prüfen, ob wir damit die Box-Höhe sprengen
            if (lastShelf.startY + lastShelf.shelfHeight > size) {
                // Rückgängig machen, passt insgesamt nicht
                removeFromShelf(rect, lastShelf);
                return false;
            }
            return true;
        } else {
            // Sonst neues Regal anlegen (falls in der Höhe noch Platz ist)
            int newShelfY = lastShelf.startY + lastShelf.shelfHeight;
            if (newShelfY + rect.height > size) {
                // Kein Platz mehr für ein neues Regal
                return false;
            }
            // Neues Regal
            Shelf newShelf = new Shelf(newShelfY);
            shelves.add(newShelf);

            placeOnShelf(rect, newShelf);
            if (newShelf.startY + newShelf.shelfHeight > size) {
                removeFromShelf(rect, newShelf);
                return false;
            }
            return true;
        }
    }

    /**
     * Prüft, ob dieses Rechteck "irgendwie" (per neuem oder vorhandenem Regal)
     * in die Box passt, ohne es fest zu platzieren.
     */
    public boolean canFit(CustomRectangle rect) {
        // Prüfen, ob es ins letzte Regal passt ...
        if (shelves.isEmpty()) {
            // Dann passt es, wenn rect <= size
            return (rect.width <= size && rect.height <= size);
        } else {
            Shelf lastShelf = shelves.get(shelves.size() - 1);
            // 1) Passt es in der Breite des aktuellen Regals?
            if (lastShelf.currentX + rect.width <= size) {
                // Dann checken wir, ob das in der Höhe noch passt
                int hypotheticalHeight = Math.max(lastShelf.shelfHeight, rect.height);
                int usedHeightSoFar = lastShelf.startY + hypotheticalHeight;
                return (usedHeightSoFar <= size);
            } else {
                // 2) Sonst müssen wir ein neues Regal bilden
                int newShelfY = lastShelf.startY + lastShelf.shelfHeight;
                // Prüfen, ob ein neues Regal rect.height hoch passt
                return (newShelfY + rect.height <= size && rect.width <= size);
            }
        }
    }

    /**
     * Interne Hilfsmethode: Rechteck in dieses Shelf legen, (x,y) setzen etc.
     */
    private void placeOnShelf(CustomRectangle rect, Shelf shelf) {
        // x,y festlegen
        rect.x = shelf.currentX;
        rect.y = shelf.startY;

        // Anlisten
        this.rectangles.add(rect);
        shelf.rectanglesOnShelf.add(rect);

        // Update shelf: shift currentX
        shelf.currentX += rect.width;
        // Falls rect höher als das Shelf, shelfHeight anpassen
        shelf.shelfHeight = Math.max(shelf.shelfHeight, rect.height);
    }

    /**
     * Falls sich rausstellt, dass das Rechteck doch nicht passt (Rollback).
     */
    private void removeFromShelf(CustomRectangle rect, Shelf shelf) {
        this.rectangles.remove(rect);
        shelf.rectanglesOnShelf.remove(rect);

        // currentX wieder zurücksetzen
        shelf.currentX -= rect.width;
        // shelfHeight neu berechnen
        shelf.shelfHeight = 0;
        for (CustomRectangle r : shelf.rectanglesOnShelf) {
            shelf.shelfHeight = Math.max(shelf.shelfHeight, r.height);
        }
    }

    /**
     * Gibt den verbleibenden Platz (freie Fläche) in dieser Box zurück.
     */
    public int getRemainingArea() {
        int occupiedArea = 0;
        for (CustomRectangle rect : rectangles) {
            occupiedArea += rect.width * rect.height;
        }
        return size * size - occupiedArea;
    }

    /**
     * Überprüft, ob die Box leer ist.
     */
    public boolean isEmpty() {
        return rectangles.isEmpty();
    }

    /**
     * Minimale Overlap-Prüfung (falls gewünscht)
     */
    public boolean hasOverlap() {
        // Optional, falls du Overlap checken willst
        for (int i = 0; i < rectangles.size(); i++) {
            CustomRectangle r1 = rectangles.get(i);
            for (int j = i + 1; j < rectangles.size(); j++) {
                CustomRectangle r2 = rectangles.get(j);
                if (overlap(r1, r2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean overlap(CustomRectangle r1, CustomRectangle r2) {
        return !(r1.x + r1.width <= r2.x ||
                r2.x + r2.width <= r1.x ||
                r1.y + r1.height <= r2.y ||
                r2.y + r2.height <= r1.y);
    }

    @Override
    public String toString() {
        return "Box{" + "size=" + size + ", rectangles=" + rectangles + '}';
    }

    /**
     * Eine interne Klasse, die ein "Regal" beschreibt:
     * - Start Y-Koordinate
     * - aktuell benutzte X-Breite
     * - aktuelle Regal-Höhe
     * - Liste der darin liegenden Rectangles
     */
    private static class Shelf {
        int startY;
        int currentX;
        int shelfHeight;
        List<CustomRectangle> rectanglesOnShelf;

        public Shelf(int startY) {
            this.startY = startY;
            this.currentX = 0;
            this.shelfHeight = 0;
            this.rectanglesOnShelf = new ArrayList<>();
        }
    }
}
