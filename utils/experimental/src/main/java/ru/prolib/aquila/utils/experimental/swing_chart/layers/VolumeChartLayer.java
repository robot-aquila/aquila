package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.time.Instant;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CANDLE_MIN_WIDTH;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CANDLE_WIDTH_RATIO;

/**
 * Created by TiM on 27.01.2017.
 */
public class VolumeChartLayer extends AbstractChartLayer<Instant, Long> {

    private Color color = Color.gray;

    public VolumeChartLayer(String id) {
        super(id);
    }

    @Override
    protected void paintObject(Instant category, Long value, CoordConverter<Instant> converter) {
        int cnt = converter.getCategories().size();
        double x = converter.getX(category);
        double y = converter.getY(Double.valueOf(value));
        double height = Math.abs(y - converter.getY(0d));
        if(height==0){
            height = 1;
        }
        double width = converter.getStepX()*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        converter.getGraphics().setColor(color);
        converter.getGraphics().fill(new Rectangle2D.Double(x - width/2, y, width, height));
    }

    @Override
    protected double getMaxValue(Long value) {
        return value.doubleValue();
    }

    @Override
    protected double getMinValue(Long value) {
        return 0d;
    }

    @Override
    protected String createTooltipText(Long volume) {
        return String.format("VOLUME: %d", volume);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
