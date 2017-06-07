package ru.prolib.aquila.utils.experimental.charts.indicators;

import javafx.scene.Node;
import javafx.scene.shape.Path;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.interpolator.*;
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
    private LineRenderer lineRenderer = new SmoothLineRenderer();

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
        currentTooltips.clear();
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
        List<Point> points = new ArrayList<>();
        for(int i=0; i<categories.getLength(); i++){
            Instant c = null;
            Double v = null;
            try {
                c = categories.get(i);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(chart.isCategoryDisplayed(c)){
                try {
                    v = data.get(i);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(v!=null){
                    points.add(new Point(chart.getCoordByCategory(c), chart.getCoordByVal(v)));
                    currentTooltips.put(c, createTooltipText(v));
                }
            }
        }

        lineRenderer.renderLine(path, points);

        if(styleClass !=null){
            path.getStyleClass().clear();
            path.getStyleClass().add(styleClass);
            path.applyCss();
        }

        return result;
    }

    public void setLineRenderer(LineRenderer lineRenderer) {
        this.lineRenderer = lineRenderer;
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

    @Override
    protected String createTooltipText(Double value) {
        return String.format("%s: %.2f", getId(), value);
    }
}
