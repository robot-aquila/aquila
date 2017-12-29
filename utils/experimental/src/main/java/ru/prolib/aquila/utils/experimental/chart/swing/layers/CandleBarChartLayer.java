package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BarChartOrientation;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.time.ZoneId;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.*;

/**
 * Created by TiM on 13.09.2017.
 */
public class CandleBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, Candle> {

    private Stroke stroke = new BasicStroke(CANDLE_LINE_WIDTH);
    private final ZoneId zoneId;

    public CandleBarChartLayer(Series<Candle> data) {
        super(data);
        zoneId = ZoneId.systemDefault();
    }

    public CandleBarChartLayer(Series<Candle> data, ZoneId zoneId) {
        super(data);
        this.zoneId = zoneId;
    }

    @Override
    protected void paintObject(int categoryIdx, Candle value, BarChartVisualizationContext context, Graphics2D g) {
        Color color = value.isBearish() ? COLOR_BEAR : COLOR_BULL;
        int barWidth = context.getBarWidthPx();
        BarChartOrientation orientation = context.getOrientation();
        if ( orientation != BarChartOrientation.LEFT_TO_RIGHT ) {
        	throw new IllegalArgumentException("Unsupported orientation: " + orientation);
        }
        
        int x = context.toDisplayBarStart(categoryIdx);
        int yOpen = context.toDisplay(value.getOpen());
        int yClose = context.toDisplay(value.getClose());
        int y = yOpen < yClose ? yOpen : yClose;
        int height = Math.abs(yOpen - yClose);
        int yHigh = context.toDisplay(value.getHigh());
        int yLow = context.toDisplay(value.getLow());

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
    protected CDecimal getMaxValue(Candle candle) {
        return candle.getHigh();
    }

    @Override
    protected CDecimal getMinValue(Candle candle) {
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
                dateTimeFormatter.format(value.getStartTime().atZone(zoneId).toLocalDateTime()),
                value.getOpen(),
                value.getHigh(),
                value.getLow(),
                value.getClose());
    }

}
