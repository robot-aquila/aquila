package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.awt.Graphics2D;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

/**
 * Filled area layer.
 */
public class SWAreaLayer extends SWCDecimalSeriesLayer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWAreaLayer.class);
	}

	public SWAreaLayer(Series<CDecimal> series) {
		super(series);
	}

	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		CategoryAxisDisplayMapper cam = context.getCategoryAxisMapper();
		ValueAxisDisplayMapper vam = context.getValueAxisMapper();
		if ( cam.getAxisDirection().isVertical() ) {
			return;
		}
		int first_cat = cam.getFirstVisibleCategory();
		int last_cat = cam.getLastVisibleCategory();
		int zero_y = context.getPlotArea().getLowerY();
		LinkedList<Point2D> points = new LinkedList<>();
		CDecimal value;
		series.lock();
		try {
			for ( int i = first_cat; i <= last_cat; i ++ ) {
				try {
					value = series.get(i);
					if ( value == null ) {
						continue;
					}
				} catch ( ValueException e ) {
					logger.error("Unexpected exception: ", e);
					continue;
				}
				points.add(new Point2D(
						cam.toDisplay(i).getMidpoint(),
						vam.toDisplay(value)
					));
			}
		} finally {
			series.unlock();
		}

		if ( points.size() == 0 ) {
			return; // Nothing to paint
		}
		points.add(new Point2D(points.getLast().getX(), zero_y));
		points.add(new Point2D(points.getFirst().getX(), zero_y));
		int n = points.size();
		int x[] = new int[n];
		int y[] = new int[n];
		int i = 0;
		for ( Point2D p : points ) {
			x[i] = p.getX();
			y[i] = p.getY();
			i ++;
		}
		graphics.setColor(getColor());
		graphics.fillPolygon(x, y, n);
	}
	
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		Range<CDecimal> range = super.getValueRange(first, number);
		if ( range != null ) {
			CDecimal min = range.getMin();
			range = new Range<>(min.min(min.withZero()), range.getMax());
		}
		return range;
	}

}
