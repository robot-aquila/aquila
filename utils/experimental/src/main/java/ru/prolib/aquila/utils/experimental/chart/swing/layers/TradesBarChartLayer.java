package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.Range;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartVisualizationContext;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

import java.awt.*;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TRADE_BUY_COLOR;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TRADE_LINE_COLOR;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TRADE_SELL_COLOR;

/**
 * Created by TiM on 16.09.2017.
 */
public class TradesBarChartLayer<TCategory> extends AbstractBarChartLayer<TCategory, TradeInfoList> {

    public static final int ACCOUNTS_PARAM = 0;

    public static final int LINE_COLOR = 0;
    public static final int BUY_COLOR = 1;
    public static final int SELL_COLOR = -1;

    private final int HEIGHT = 10;
    private final int WIDTH = 20;

    private List<Account> accounts;

    public TradesBarChartLayer(Series<TradeInfoList> data) {
        super(data);
        colors.put(LINE_COLOR, TRADE_LINE_COLOR);
        colors.put(BUY_COLOR, TRADE_BUY_COLOR);
        colors.put(SELL_COLOR, TRADE_SELL_COLOR);
    }

    @Override
    protected void paintObject(int categoryIdx, TradeInfoList tradeInfoList, BarChartVisualizationContext context, Graphics2D g) {
        if (tradeInfoList != null) {
            tradeInfoList = new TradeInfoList(tradeInfoList, accounts);
            Color color = colors.get(LINE_COLOR);
            int x = context.toCanvasX(categoryIdx);
            for (int j = 0; j < tradeInfoList.size(); j++) {
                TradeInfo tradeInfo = tradeInfoList.get(j);
                int y = context.toCanvasY(tradeInfo.getPrice());
                int y2;
                Color fillColor;
                if (tradeInfo.getAction().equals(OrderAction.BUY)) {
                    y2 = y + HEIGHT;
                    fillColor = colors.get(BUY_COLOR);
                } else {
                    y2 = y - HEIGHT;
                    fillColor = colors.get(SELL_COLOR);
                }
                int[] xPoints = {x, x - WIDTH / 2, x + WIDTH / 2};
                int[] yPoints = {y, y2, y2};
                g.setColor(color);
                g.drawPolygon(xPoints, yPoints, 3);
                g.setColor(fillColor);
                g.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }

    @Override
    public Range<Double> getValueRange(int first, int number) {
        Double minY = null;
        Double maxY = null;
        if (!visible || data == null) {
            return null;
        }
        data.lock();
        try {
            for(int i=first; i< first + number; i++){
                TradeInfoList value = null;
                try {
                    value = data.get(i);
                } catch (ValueException e) {
                    value = null;
                }
                if(value!=null) {
                    value = new TradeInfoList(value, accounts);
                }
                if(value!=null && value.size()>0){
                    double y = getMaxValue(value);
                    if(maxY==null || y>maxY){
                        maxY = y;
                    }
                    y = getMinValue(value);
                    if(minY==null || y<minY){
                        minY = y;
                    }
                }
            }
        } finally {
            data.unlock();
        }
        if(minY!=null && maxY!=null){
            return Range.between(minY, maxY);
        }
        return null;
    }

    @Override
    protected double getMaxValue(TradeInfoList value) {
    	// TODO: fixme
    	CDecimal x = value.getMaxValue();
        return x == null ? 0d : x.toBigDecimal().doubleValue();
    }

    @Override
    protected double getMinValue(TradeInfoList value) {
    	// TODO: fixme
    	CDecimal x = value.getMinValue();
        return x == null ? 0d : x.toBigDecimal().doubleValue();
    }

    @Override
    protected String createTooltipText(TradeInfoList value, LabelFormatter labelFormatter) {
        value = new TradeInfoList(value, accounts);
        if(value.size()>0){
            StringBuilder sb = new StringBuilder("Orders:\n");
            for(int i=0; i< value.size(); i++){
                TradeInfo ti = value.get(i);
                sb.append(ti.getOrderId());
                sb.append(";\n");
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public BarChartLayer<TCategory> setParam(int paramId, Object value) {
        super.setParam(paramId, value);
        if(paramId == ACCOUNTS_PARAM && value instanceof List){
            accounts = (List<Account>) value;
        }
        return this;
    }
}
