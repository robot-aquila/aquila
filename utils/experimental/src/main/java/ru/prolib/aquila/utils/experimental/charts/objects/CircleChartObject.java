package ru.prolib.aquila.utils.experimental.charts.objects;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.Chart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 28.01.2017.
 */
public class CircleChartObject implements ChartObject {

    private Chart chart;
    private LocalDateTime x;
    private double y;
    private double r;

    public CircleChartObject(LocalDateTime x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public List<LocalDateTime> getXValues() {
        List<LocalDateTime> list = new ArrayList<>();
        list.add(x);
        return list;
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        double y = chart.getY(this.y);
        double r = chart.getDistance(this.r);
        Node circle = new Circle(chart.getX(x), y, r);
        result.add(circle);
        return result;
    }

    @Override
    public Pair<Double, Double> getYInterval() {
        if(chart.isTimeDisplayed(x)){
            return new ImmutablePair<>(y-r, y+r);
        }
        return new ImmutablePair<>(1e6, 0.);

    }
}
