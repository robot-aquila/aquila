package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.formatters.DefaultTimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.objects.ChartObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TiM on 22.12.2016.
 */
public class Chart extends ScatterChart {

    public static final String CURRENT_POSITION_CHANGE="CURRENT_POSITION_CHANGE";
    public static final String NUMBER_OF_POINTS_CHANGE="NUMBER_OF_POINTS_CHANGE";
    private int numberOfPoints = 15;
    private int currentPosition = 0;
    private Series highSeries = new Series();
    private Series lowSeries = new Series();
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private final List<LocalDateTime> xValues = new ArrayList<>();
    private ActionListener actionListener;
    private TimeAxisSettings timeAxisSettings = new DefaultTimeAxisSettings();
    private final List<ChartObject> chartObjects = new ArrayList<>();

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

        this.setOnMouseClicked(event -> {
//            setCurrentPosition(getCurrentPosition()+1);
        });

        this.setOnScroll(event -> {
            if (event.isControlDown()) {
                setNumberOfPoints(getNumberOfPoints() - (int) Math.signum(event.getDeltaY()));
            } else {
                setCurrentPosition(getCurrentPosition() - (int) Math.signum(event.getDeltaY()));
            }

        });

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

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(Integer numberOfPoints) {
        if (numberOfPoints < 2) {
            numberOfPoints = 2;
        }
        if (numberOfPoints > xValues.size()) {
            numberOfPoints = xValues.size();
        }
        this.numberOfPoints = numberOfPoints;
        if(actionListener!=null){
            actionListener.actionPerformed(new ActionEvent(this, 1, NUMBER_OF_POINTS_CHANGE));
        }
        setCurrentPosition(getCurrentPosition());
    }

    public void setCurrentPosition(Integer currentPosition) {
        updateXValues();
        if(xValues.size()==0){
            return;
        }
        if (/*xValues.size() < numberOfPoints || */currentPosition < 0) {
            setCurrentPosition(0);
        } else if (currentPosition > 0 && currentPosition > xValues.size() - numberOfPoints) {
            setCurrentPosition(xValues.size() - numberOfPoints);
        } else {
            this.currentPosition = currentPosition;

            double maxY = 0;
            double minY = 1e6;
            for(ChartObject obj: chartObjects){
                Pair<Double, Double> interval = obj.getYInterval();
                if(interval.getRight() > maxY){
                    maxY = interval.getRight();
                }
                if(interval.getLeft() < minY){
                    minY = interval.getLeft();
                }
            }

            updateYInterval(minY, maxY);

            xAxis.setLowerBound(currentPosition - 1);
            xAxis.setUpperBound(currentPosition+numberOfPoints);
            if(actionListener!=null){
                actionListener.actionPerformed(new ActionEvent(this, 1, CURRENT_POSITION_CHANGE));
            }
        }
    }

    public void updateYInterval(double minY, double maxY){
        highSeries.getData().clear();
        lowSeries.getData().clear();
        highSeries.getData().add(new Data<>(currentPosition, maxY));
        lowSeries.getData().add(new Data<>(currentPosition, minY));
    }

    public Pair<LocalDateTime, LocalDateTime> getCurrentTimeInterval(){
        LocalDateTime min = null;
        LocalDateTime max = null;
        int cnt = xValues.size();
        if(cnt > 0){
            min = xValues.get(currentPosition);
            if(cnt >= currentPosition + numberOfPoints){
                max = xValues.get(currentPosition + numberOfPoints - 1);
            } else {
                max = xValues.get(cnt - 1);
            }
        }
        return new ImmutablePair<>(min, max);
    }

    public boolean isTimeDisplayed(LocalDateTime time){
        Pair<LocalDateTime, LocalDateTime> displayedInterval = getCurrentTimeInterval();
        return time.equals(displayedInterval.getLeft())||
                time.equals(displayedInterval.getRight())||
                (time.isBefore(displayedInterval.getRight()) && time.isAfter(displayedInterval.getLeft()));
    }

    public void refresh(){
        Platform.runLater(()->{
            setCurrentPosition(getCurrentPosition());
        });
    }

    public void refresh(int newCurrentPosition){
        Platform.runLater(()->{
            setCurrentPosition(newCurrentPosition);
        });
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

    @Override
    protected void layoutPlotChildren() {
        Pair<LocalDateTime, LocalDateTime> interval = getCurrentTimeInterval();
        for(int i=getPlotChildren().size()-1; i>=0; i--){
            Node node = (Node) getPlotChildren().get(i);
            if(node.getId() == null || node.getId().equals("")){
                getPlotChildren().remove(node);
            }
        }
//        getPlotChildren().clear();

        for(ChartObject obj: chartObjects){
            getPlotChildren().addAll(obj.paint());
        }

        for(int i=getPlotChildren().size()-1; i>=0; i--){
            Node node = (Node) getPlotChildren().get(i);
            if(node.getId() != null){
                String timeStr = node.getId();
                LocalDateTime time = null;
                try {
                    time = LocalDateTime.parse(timeStr);
                } catch (Exception e){

                }
                if(time!=null && (time.isBefore(interval.getLeft()) || time.isAfter(interval.getRight()))){
                    getPlotChildren().remove(node);
                }
            }
        }

        updateStyles();
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

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setTimeAxisSettings(TimeAxisSettings timeAxisSettings) {
        this.timeAxisSettings = timeAxisSettings;
    }

    public ChartObject getChartObject(int i) {
        return chartObjects.get(i);
    }

    public void addChartObject(ChartObject object){
        object.setChart(this);
        chartObjects.add(object);
        updateXValues();
        refresh();
    }

    public void removeChartObject(ChartObject object){
        object.setChart(null);
        chartObjects.remove(object);
        updateXValues();
        refresh();
    }

    public void clearChartObjects(){
        for(ChartObject object: chartObjects){
            object.setChart(null);
        }
        chartObjects.clear();
        updateXValues();
        refresh();
    }

    public List<LocalDateTime> getXValues() {
        return xValues;
    }

    private void updateXValues(){
        xValues.clear();
        for(ChartObject obj: chartObjects){
            List<LocalDateTime> list = obj.getXValues();
            for(LocalDateTime time: list){
                if(!xValues.contains(time)){
                    xValues.add(time);
                }
            }
        }
        Collections.sort(xValues);
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

