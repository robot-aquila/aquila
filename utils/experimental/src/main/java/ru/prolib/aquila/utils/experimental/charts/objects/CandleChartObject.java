package ru.prolib.aquila.utils.experimental.charts.objects;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.Chart;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TiM on 27.01.2017.
 */
public class CandleChartObject implements ChartObject{

    private HashMap<LocalDateTime, Candle> data = new HashMap<>();
    private Chart chart;
    private final double MIN_WIDTH = 5;

    public void setData(List<Candle> data) {
        this.data.clear();
        for(Candle candle: data){
            LocalDateTime time = toLocalDateTime(candle.getStartTime());
            this.data.put(time, candle);
        }
        this.chart.refresh();
    }

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public List<LocalDateTime> getXValues() {
        List<LocalDateTime> values = new ArrayList<>(data.keySet());
        Collections.sort(values);
        return values;
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < chart.getNumberOfPoints(); i++) {
            if(i < chart.getXValues().size()){
                int idx = chart.getCurrentPosition() + i;
                LocalDateTime time = chart.getXValues().get(idx);
                Candle candle = data.get(time);
                if(candle != null){
                    Node node = chart.getNodeById(time.toString());
                    double x = chart.getX(time);

                    double height = Math.abs(chart.getY(candle.getOpen()) - chart.getY(candle.getClose()));
                    if(height==0){
                        height = 1;
                    }
                    double width = chart.getWidth()/chart.getNumberOfPoints()/4;
                    if(width< MIN_WIDTH){
                        width = MIN_WIDTH;
                    }

                    Line line;
                    Rectangle body;
                    if(node == null){
                        line = new Line(x, chart.getY(candle.getHigh()), x, chart.getY(candle.getLow()));
                        line.getStyleClass().add("candle-line");
                        body = new Rectangle(x, chart.getY(candle.getBodyMiddle()), width, height);
                    } else {
                        line = (Line) ((Group)node).getChildren().get(0);
                        line.setStartX(x);
                        line.setEndX(x);
                        line.setStartY(chart.getY(candle.getHigh()));
                        line.setEndY(chart.getY(candle.getLow()));

                        body = (Rectangle) ((Group)node).getChildren().get(1);
                        body.setX(x);
                        body.setY(chart.getY(candle.getBodyMiddle()));
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

                    if(node==null){
                        Group group = new Group(line, body);
                        group.setId(time.toString());
                        result.add(group);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Pair<Double, Double> getYInterval() {
        double minY = 1e6;
        double maxY = 0;
        for (int i = 0; i < chart.getNumberOfPoints(); i++) {
            int idx = chart.getCurrentPosition() + i;
            if(chart.getXValues().size()>idx){
                Candle candle = data.get(chart.getXValues().get(idx));
                if(candle != null){
                    double y = candle.getHigh();
                    if(y > maxY){
                        maxY = y;
                    }
                    y = candle.getLow();
                    if(y < minY){
                        minY = y;
                    }
                }
            }
        }
        return new ImmutablePair(minY, maxY);
    }

    private LocalDateTime toLocalDateTime(Instant time){
        return time.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

    public Candle getLastCandle(){
        if(data.size()==0){
            return null;
        }
        List<LocalDateTime> list = new ArrayList<>(data.keySet());
        Collections.sort(list);
        return data.get(list.get(list.size()-1));
    }

    public void addCandle(Candle candle){
        Candle lastCandle = getLastCandle();
        if(lastCandle==null || candle.getStartTime().isAfter(lastCandle.getStartTime())){
            data.put(toLocalDateTime(candle.getStartTime()), candle);
        } else {
            throw new IllegalArgumentException("We can add candle only to the end.");
        }

        Pair<LocalDateTime, LocalDateTime> displayedInterval = chart.getCurrentTimeInterval();
        if(lastCandle==null){
            chart.refresh();
        } else {
            LocalDateTime lastTime = toLocalDateTime(lastCandle.getStartTime());
            if(lastTime.equals(displayedInterval.getRight())){
                chart.refresh(chart.getCurrentPosition()+1);
            } else {
                chart.refresh();
            }
        }
    }

    public void setLastClose(double close){
        Candle lastCandle = getLastCandle();
        if(lastCandle == null){
            throw new IllegalStateException("Empty candle list");
        }
        double high = close>lastCandle.getHigh()?close:lastCandle.getHigh();
        double low = close<lastCandle.getLow()?close:lastCandle.getLow();
        LocalDateTime lastTime = toLocalDateTime(lastCandle.getStartTime());
        Candle newCandle = new Candle(lastCandle.getInterval(), lastCandle.getOpen(), high, low, close, lastCandle.getVolume());
        data.put(lastTime, newCandle);
        if(chart.isTimeDisplayed(lastTime)){
            chart.refresh();
        }
    }

}
