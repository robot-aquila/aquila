package ru.prolib.aquila.utils.experimental.charts.objects;

import javafx.scene.Node;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.Chart;
import ru.prolib.aquila.utils.experimental.charts.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.interpolator.CubicCurveCalc;
import ru.prolib.aquila.utils.experimental.charts.interpolator.Segment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Created by TiM on 31.01.2017.
 */
public class IndicatorChartObject implements ChartObject {
    private Chart chart;
    private List<Pair<LocalDateTime, Double>> data = Collections.synchronizedList(new ArrayList<>());
    private String styleClass;
    private final Calculator calculator;
    private final String id;

    public IndicatorChartObject(String id, Calculator calculator) {
        this.id = id;
        this.calculator = calculator;
    }

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public List<LocalDateTime> getXValues() {
        List<LocalDateTime> result = new ArrayList<>();
        for(Pair<LocalDateTime, Double> p: data){
            result.add(p.getLeft());
        }
        return result;
//        return data.stream().filter(p->p != null).map(p-> p.getLeft()).collect(Collectors.toList());
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        Node node = chart.getNodeById(id);
        Path path;
        if(node!=null && node instanceof Path){
            path = (Path) node;
            path.getElements().clear();
        } else {
            path = new Path();
            path.setId(id);
            result.add(path);
        }
        path.toFront();
        List<Pair<Double, Double>> points = new ArrayList<>();
        data.stream().filter(v-> chart.isTimeDisplayed(v.getLeft()))
                .forEach(v-> points.add(new ImmutablePair(chart.getX(v.getLeft()), chart.getY(v.getRight()))));
        List<Segment> segments = CubicCurveCalc.calc(points);

        for(Segment s: segments){
            PathElement element;
            if(path.getElements().size()==0){
                element = new MoveTo(s.getX1(), s.getY1());
                path.getElements().add(element);
            }
            element = new CubicCurveTo(s.getXc1(), s.getYc1(),
                    s.getXc2(), s.getYc2(), s.getX2(), s.getY2());
            path.getElements().add(element);
        }

        if(styleClass !=null){
            path.getStyleClass().clear();
            path.getStyleClass().add(styleClass);
            path.applyCss();
        }

        return result;
    }

    @Override
    public Pair<Double, Double> getYInterval() {
        OptionalDouble maxY = data.stream()
                .filter(p-> chart.isTimeDisplayed(p.getLeft()))
                .mapToDouble(p-> p.getRight()).max();
        OptionalDouble minY = data.stream()
                .filter(p-> chart.isTimeDisplayed(p.getLeft()))
                .mapToDouble(p-> p.getRight()).min();
        return new ImmutablePair<>(minY.orElse(1e6), maxY.orElse(0));
    }

    public List<Pair<LocalDateTime, Double>> getData() {
        return data;
    }

    public void setData(List<Candle> data) {
        this.data.clear();
        this.data.addAll(calculator.calculate(data));
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
}
