package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleStartTimeSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.swing_chart.Utils;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorage;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorageTSeries;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.time.Instant;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.*;

/**
 * Created by TiM on 13.06.2017.
 */
public class CandleChartLayer extends AbstractChartLayer<Instant, Candle> {
    private Stroke stroke = new BasicStroke(CANDLE_LINE_WIDTH);

    public CandleChartLayer(String id) {
        super(id);
    }

    public CandleChartLayer(String id, ChartLayerDataStorage<Instant, Candle> storage) {
        super(id, storage);
    }

    @Override
    public void setData(Series<Candle> data) {
        super.setData(data);
            //TODO переделать
        if(!(storage instanceof ChartLayerDataStorageTSeries)){
            setCategories(new CandleStartTimeSeries(data));
        }
    }

    @Override
    protected void paintObject(Instant category, Candle value, CoordConverter<Instant> converter, Graphics2D g) {
        Color color = value.getOpen()>value.getClose()?COLOR_BEAR:COLOR_BULL;
        Double step = converter.getStepX();
        double x = converter.getX(category);
        double yOpen = converter.getY(value.getOpen());
        double yClose = converter.getY(value.getClose());
        double y = yOpen < yClose ? yOpen : yClose;
        double height = Math.abs(yOpen - yClose);
        double yHigh = converter.getY(value.getHigh());
        double yLow = converter.getY(value.getLow());

        g.setColor(COLOR_BULL);
        g.setStroke(stroke);
        Shape line = new Line2D.Double(x, yLow, x, yHigh);
        g.draw(line);

        double width = step*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        Shape body = new Rectangle2D.Double(x-width/2, y, width, height);
        g.setColor(color);
        g.fill(body);
        g.setColor(COLOR_BULL);
        g.draw(body);
    }

    @Override
    protected double getMaxValue(Candle value) {
        return value.getHigh();
    }

    @Override
    protected double getMinValue(Candle value) {
        return value.getLow();
    }

    @Override
    protected String createTooltipText(Candle value) {
        return String.format(
                "%s%n"+
                        "OPEN: %8.2f%n" +
                        "HIGH: %8.2f%n" +
                        "LOW:  %8.2f%n" +
                        "CLOSE:%8.2f",
                Utils.instantToStr(value.getStartTime()),
                value.getOpen(),
                value.getHigh(),
                value.getLow(),
                value.getClose());
    }
}
