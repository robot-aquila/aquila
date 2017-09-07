package ru.prolib.aquila.utils.experimental.swing_chart;

/**
 * Created by TiM on 05.09.2017.
 */
public class StaticOverlay implements Overlay {
    private String text;
    private int y = 0;

    public StaticOverlay(String text, int y) {
        this.text = text;
        this.y = y;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }
}
