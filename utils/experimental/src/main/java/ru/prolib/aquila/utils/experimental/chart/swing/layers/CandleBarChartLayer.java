package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.Utils;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.*;

/**
 * Created by TiM on 13.09.2017.
 */
public class CandleBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, Candle> {

    private Stroke stroke = new BasicStroke(CANDLE_LINE_WIDTH);

    public CandleBarChartLayer(Series<Candle> data) {
        super(data);
    }

    @Override
    protected void paintObject(int categoryIdx, Candle value, BarChartVisualizationContext context, Graphics2D g) {
        Color color = value.getOpen()>value.getClose()?COLOR_BEAR:COLOR_BULL;
        Double step = context.getStepX();
        double x = context.toCanvasX(categoryIdx);
        double yOpen = context.toCanvasY(value.getOpen());
        double yClose = context.toCanvasY(value.getClose());
        double y = yOpen < yClose ? yOpen : yClose;
        double height = Math.abs(yOpen - yClose);
        double yHigh = context.toCanvasY(value.getHigh());
        double yLow = context.toCanvasY(value.getLow());

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
    protected double getMaxValue(Candle candle) {
        return candle.getHigh();
    }

    @Override
    protected double getMinValue(Candle candle) {
        return candle.getLow();
    }

    @Override
    protected String createTooltipText(Candle value, LabelFormatter formatter) {
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
