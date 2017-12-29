package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.utils.experimental.chart.AxisLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.BarChartViewport;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.swing.GraphicsProvider;

import java.awt.*;

import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_INDENT;

/**
 * Created by TiM on 12.09.2017.
 */
public class BarChartAxisH<TCategory> extends AbstractBarChartAxis {

    public static int POSITION_TOP = 0;
    public static int POSITION_BOTTOM = 1;

    private final int position;
    private final GraphicsProvider graphicsProvider;

    public BarChartAxisH(int position, GraphicsProvider graphicsProvider) {
        this.position = position;
        this.graphicsProvider = graphicsProvider;
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

            int maxWidth = 0;
            for (int i=0; i<labelProvider.getLength(); i++) {
                int width = metrics.stringWidth(labelProvider.getLabel(i, labelFormatter));
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }	

            int scaleCoeff = (int) Math.floor(maxWidth * 2 / context.getStepX()) + 1;

            for (int i = 0; i < labelProvider.getLength(); i++) {
                int x = labelProvider.getCanvasX(i);
                if ((i + 1) % scaleCoeff == 0) {
                    String label = labelProvider.getLabel(i, labelFormatter);
                    float width = metrics.stringWidth(label);
                    float height = metrics.getHeight();
                    float y = 0;
                    if (position == POSITION_TOP) {
                        y = context.getPlotBounds().getUpperLeftY() - LABEL_INDENT;
                    } else {
                        y = context.getPlotBounds().getUpperLeftY() + context.getPlotBounds().getHeight() + LABEL_INDENT + height;
                    }
                    g.drawString(label, x - width / 2, y);
                }
            }
        } finally {
            g.dispose();
        }
    }

	@Override
	public Rectangle getPaintArea(BarChartViewport viewport, ChartLayout layout) {
		if ( ! isVisible() ) {
			return null;
		}
		Rectangle root = layout.getRoot();
		int width = root.getWidth();
		int height = graphicsProvider.getGraphics()
				.getFontMetrics(LABEL_FONT).getHeight() + LABEL_INDENT;
		int y = position == POSITION_BOTTOM ? 0 : root.getHeight() - height;
		return new Rectangle(new Point2D(0, y), width, height, root);
	}

}
