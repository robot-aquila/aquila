package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.Utils;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListSeries;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListTimeSeries;

import java.time.Instant;
import java.util.ArrayList;
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
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        if(getCategories()!=null) {
            int cnt = getCategories().getLength();
            for (int i = 0; i < cnt; i++) {
                Instant time = null;
                List<TradeInfo> tradeInfoList = null;
                try {
                    time = getCategories().get(i);
                    tradeInfoList = data.get(i);
                } catch (ValueException e) {
                    e.printStackTrace();
                }

                if(time!=null && tradeInfoList != null && tradeInfoList.size()>0 && chart.isCategoryDisplayed(time)){
                    Node node = chart.getNodeById(buildId(time));
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
                        chart.addObjectBounds(arrow.getBoundsInParent(), getTooltipText(tradeInfo));
                    }
                    if(node==null){
                        result.add(group);
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected double getMaxValue(List<TradeInfo> value) {
        return value.stream().mapToDouble(ti-> ti.getPrice()).max().orElse(0d);
    }

    @Override
    protected double getMinValue(List<TradeInfo> value) {
        return value.stream().mapToDouble(ti-> ti.getPrice()).min().orElse(1e6);
    }

    private String getTooltipText(TradeInfo tradeInfo) {
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
