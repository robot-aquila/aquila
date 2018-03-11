package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class SWAbstractLayer implements BarChartLayer {
	//protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	protected final HashMap<Integer, Color> colors = new HashMap<>();
	protected final Map<Integer, Object> params = new HashMap<>();
	protected String id;
	protected boolean visible = true;
	//protected List<String> tooltips;

	public SWAbstractLayer(String layerID) {
		this.id = layerID;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public BarChartLayer setColor(Color color) {
		return setColor(0, color);
	}
	
	@Override
	public Color getColor(int colorId) {
		return colors.get(colorId);
	}
	
	@Override
	public Color getColor() {
		return getColor(DEFAULT_COLOR);
	}
	
	@Override
	public Object getParam(int paramId) {
		return params.get(paramId);
	}

	@Override
	public BarChartLayer setParam(int paramId, Object value) {
		params.put(paramId, value);
		return this;
	}

	@Override
	public BarChartLayer setColor(int colorId, Color color) {
		colors.put(colorId, color);
		return this;
	}
	
	@Override
	public void paint(BCDisplayContext context, Object device) {
		paintLayer(context, (Graphics2D) device);
	}
	
	abstract protected void paintLayer(BCDisplayContext context, Graphics2D graphics);
	

/*
    @Override
    public void paint(){
        if( ! visible ) {
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(vc).create();
        tooltips.clear();
        series.lock();
        try {
            int first = vc.getFirstVisibleCategoryIndex();
            int dataLength = series.getLength();
            for (int i = 0; i < vc.getNumberOfVisibleCategories() && first+i < dataLength; i++) {
                if(first + i < dataLength){
                    TValue value = series.get(first + i);
                    if(value!=null){
                        paintObject(i, value, vc, g);
                        tooltips.add(createTooltipText(value, vc.getValuesLabelFormatter()));
                    } else {
                        tooltips.add(null);
                    }
                }
            }
        } catch (ValueException e) {
            e.printStackTrace();
        } finally {
            series.unlock();
            g.dispose();
        }
    }

    @Override
    public Range<CDecimal> getValueRange(int first, int number) {
        CDecimal minY = CDecimalBD.ZERO;
        CDecimal maxY = CDecimalBD.ZERO;
        if ( ! visible || series == null ) {
            return null;
        }
        series.lock();
        try {
            for(int i=first; i< first + number; i++){
                TValue value = null;
                try {
                    value = series.get(i);
                } catch (ValueException e) {
                    value = null;
                }
                if(value!=null){
                    CDecimal y = getMaxValue(value);
                    minY = minY.min(y);
                    maxY = maxY.max(y);
                }
            }
        } finally {
            series.unlock();
        }
        if(minY!=null && maxY!=null){
            return Range.between(minY, maxY);
        }
        return null;
    }



    public void setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
    }

    protected String createTooltipText(TValue value, LabelFormatter labelFormatter) {
        return String.format("%s: %s", getId(), labelFormatter.format(value));
    }

    abstract protected CDecimal getMaxValue(TValue value);{
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }

    abstract protected CDecimal getMinValue(TValue value);{
        if(value instanceof Number){
            return ((Number) value).doubleValue();
        }
        return 0;
    }

    protected abstract void paintObject(int categoryIdx, TValue value,
    		BarChartVisualizationContext context, Graphics2D g);
*/
}
