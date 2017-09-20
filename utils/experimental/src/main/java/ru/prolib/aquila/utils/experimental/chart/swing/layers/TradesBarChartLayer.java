package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.swing_chart.Utils;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.TradeInfo;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.TradeInfoList;

import java.awt.*;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_BUY_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_LINE_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_SELL_COLOR;

/**
 * Created by TiM on 16.09.2017.
 */
public class TradesBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, TradeInfoList> {

    public static int LINE_COLOR = 0;
    public static int BUY_COLOR = 1;
    public static int SELL_COLOR = -1;

    private final int HEIGHT = 10;
    private final int WIDTH = 20;

    public TradesBarChartLayer(Series<TradeInfoList> data) {
        super(data);
        colors.put(LINE_COLOR, TRADE_LINE_COLOR);
        colors.put(BUY_COLOR, TRADE_BUY_COLOR);
        colors.put(SELL_COLOR, TRADE_SELL_COLOR);
    }

    @Override
    protected void paintObject(int categoryIdx, TradeInfoList tradeInfoList, BarChartVisualizationContext context, Graphics2D g) {
        if(tradeInfoList!=null) {
            tradeInfoList = new TradeInfoList(tradeInfoList);
            Color color = colors.get(LINE_COLOR);
            int x = context.toCanvasX(categoryIdx);
            for (int j=0; j<tradeInfoList.size(); j++){
                TradeInfo tradeInfo = tradeInfoList.get(j);
                int y = context.toCanvasY(tradeInfo.getPrice());
                int y2;
                Color fillColor;
                if(tradeInfo.getAction().equals(OrderAction.BUY)){
                    y2 = y + HEIGHT;
                    fillColor = colors.get(BUY_COLOR);
                } else {
                    y2 = y - HEIGHT;
                    fillColor = colors.get(SELL_COLOR);
                }
                int[] xPoints = {x, x-WIDTH/2, x+WIDTH/2};
                int[] yPoints = {y, y2, y2};
                g.setColor(color);
                g.drawPolygon(xPoints, yPoints, 3);
                g.setColor(fillColor);
                g.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }

    @Override
    protected double getMaxValue(TradeInfoList value) {
        return value.getMaxValue();
    }

    @Override
    protected double getMinValue(TradeInfoList value) {
        return value.getMinValue();
    }

    @Override
    protected String createTooltipText(TradeInfoList value, LabelFormatter labelFormatter) {
        StringBuilder sb = new StringBuilder("Orders:\n");
        value = new TradeInfoList(value);
        for(int i=0; i< value.size(); i++){
            TradeInfo ti = value.get(i);
            sb.append(ti.getOrderId());
            sb.append(";\n");
        }
        return sb.toString();
    }

    private String createTooltipText(TradeInfo tradeInfo) {
        return String.format("%s%n" +
                        "%s %s @ %.2f x %d%n" +
                        "ACCOUNT: %s%n" +
                        "ORDER #: %d",
                Utils.instantToStr(tradeInfo.getTime()),
                tradeInfo.getAction(), tradeInfo.getSymbol(), tradeInfo.getPrice(),tradeInfo.getVolume(),
                tradeInfo.getAccount(),
                tradeInfo.getOrderId());
    }
}
