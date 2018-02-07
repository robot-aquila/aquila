package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

import java.awt.Color;
import java.awt.Graphics2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by TiM on 13.09.2017.
 */
public class BarChartCandlestickLayer extends BarChartAbstractLayer {
	public static final int BULLISH_BODY_COLOR = 1;
	public static final int BULLISH_SHADOW_COLOR = 2;
	public static final int BEARISH_BODY_COLOR = 3;
	public static final int BEARISH_SHADOW_COLOR = 4;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(BarChartCandlestickLayer.class);
	}
	
	private final Series<Candle> series;

	public BarChartCandlestickLayer(Series<Candle> series) {
		super(series.getId());
		this.series = series;
		setColor(BULLISH_BODY_COLOR,	new Color(  0, 128, 0));
		setColor(BULLISH_SHADOW_COLOR,	new Color(  0,  80, 0));
		setColor(BEARISH_BODY_COLOR,	new Color(230,   0, 0));
		setColor(BEARISH_SHADOW_COLOR,	new Color( 80,   0, 0));
    }
	
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		if ( ! isVisible() || series == null ) {
			return null;
		}
		series.lock();
		try {
			CDecimal min = null, max = null;
			for ( int i = 0; i < number; i ++ ) {
				try {
					Candle x = series.get(first + i);
					if ( x != null ) {
						min = x.getLow().min(min);
						max = x.getHigh().max(max);
					}
				} catch ( ValueException e ) { }
			}
			if ( min != null ) {
				return new Range<CDecimal>(min, max);
			}
		} finally {
			series.unlock();
		}
		return null;
	}
	
	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		CategoryAxisDisplayMapper cMapper = context.getCategoryAxisMapper();
		ValueAxisDisplayMapper vMapper = context.getValueAxisMapper();
		Candle candle = null;
		if ( cMapper.getAxisDirection().isVertical() ) {
			logger.warn("Axis direction now unsupported: " + cMapper.getAxisDirection());
			return;
		}
		series.lock();
		try {
			int n = cMapper.getNumberOfVisibleCategories();
			for ( int i = cMapper.getFirstVisibleCategory(); i < n; i ++ ) {
				try {
					candle = series.get(i);
				} catch ( ValueException e ) {
					logger.error("Error accessing candle: ", e);
					continue;
				}
				Color bodyColor = null, shadowColor = null;
				if ( candle.isBullish() ) {
					bodyColor = getColor(BULLISH_BODY_COLOR);
					shadowColor = getColor(BULLISH_SHADOW_COLOR);
				} else {
					bodyColor = getColor(BEARISH_BODY_COLOR);
					shadowColor = getColor(BEARISH_SHADOW_COLOR);
				}
				Segment1D barSeg = cMapper.toDisplay(i);
				int bodyUpY = vMapper.toDisplay(candle.getOpen().max(candle.getClose()));
				int bodyDnY = vMapper.toDisplay(candle.getOpen().min(candle.getClose()));
				int cShadowUpY = vMapper.toDisplay(candle.getHigh());
				int cShadowDnY = vMapper.toDisplay(candle.getLow());
				if ( barSeg.getLength() > 1 ) {
					int cLeftX = barSeg.getStart();
					int cWidth = barSeg.getLength() - 1;
					int cMidX = cLeftX + cWidth / 2;
					graphics.setColor(shadowColor);
					graphics.drawLine(cMidX, cShadowUpY, cMidX, cShadowDnY);
					graphics.setColor(bodyColor);
					graphics.fillRect(cLeftX, bodyUpY, cWidth, bodyDnY - bodyUpY + 1);
				} else {
					int x = barSeg.getStart();
					graphics.setColor(shadowColor);
					graphics.drawLine(x, cShadowUpY, x, cShadowDnY);
					graphics.setColor(bodyColor);
					graphics.drawLine(x, bodyUpY, x, bodyDnY);
				}
			}
		} finally {
			series.unlock();
		}
	}

}
