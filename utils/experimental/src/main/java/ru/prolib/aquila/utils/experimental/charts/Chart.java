package ru.prolib.aquila.utils.experimental.charts;

import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import ru.prolib.aquila.utils.experimental.charts.formatters.DefaultTimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TiM on 22.12.2016.
 */
public class Chart extends ScatterChart {

    private Series highSeries = new Series();
    private Series lowSeries = new Series();
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private List<LocalDateTime> xValues = Collections.synchronizedList(new ArrayList<>());
    private TimeAxisSettings timeAxisSettings = new DefaultTimeAxisSettings();

    public Chart(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        setAnimated(false);
        getStylesheets().add(Chart.class.getResource("/charts.css").toExternalForm());

        setLegendVisible(false);
        getData().add(highSeries);
        getData().add(lowSeries);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        yAxis.setForceZeroInRange(false);
        xAxis.setForceZeroInRange(false);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickCount(1);
        xAxis.setTickLabelRotation(-90);
        xAxis.setAutoRanging(false);

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int i = object.intValue();
                if(i>=0 && i<xValues.size()){
                    return getTimeLabelText(xValues.get(i));
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
    }

    public void setAxisValues(List<LocalDateTime> xValues, double minY, double maxY){
//        this.xValues = xValues;
        this.xValues.clear();
        this.xValues.addAll(xValues);
        xAxis.setLowerBound(-1);
        xAxis.setUpperBound(xValues.size());
        highSeries.getData().clear();
        lowSeries.getData().clear();
        highSeries.getData().add(new Data<>(0, maxY));
        lowSeries.getData().add(new Data<>(0, minY));
        List<Number> list = new ArrayList<>();
        for(int i=0; i<xValues.size(); i++){
            list.add(i);
        }
        xAxis.invalidateRange(list);
        this.layout();
    }

    public boolean isTimeDisplayed(LocalDateTime time){
        return xValues.contains(time);
    }

    public double getY(double chartY) {
        return yAxis.getDisplayPosition(chartY);
    }

    public double getX(LocalDateTime time) {
        int idx = xValues.indexOf(time);
        if(idx<0){
            throw new IllegalArgumentException("Can not find time: "+time.toString());
        }
        return xAxis.getDisplayPosition(idx);
    }

    public double getDistance(double distance){
        return Math.abs(getY(distance) - getY(0));
    }

    public void updatePlotChildren(List<Node> nodes){
        for(int i=getPlotChildren().size()-1; i>=0; i--){
            Node node = (Node) getPlotChildren().get(i);
            if(node.getId() == null || node.getId().equals("")){
                getPlotChildren().remove(node);
            }
        }

        getPlotChildren().addAll(nodes);

        for(int i=getPlotChildren().size()-1; i>=0; i--){
            Node node = (Node) getPlotChildren().get(i);
            if(node.getId() != null){
                String timeStr = node.getId();
                LocalDateTime time = null;
                try {
                    time = LocalDateTime.parse(timeStr);
                } catch (Exception e){

                }
                if(time!=null && !isTimeDisplayed(time)){
                    getPlotChildren().remove(node);
                }
            }
        }
        updateStyles();
        this.layout();
    }

    @Override
    protected void layoutPlotChildren() {
// do nothing
    }

    private void updateStyles() {
        xAxis.getChildrenUnmodifiable().stream().filter(n->!n.isVisible()).forEach((n)->n.setVisible(true));
        int[] idx = { 0 };
        xAxis.getChildrenUnmodifiable().filtered(n-> n instanceof Text).forEach(n ->{
            int i = xAxis.getTickMarks().get(idx[0]++).getValue().intValue();
            if(i>=0 && i < xValues.size()){
                if(timeAxisSettings.isMinorLabel(xValues.get(i))){
                    n.getStyleClass().add(timeAxisSettings.getMinorLabelStyleClass());
                } else {
                    n.getStyleClass().add(timeAxisSettings.getLabelStyleClass());
                }
            }
        });
    }

    private String getTimeLabelText(LocalDateTime time) {
        if(time==null){
            return "";
        }
        return timeAxisSettings.formatDateTime(time);
    }

    public void setTimeAxisSettings(TimeAxisSettings timeAxisSettings) {
        this.timeAxisSettings = timeAxisSettings;
    }

    public List<LocalDateTime> getXValues() {
        return xValues;
    }

    public Node getNodeById(String id){
        for(Object obj: getPlotChildren()) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                if (id.equals(node.getId())) {
                    return node;
                }
            }
        }
        return null;
    }

}

