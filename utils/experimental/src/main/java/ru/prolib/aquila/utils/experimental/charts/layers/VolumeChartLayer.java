package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.time.Instant;

/**
 * Created by TiM on 27.01.2017.
 */
public class VolumeChartLayer extends AbstractChartLayer<Instant, Long> {

    private final double MIN_WIDTH = 5;
    private final double WIDTH_RATIO = 1.7;

    @Override
    public Node paintNode(Instant time, Long volume, Node node) {
        int cnt = chart.getCategories().size();
        double x = chart.getCoordByCategory(time);
        double y = chart.getCoordByVal(volume/2);

        double height = Math.abs(chart.getCoordByVal(volume) - chart.getCoordByVal(0));
        if(height==0){
            height = 1;
        }
        double width = chart.getWidth()/cnt/WIDTH_RATIO;
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
        return body;
    }

    @Override
    protected double getMaxValue(Long value) {
        return value.doubleValue();
    }

    @Override
    protected double getMinValue(Long value) {
        return 0d;
    }

    @Override
    protected String getIdPrefix() {
        return "VOLUME";
    }

    protected String createTooltipText(Long volume) {
        return String.format("VOLUME: %d", volume);
    }

}
