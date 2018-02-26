package ru.prolib.aquila.utils.experimental.chart.swing.layer;


import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.*;

/**
 * Created by TiM on 06.10.2017.
 */
// TODO: it's ruler's function
@Deprecated
public class BarChartCurrentValueLayer extends BarChartCDecimalSeriesLayer {
	public static final int COLOR_TEXT = 0;
	public static final int COLOR_BG = 1;
	public static final int COLOR_LINE = 2;
	
	public static final int PARAM_SHOW_LINE = 0;
	
	private Stroke stroke = new BasicStroke(CURRENT_VALUE_LINE_WIDTH);
	private static final int R = 5;
	
	public BarChartCurrentValueLayer(Series<CDecimal> series) {
		super(series);
		setColor(COLOR_TEXT, CURRENT_VALUE_TEXT_COLOR);
		setColor(COLOR_BG, CURRENT_VALUE_BG_COLOR);
		setColor(COLOR_LINE, CURRENT_VALUE_LINE_COLOR);
		setParam(PARAM_SHOW_LINE, true);
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
        if(!visible){
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(context).create();
        data.lock();
        try {
            CDecimal value = null;
            int i = data.getLength() - 1;
            while (value == null && i >= 0){
                value = data.get(i--);
            }
            if(value!=null){
                paint(context, g, value);
            }
        } catch (ValueException e) {
            e.printStackTrace();
        } finally {
            g.dispose();
            data.unlock();
        }
    }

    protected void paint(BarChartVisualizationContext context, Graphics2D g, CDecimal value){
        int xStartLine = context.getPlotBounds().getUpperLeftX();
        int xEndLine = xStartLine + context.getPlotBounds().getWidth();
        int x = xEndLine + LABEL_INDENT;
        int y = context.toCanvasY(value.doubleValue());
        String text = context.getValuesLabelFormatter().format(value);
        g.setStroke(stroke);
        if(params.get(PARAM_SHOW_LINE).equals(true)){
            g.setColor(colors.get(COLOR_LINE));
            g.drawLine(xStartLine, y, x, y);
        }
        g.setFont(LABEL_FONT);
        int height = g.getFontMetrics().getHeight();
        int width = g.getFontMetrics().stringWidth(text);
        g.setColor(colors.get(COLOR_BG));
        int yMax = context.toCanvasY(context.getMinVisibleValue());
        int yMin = context.toCanvasY(context.getMaxVisibleValue());
        if(y < yMin + height/2){
            y = yMin + height/2;
        }
        if(y > yMax - height/2){
            y = yMax - height/2;
        }
        g.fillRoundRect(x, y - height/2, width+2*R, height, R, R);
        g.setColor(colors.get(COLOR_TEXT));
        g.drawString(text, x + R, y + height/4f);
    }
*/
    
}
