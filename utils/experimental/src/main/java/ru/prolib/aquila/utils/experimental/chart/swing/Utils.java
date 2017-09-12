package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;

import java.awt.*;

/**
 * Created by TiM on 12.09.2017.
 */
public class Utils {
    public static Graphics2D getGraphics(BarChartVisualizationContext vc) {
        return (Graphics2D) ((BarChartVisualizationContextImpl) vc).getGraphics();
    }
}
