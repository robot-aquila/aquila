package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.swing_chart.Utils;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.TradeInfo;

import java.awt.*;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_BUY_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_LINE_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_SELL_COLOR;

/**
 * Created by TiM on 16.09.2017.
 */
public class TradesBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, List<TradeInfo>> {

    public static int LINE_COLOR = 0;
    public static int BUY_COLOR = 1;
    public static int SELL_COLOR = -1;

    private final int HEIGHT = 10;
    private final int WIDTH = 20;

    public TradesBarChartLayer(Series<List<TradeInfo>> data) {
        super(data);
        colors.put(LINE_COLOR, TRADE_LINE_COLOR);
        colors.put(BUY_COLOR, TRADE_BUY_COLOR);
        colors.put(SELL_COLOR, TRADE_SELL_COLOR);
    }

    @Override
    protected void paintObject(int categoryIdx, List<TradeInfo> tradeInfoList, BarChartVisualizationContext context, Graphics2D g) {
        if(tradeInfoList!=null && tradeInfoList.size()>0){
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
    protected double getMaxValue(List<TradeInfo> value) {
        return value.stream().mapToDouble(ti-> ti.getPrice()).max().orElse(0d);
    }

    @Override
    protected double getMinValue(List<TradeInfo> value) {
        return value.stream().mapToDouble(ti-> ti.getPrice()).min().orElse(1e6);
    }

    @Override
    protected String createTooltipText(List<TradeInfo> value, LabelFormatter labelFormatter) {
        StringBuilder sb = new StringBuilder("Orders:\n");
        for(TradeInfo ti: value){
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
