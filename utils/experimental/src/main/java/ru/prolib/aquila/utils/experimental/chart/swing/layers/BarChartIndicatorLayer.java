package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import java.awt.Graphics2D;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.interpolator.LineRenderer;

public class BarChartIndicatorLayer extends BarChartCDecimalSeriesLayer {
	public static final int INVERT_VALUES_PARAM = 0;
	public static final int ZERO_LINE_ON_CENTER_PARAM = 1;
	
	private final LineRenderer renderer;
	
	public BarChartIndicatorLayer(Series<CDecimal> data, LineRenderer renderer) {
		super(data);
		this.renderer = renderer;
		setParam(INVERT_VALUES_PARAM, false);
		setParam(ZERO_LINE_ON_CENTER_PARAM, false);
	}
	
	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		
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
