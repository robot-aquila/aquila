package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.charts.Utils;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListSeries;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListTimeSeries;

import java.time.Instant;
import java.util.List;

/**
 * Created by TiM on 19.04.2017.
 */
public class TradeChartLayer extends AbstractChartLayer<Instant, List<TradeInfo>> {

    private final String ID_PREFIX = "TRADES";
    private final double HEIGHT = 10d;
    private final double WIDTH = 20d;

    @Override
    public void setData(Series<List<TradeInfo>> data) {
        super.setData(data);
        setCategories(new StampedListTimeSeries((StampedListSeries<TradeInfo>) data));
    }

    @Override
    public Node paintNode(Instant time, List<TradeInfo> tradeInfoList, Node node) {
        if(tradeInfoList.size()>0){
            Group group = (Group)node;
            if(group==null){
                group = new Group();
                group.setId(buildId(time));
            }
            double x = chart.getCoordByCategory(time);
            for (int j=0; j<tradeInfoList.size(); j++){
                TradeInfo tradeInfo = tradeInfoList.get(j);
                double y = chart.getCoordByVal(tradeInfo.getPrice());
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
            }
            return group;
        }
        return null;
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

    private String buildId(Instant time){
        return ID_PREFIX+"@"+time.toString();
    }
}
