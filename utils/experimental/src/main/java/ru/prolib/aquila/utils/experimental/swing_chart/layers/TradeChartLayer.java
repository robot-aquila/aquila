package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.charts.Utils;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeInfo;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListSeries;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListTimeSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;

import java.awt.*;
import java.time.Instant;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_BUY_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_LINE_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TRADE_SELL_COLOR;

/**
 * Created by TiM on 19.04.2017.
 */
public class TradeChartLayer extends AbstractChartLayer<Instant, List<TradeInfo>> {

    private final int HEIGHT = 10;
    private final int WIDTH = 20;

    public TradeChartLayer(String id) {
        super(id);
    }

    @Override
    public void setData(Series<List<TradeInfo>> data) {
        super.setData(data);
        setCategories(new StampedListTimeSeries((StampedListSeries<TradeInfo>) data));
    }

    @Override
    protected void paintObject(Instant time, List<TradeInfo> tradeInfoList, CoordConverter<Instant> converter, Graphics2D g) {
        if(tradeInfoList.size()>0){
            Color color = TRADE_LINE_COLOR;
            Double x = converter.getX(time);
            if(x!=null){
                int xI = (int) Math.round(x);
                for (int j=0; j<tradeInfoList.size(); j++){
                    TradeInfo tradeInfo = tradeInfoList.get(j);
                    double y = converter.getY(tradeInfo.getPrice());
                    double y2;
                    Color fillColor;
                    if(tradeInfo.getAction().equals(OrderAction.BUY)){
                        y2 = y + HEIGHT;
                        fillColor = TRADE_BUY_COLOR;
                    } else {
                        y2 = y - HEIGHT;
                        fillColor = TRADE_SELL_COLOR;
                    }
                    int[] xPoints = {xI, xI-WIDTH/2, xI+WIDTH/2};
                    int[] yPoints = {(int) y, (int) y2, (int) y2};
                    g.setColor(color);
                    g.drawPolygon(xPoints, yPoints, 3);
                    g.setColor(fillColor);
                    g.fillPolygon(xPoints, yPoints, 3);
                }
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
    protected String createTooltipText(List<TradeInfo> value) {
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
