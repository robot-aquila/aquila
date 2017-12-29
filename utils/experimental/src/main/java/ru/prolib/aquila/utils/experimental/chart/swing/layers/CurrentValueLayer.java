package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.Range;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.*;
import static ru.prolib.aquila.utils.experimental.chart.swing.Utils.getGraphics;

/**
 * Created by TiM on 06.10.2017.
 */
public class CurrentValueLayer<TCategory> implements BarChartLayer<TCategory> {

    public static int COLOR_TEXT = 0;
    public static int COLOR_BG = 1;
    public static int COLOR_LINE = 2;

    public static int PARAM_SHOW_LINE = 0;

    private final Series<CDecimal> data;
    protected final Map<Integer, Color> colors = new HashMap<>();
    protected final Map<Integer, Object> params = new HashMap<>();

    private boolean visible = true;
    private Stroke stroke = new BasicStroke(CURRENT_VALUE_LINE_WIDTH);
    private static final int R = 5;

    public CurrentValueLayer(Series<CDecimal> data) {
        this.data = data;
        setColor(COLOR_TEXT, CURRENT_VALUE_TEXT_COLOR);
        setColor(COLOR_BG, CURRENT_VALUE_BG_COLOR);
        setColor(COLOR_LINE, CURRENT_VALUE_LINE_COLOR);
        setParam(PARAM_SHOW_LINE, true);
    }

    @Override
    public String getId() {
        return "Current value: "+ data.getId();
    }

    @Override
    public Range<CDecimal> getValueRange(int first, int number) {
        return null;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public BarChartLayer<TCategory> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public BarChartLayer<TCategory> setColor(Color color) {
        return setColor(COLOR_TEXT, color);
    }

    @Override
    public BarChartLayer<TCategory> setColor(int colorId, Color color) {
        colors.put(colorId, color);
        return this;
    }

    @Override
    public BarChartLayer<TCategory> setParam(int paramId, Object value) {
        params.put(paramId, value);
        return this;
    }

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
}
