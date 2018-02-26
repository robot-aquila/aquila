package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.chart.interpolator.Point;

public class SWIndicatorLayer extends BarChartCDecimalSeriesLayer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWIndicatorLayer.class);
	}
	
	/**
	 * Enables or disables reflection of values from zero.<br>
	 * Type: boolean<br>
	 * Default: false
	 */
	public static final int NEGATE_VALUES_PARAM = 0;
	
	/**
	 * Indicator line width in pixels.<br>
	 * Type: int<br>
	 * Default: 2
	 */
	public static final int LINE_WIDTH_PARAM = 1;
	
	private final LineRenderer renderer;
	
	public SWIndicatorLayer(Series<CDecimal> series, LineRenderer renderer) {
		super(series);
		this.renderer = renderer;
		setParam(NEGATE_VALUES_PARAM, false);
		setParam(LINE_WIDTH_PARAM, 2);
		setColor(DEFAULT_COLOR, Color.BLUE);
	}
	
	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		if ( ! isVisible() ) {
			return;
		}
		CategoryAxisDisplayMapper cMapper = context.getCategoryAxisMapper();
		ValueAxisDisplayMapper vMapper = context.getValueAxisMapper();
		AxisDirection dir = cMapper.getAxisDirection();
		if ( dir.isVertical() ) {
			logger.warn("Axis direction is not unsupported: " + dir);
		}
		boolean negate = isNegateValues();
		CDecimal value;
		List<Point> points = new ArrayList<>();
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
					points.add(null); // TODO: test me
				} else {
					if ( negate ) {
						value = value.negate(); // TODO: test me
					}
					Segment1D barSegment = cMapper.toDisplay(i);
					int y = vMapper.toDisplay(value);
					points.add(new Point(barSegment.getMidpoint(), y));
				}
			}
		} finally {
			series.unlock();
		}
		graphics.setColor(getColor());
		graphics.setStroke(new BasicStroke((int)getParam(LINE_WIDTH_PARAM)));
		graphics.draw(renderer.renderLine(points));
	}
	
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		Range<CDecimal> range = super.getValueRange(first, number);
		if ( range != null && isNegateValues() ) {
			range = new Range<>(range.getMax().negate(), range.getMin().negate());
		}
		return range;
	}
	
	public boolean isNegateValues() {
		return (Boolean) getParam(NEGATE_VALUES_PARAM);
	}

    /*
    @Override
    protected void paintObject(int categoryIdx, CDecimal number, BarChartVisualizationContext context, Graphics2D g) {

    }

    @Override
    public void paint(BarChartVisualizationContext context) {
        if(data==null || !visible){
            return;
        }

        Graphics2D g = (Graphics2D) getGraphics(context).create();
        tooltips.clear();
        data.lock();
        try {
            g.setColor(colors.get(0));
            g.setStroke(new BasicStroke(INDICATOR_LINE_WIDTH));
            List<Point> points = new ArrayList<>();
            int first = context.getFirstVisibleCategoryIndex();
            int dataLength = data.getLength();

            for(int i=0; i<context.getNumberOfVisibleCategories() && first+i < dataLength; i++){
                CDecimal v = null;
                try {
                    v = data.get(i + first);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if( v==null ) {
                    points.add(null);
                    tooltips.add(null);
                } else {
                    v = v.multiply((long) getSign());
                    points.add(new Point(context.toCanvasX(i), context.toCanvasY(v)));
                    tooltips.add(createTooltipText(v, context.getValuesLabelFormatter()));
                }
            }
            g.draw(renderer.renderLine(points));
        } finally {
            g.dispose();
            data.unlock();
        }
    }

    @Override
    protected CDecimal getMaxValue(CDecimal value) {
        if(zeroOnCenter()){
        	return value.abs();
            //return Math.abs(value.doubleValue());
        }
        return value.multiply((long) getSign());
        //return getSign()*value.doubleValue();
    }

    @Override
    protected CDecimal getMinValue(CDecimal value) {
        if(zeroOnCenter()){
        	return value.abs().negate();
            //return -Math.abs(value.doubleValue());
        }
        return value.multiply((long) getSign());
        //return getSign()*value.doubleValue();
    }

    private int getSign(){
        return params.get(INVERT_VALUES_PARAM).equals(true)?-1:1;
    }

    private boolean zeroOnCenter(){
        return (boolean) params.get(ZERO_LINE_ON_CENTER_PARAM);
    }
    */
}
