package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.utils.experimental.chart.AxisLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;

import java.awt.*;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_INDENT;

/**
 * Created by TiM on 12.09.2017.
 */
public class BarChartAxisV extends AbstractBarChartAxis {

    public static int POSITION_LEFT = 0;
    public static int POSITION_RIGHT = 1;

    protected final int position;

    public BarChartAxisV(int position) {
        this.position = position;
    }

    @Override
    public void paint(BarChartVisualizationContext context, AxisLabelProvider labelProvider) {
        if(!isVisible()){
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(context).create();
        try {
            g.setFont(LABEL_FONT);
            FontMetrics metrics = g.getFontMetrics(LABEL_FONT);
            for (int i=0; i<labelProvider.getLength(); i++) {
                String label = labelProvider.getLabel(i, labelFormatter);
                int width = metrics.stringWidth(labelProvider.getLabel(i, labelFormatter));
                int height = metrics.getHeight();
                int y = Math.round(labelProvider.getCanvasY(i) + height/4f);
                int x;
                if(position == POSITION_LEFT){
                    x = context.getPlotBounds().getUpperLeftX() - LABEL_INDENT - width;
                } else {
                    x = context.getPlotBounds().getUpperLeftX() + context.getPlotBounds().getWidth() + LABEL_INDENT;
                }
                g.drawString(label, (int)x, (int)y);
            }
        } finally {
            g.dispose();
        }
    }
}
