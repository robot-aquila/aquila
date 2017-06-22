package ru.prolib.aquila.utils.experimental.swing_chart.interpolator;

import java.awt.*;
import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public interface LineRenderer {
    Shape renderLine(List<Point> points);
}
