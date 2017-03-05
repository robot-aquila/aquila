package ru.prolib.aquila.utils.experimental.charts.objects;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.Chart;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.prolib.aquila.utils.experimental.charts.Utils.toLocalDateTime;

/**
 * Created by TiM on 27.01.2017.
 */
public class VolumeChartObject implements ChartObject{

    private Map<LocalDateTime, Long> data = new TreeMap<>();
    private Chart chart;
    private final double MIN_WIDTH = 5;

    public void setData(List<Candle> data) {
        this.data.clear();
        for(Candle candle: data){
            LocalDateTime time = toLocalDateTime(candle.getStartTime());
            this.data.put(time, candle.getVolume());
        }
    }

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public List<LocalDateTime> getXValues() {
        List<LocalDateTime> values = new ArrayList<>(data.keySet());
        return values;
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        int cnt = chart.getXValues().size();
        for (int i = 0; i < cnt; i++) {
            LocalDateTime time = chart.getXValues().get(i);
            Long volume = data.get(time);
            if(volume != null){
                Node node = chart.getNodeById(time.toString());
                double x = chart.getX(time);
                double y = chart.getY(volume/2);

                double height = Math.abs(chart.getY(volume) - chart.getY(0));
                if(height==0){
                    height = 1;
                }
                double width = chart.getWidth()/cnt/4;
                if(width< MIN_WIDTH){
                    width = MIN_WIDTH;
                }

                Rectangle body;
                if(node == null){
                    body = new Rectangle(x, y, width, height);
                } else {
                    body = (Rectangle)node;
                    body.setX(x);
                    body.setY(y);
                    body.setWidth(width);
                    body.setHeight(height);
                }

                body.setLayoutX(-width/2);
                body.setLayoutY(-height/2);
                body.getStyleClass().clear();
                body.getStyleClass().add("volume-bar");
                body.applyCss();

                if(node==null){
                    body.setId(time.toString());
                    result.add(body);
                }
            }
        }
        return result;
    }

    @Override
    public Pair<Double, Double> getYInterval(List<LocalDateTime> xValues) {
        double minY = 1e6;
        double maxY = 0;
        for (LocalDateTime time: xValues) {
            Long volume = data.get(time);
            if(volume != null){
                if(volume > maxY){
                    maxY = volume;
                }
            }
        }
        minY = 0;
        return new ImmutablePair(minY, maxY);
    }

    public void addVolume(Candle candle){
        data.put(toLocalDateTime(candle.getStartTime()), candle.getVolume());
    }

    public void setLastVolume(Candle candle){
        LocalDateTime lastTime = toLocalDateTime(candle.getStartTime());
        data.put(lastTime, candle.getVolume());
    }

}
