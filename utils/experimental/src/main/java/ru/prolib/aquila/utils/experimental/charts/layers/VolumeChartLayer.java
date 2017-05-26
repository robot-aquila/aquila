package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import ru.prolib.aquila.core.data.ValueException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 27.01.2017.
 */
public class VolumeChartLayer extends AbstractChartLayer<Instant, Long> {

    private final double MIN_WIDTH = 5;
    private final double WIDTH_RATIO = 1.7;

    @Override
    public List<Node> paint() {
        List<Node> result = new ArrayList<>();
        int cnt = chart.getCategories().size();
        for (int i = 0; i < cnt; i++) {
            Instant time = chart.getCategories().get(i);
            Long volume = null;
            try {
                volume = getByCategory(time);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(volume != null){
                Node node = chart.getNodeById(getIdByCategory(time));
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
                chart.addObjectBounds(body.getBoundsInParent(), getTooltipText(volume));
                if(node==null){
                    body.setId(getIdByCategory(time));
                    result.add(body);
                }
            }
        }
        return result;
    }

    @Override
    protected double getMaxValue(Long value) {
        return value.doubleValue();
    }

    @Override
    protected double getMinValue(Long value) {
        return value.doubleValue();
    }

    @Override
    protected String getIdPrefix() {
        return "VOLUME";
    }

    private String getTooltipText(long volume) {
        return String.format("VOLUME: %d", volume);
    }

}
