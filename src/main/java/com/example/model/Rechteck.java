package com.example.model;

public class Rechteck {
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean rotated; // false: normal, true: um 90° rotiert

    public Rechteck(int width, int height) {
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;
        this.rotated = false;
    }

    public int getWidth() {
        return rotated ? height : width;
    }

    public int getHeight() {
        return rotated ? width : height;
    }

    public void rotate() {
        rotated = !rotated;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Rechteck))
            return false;
        Rechteck r = (Rechteck) o;
        return width == r.width && height == r.height &&
                x == r.x && y == r.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(width, height, x, y);
    }

}
