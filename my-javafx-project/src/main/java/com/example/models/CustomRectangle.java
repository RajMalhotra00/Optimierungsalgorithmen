package com.example.models;

public class CustomRectangle {
    public int width, height;
    public int x, y;

    public CustomRectangle(int width, int height) {
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;
    }

    // Copy-Konstruktor (optional)
    public CustomRectangle(CustomRectangle original) {
        this.width = original.width;
        this.height = original.height;
        this.x = original.x;
        this.y = original.y;
    }

    public CustomRectangle rotate() {
        return new CustomRectangle(this.height, this.width);
    }

    @Override
    public String toString() {
        return "Rectangle{" + "width=" + width + ", height=" + height +
                ", x=" + x + ", y=" + y + '}';
    }
}
