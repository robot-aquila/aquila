package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

public class SWHistogramLayer extends BarChartCDecimalSeriesLayer {
	public static final int NEGATE_VALUES_PARAM = 0;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWHistogramLayer.class);
	}
	
	public SWHistogramLayer(Series<CDecimal> data) {
		super(data);
		setParam(NEGATE_VALUES_PARAM, false);
		setColor(Color.GRAY);
	}
	
	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		CategoryAxisDisplayMapper cMapper = context.getCategoryAxisMapper();
		ValueAxisDisplayMapper vMapper = context.getValueAxisMapper();
		AxisDirection dir = cMapper.getAxisDirection();
		if ( dir.isVertical() ) {
			logger.warn("Axis direction now unsupported: " + dir);
			return;
		}
		boolean negate = isNegateValues();
		CDecimal value;
		graphics.setColor(getColor());
		series.lock();
		try {
			int last = cMapper.getLastVisibleCategory();
			for ( int i = cMapper.getFirstVisibleCategory(); i <= last; i ++ ) {
				try {
					value = series.get(i);
				} catch ( ValueException e ) {
					logger.error("Error accessing value: ", e);
					continue;
				}
				if ( value == null ) {
					continue;
				}
				if ( negate ) {
					value = value.negate();
				}
				Segment1D barSegment = cMapper.toDisplay(i);
				int upY = vMapper.toDisplay(value.max(CDecimalBD.ZERO)),
					dnY = vMapper.toDisplay(value.min(CDecimalBD.ZERO));
				if ( barSegment.getLength() > 1 ) {
					int leftX = barSegment.getStart(),
						width = barSegment.getLength() - 1,
						height = dnY - upY + 1;
					graphics.fillRect(leftX, upY, width, height);
				} else {
					int x = barSegment.getStart();
					graphics.drawLine(x, upY, x, dnY); // TODO: test this case
				}
			}
		} finally {
			series.unlock();
		}
	}
	
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		Range<CDecimal> range = super.getValueRange(first, number);
		if ( range != null ) {
			int zcmpMin = range.getMin().compareTo(CDecimalBD.ZERO),
				zcmpMax = range.getMax().compareTo(CDecimalBD.ZERO);
			if ( zcmpMin < 0 && zcmpMax < 0 ) {
				// Both are lower than zero -> make zero the max value
				range = new Range<>(range.getMin(), CDecimalBD.ZERO);
			} else if ( zcmpMin > 0 && zcmpMax > 0 ) {
				// Both are greater than zero -> make zero the min value
				range = new Range<>(CDecimalBD.ZERO, range.getMax());
			}
			if ( isNegateValues() ) {
				range = new Range<>(range.getMax().negate(), range.getMin().negate());
			}
		}
		return range;
	}
	
	public boolean isNegateValues() {
		return (Boolean) getParam(NEGATE_VALUES_PARAM);
	}

}
