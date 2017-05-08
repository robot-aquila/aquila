package ru.prolib.aquila.utils.experimental.charts.indicators;

import javafx.scene.Node;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.interpolator.CubicCurveCalc;
import ru.prolib.aquila.utils.experimental.charts.interpolator.Segment;
import ru.prolib.aquila.utils.experimental.charts.layers.AbstractChartLayer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 31.01.2017.
 */
public class IndicatorChartLayer extends AbstractChartLayer<Instant, Double> {
    private String styleClass;
    private final Calculator calculator;
    private final String id;

    public IndicatorChartLayer(IndicatorSettings settings) {
        this.calculator = settings.getCalculator();
        this.id = calculator.getId();
        this.styleClass = settings.getStyleClass();
    }

    @Override
    public void setData(Series<Double> data) {
        this.data = calculator.calculate(data);
    }

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        if(this.categories==null){
            return result;
        }
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
        for(int i=0; i<categories.getLength(); i++){
            Instant c = null;
            Double v = null;
            try {
                c = categories.get(i);
                v = data.get(i);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(v!=null && chart.isCategoryDisplayed(c)){
                points.add(new ImmutablePair(chart.getCoordByCategory(c), chart.getCoordByVal(v)));
            }
        }

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
    protected double getMaxValue(Double value) {
        return value;
    }

    @Override
    protected double getMinValue(Double value) {
        return value;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getId() {
        return id;
    }
}
