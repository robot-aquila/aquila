package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.SELECTION_COLOR;

/**
 * Created by TiM on 13.09.2017.
 */
@Deprecated
public class BarChartCursorLayer extends SWAbstractLayer {
	private Color color = SELECTION_COLOR;

	public BarChartCursorLayer(AtomicInteger coord) {
		super("___CURSOR");
	}
	
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		return null;
	}
	
	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		
	}

    /*
    @Override
    public void paint(BarChartVisualizationContext context) {
        Graphics2D g = (Graphics2D) getGraphics(context).create();
        try {
            int lastCategoryIdx = context.toCategoryIdx(coord.get(), coord.get());
            if(lastCategoryIdx>=0 && lastCategoryIdx < context.getNumberOfVisibleCategories()){
                double x = context.toCanvasX(lastCategoryIdx);
                double width = context.getStepX();
                g.setColor(SELECTION_COLOR);
                g.fill(new Rectangle2D.Double(x-width/2, context.getPlotBounds().getUpperLeftY(), width, context.getPlotBounds().getHeight()));
            }
        } finally {
            g.dispose();
        }

    }
     */
}
