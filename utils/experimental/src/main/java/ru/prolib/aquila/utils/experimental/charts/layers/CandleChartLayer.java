package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleStartTimeSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.Utils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 27.01.2017.
 */
public class CandleChartLayer extends AbstractChartLayer<Instant, Candle> {

    private final double MIN_WIDTH = 5;
    private final double WIDTH_RATIO = 1.7;

    @Override
    public void setData(Series<Candle> data) {
        super.setData(data);
        setCategories(new CandleStartTimeSeries(data));
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        int cnt = chart.getCategories().size();
        for (int i = 0; i < cnt; i++) {
            Instant time = chart.getCategories().get(i);
            Candle candle = null;
            try {
                candle = getByCategory(time);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(candle != null){
                Node node = chart.getNodeById(getIdByCategory(time));
                double x = chart.getCoordByCategory(time);

                double height = Math.abs(chart.getCoordByVal(candle.getOpen()) - chart.getCoordByVal(candle.getClose()));
                if(height==0){
                    height = 1;
                }
                double width = chart.getWidth()/cnt/ WIDTH_RATIO;
                if(width< MIN_WIDTH){
                    width = MIN_WIDTH;
                }

                Line line;
                Rectangle body;
                if(node == null){
                    line = new Line(x, chart.getCoordByVal(candle.getHigh()), x, chart.getCoordByVal(candle.getLow()));
                    line.getStyleClass().add("candle-line");
                    body = new Rectangle(x, chart.getCoordByVal(candle.getBodyMiddle()), width, height);
                } else {
                    line = (Line) ((Group)node).getChildren().get(0);
                    line.setStartX(x);
                    line.setEndX(x);
                    line.setStartY(chart.getCoordByVal(candle.getHigh()));
                    line.setEndY(chart.getCoordByVal(candle.getLow()));

                    body = (Rectangle) ((Group)node).getChildren().get(1);
                    body.setX(x);
                    body.setY(chart.getCoordByVal(candle.getBodyMiddle()));
                    body.setWidth(width);
                    body.setHeight(height);
                }

                body.setLayoutX(-width/2);
                body.setLayoutY(-height/2);
                body.getStyleClass().clear();
                if(candle.getOpen()<candle.getClose()){
                    body.getStyleClass().add("candle-body-bull");
                } else {
                    body.getStyleClass().add("candle-body-bear");
                }
                body.applyCss();
                chart.addObjectBounds(body.getBoundsInParent(), getTooltipText(candle));

                if(node==null){
                    Group group = new Group(line, body);
                    group.setId(getIdByCategory(time));
                    result.add(group);
                }
            }
        }
        return result;
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
    protected String getIdPrefix() {
        return "CANDLE";
    }

    private String getTooltipText(Candle candle) {
        return String.format(
                "%s%n"+
                "OPEN: %8.2f%n" +
                "HIGH: %8.2f%n" +
                "LOW:  %8.2f%n" +
                "CLOSE:%8.2f",
                Utils.instantToStr(candle.getStartTime()),
                candle.getOpen(),
                candle.getHigh(),
                candle.getLow(),
                candle.getClose());
    }
}
