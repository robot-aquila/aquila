package ru.prolib.aquila.utils.experimental.chart;

/**
 * Created by TiM on 06.09.2017.
 */
@Deprecated
public interface ChartOverlay {
    String getText();
    int getY();
    void setVisible(boolean visible);
    boolean isVisible();
}
