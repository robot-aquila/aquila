package ru.prolib.aquila.utils.experimental.swing_chart;

/**
 * Created by TiM on 05.09.2017.
 */
public class Overlay {
    private String text;
    private int y = 0;

    public Overlay(String text, int y) {
        this.text = text;
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
