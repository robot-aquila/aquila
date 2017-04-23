package ru.prolib.aquila.utils.experimental.charts.objects;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Polygon;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.Chart;
import ru.prolib.aquila.utils.experimental.charts.Utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by TiM on 19.04.2017.
 */
public class TradeChartObject implements ChartObject {

    private final String ID_PREFIX = "TRADES";
    private final double HEIGHT = 10d;
    private final double WIDTH = 20d;

    private Chart chart;
    private Map<LocalDateTime, List<TradeInfo>> data = new TreeMap<>();
    private List<LocalDateTime> xValues = new ArrayList<>();
    private long period = 0; //period in seconds

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public List<LocalDateTime> getXValues() {
        return xValues;
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        int cnt = chart.getXValues().size();
        for (int i = 0; i < cnt; i++) {
            LocalDateTime time = chart.getXValues().get(i);
            List<TradeInfo> tradeInfoList = data.get(time);
            if(tradeInfoList != null && tradeInfoList.size()>0){
                Node node = chart.getNodeById(buildId(time));
                Group group = (Group)node;
                if(group==null){
                    group = new Group();
                    group.setId(buildId(time));
                }
                double x = chart.getX(time);
                for (int j=0; j<tradeInfoList.size(); j++){
                    TradeInfo tradeInfo = tradeInfoList.get(j);
                    double y = chart.getY(tradeInfo.getPrice());
                    double y2;
                    String styleClass;
                    if(tradeInfo.getAction().equals(OrderAction.BUY)){
                        y2 = y + HEIGHT;
                        styleClass = "trade-buy";
                    } else {
                        y2 = y - HEIGHT;
                        styleClass = "trade-sell";
                    }
                    Polygon arrow = null;
                    if(j<group.getChildren().size()){
                        arrow = (Polygon) group.getChildren().get(j);
                        arrow.getPoints().setAll(x, y, x-WIDTH/2, y2, x+WIDTH/2, y2);
                    } else {
                        arrow = new Polygon(x, y, x-WIDTH/2, y2, x+WIDTH/2, y2);
                        arrow.getStyleClass().add(styleClass);
                        group.getChildren().add(arrow);
                    }
                    Tooltip.install(arrow, getTooltip(tradeInfo));
                }
                if(node==null){
                    result.add(group);
                }
            }
        }
        return result;

    }

    private Tooltip getTooltip(TradeInfo tradeInfo){
        return new Tooltip(String.format("%s %s @ %.2f x %d", tradeInfo.getAction(), tradeInfo.getSymbol(), tradeInfo.getPrice(), tradeInfo.getVolume()));
    }

    @Override
    public Pair<Double, Double> getYInterval(List<LocalDateTime> xValues) {
        double minY = 1e6;
        double maxY = 0;
        for (LocalDateTime time: xValues) {
            List<TradeInfo> tradeInfoList = data.get(time);
            if(tradeInfoList != null){
                for(TradeInfo t: tradeInfoList){
                    double y = t.getPrice();
                    if(y > maxY){
                        maxY = y;
                    }
                    if(y < minY){
                        minY = y;
                    }
                }
            }
        }
        return new ImmutablePair(minY, maxY);
    }

    public void setXValues(List<LocalDateTime> xValues) {
        this.xValues.clear();
        this.xValues.addAll(xValues);
        if(period ==0 && xValues.size()>1){
            setPeriod(xValues.get(0), xValues.get(1));
        }
    }

    public void addXValue(LocalDateTime xValue) {
        this.xValues.add(xValue);
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void setPeriod(Temporal from, Temporal to) {
        this.period = ChronoUnit.SECONDS.between(from, to);
    }

    public void setPeriod(Candle candle) {
        setPeriod(candle.getStartTime(), candle.getEndTime());
    }

    public void setData(List<TradeInfo> tradeInfoList){
        this.data.clear();
        addData(tradeInfoList);
    }

    public void addData(List<TradeInfo> tradeInfoList){
        for(TradeInfo t: tradeInfoList){
            addData(t);
        }
    }

    public void addData(TradeInfo tradeInfo){
        LocalDateTime tradeTime = Utils.toLocalDateTime(tradeInfo.getTime());
        for (LocalDateTime xStart : xValues) {
            LocalDateTime xEnd = xStart.plusSeconds(period);
            List<TradeInfo> list = data.computeIfAbsent(xStart, k -> new ArrayList<>());
            if ((tradeTime.isAfter(xStart) && tradeTime.isBefore(xEnd)) || tradeTime.isEqual(xStart)) {
                list.add(tradeInfo);
            }
        }
    }

    private String buildId(LocalDateTime time){
        return ID_PREFIX+"@"+time.toString();
    }
}
