package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorage;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.time.Instant;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CANDLE_MIN_WIDTH;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CANDLE_WIDTH_RATIO;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.BAR_COLOR;

/**
 * Created by TiM on 27.01.2017.
 */
public class BarChartLayer extends AbstractChartLayer<Instant, Number> {

    private Color color = BAR_COLOR;
    private boolean invertValues = false;
    private int sign = 1;
    private boolean fixCenter = false;


    public BarChartLayer(String id) {
        super(id);
    }

    public BarChartLayer(String id, ChartLayerDataStorage<Instant, Number> storage) {
        super(id, storage);
    }

    @Override
    protected void paintObject(Instant category, Number value, CoordConverter<Instant> converter, Graphics2D g) {
        int cnt = converter.getCategories().size();
        double x = converter.getX(category);
        double y = converter.getY(sign * value.doubleValue());
        double height = Math.abs(y - converter.getY(0d));
        if(height==0){
            height = 1;
        }
        double width = converter.getStepX()*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        g.setColor(color);
        g.fill(new Rectangle2D.Double(x - width/2, invertValues?y-height:y, width, height));
    }

    @Override
    protected double getMaxValue(Number value) {
        if(fixCenter){
            return value.doubleValue();
        }
        return invertValues?0d:value.doubleValue();

    }

    @Override
    protected double getMinValue(Number value) {
        if(fixCenter){
            return -value.doubleValue();
        }
        return invertValues?-value.doubleValue():0d;
    }

    public BarChartLayer withColor(Color color) {
        this.color = color;
        return this;
    }

    public BarChartLayer withInvertValues(boolean invertValues) {
        this.invertValues = invertValues;
        sign = invertValues?-1:1;
        return this;
    }

    public BarChartLayer withFixCenter(boolean fixCenter) {
        this.fixCenter = fixCenter;
        return this;
    }
}
