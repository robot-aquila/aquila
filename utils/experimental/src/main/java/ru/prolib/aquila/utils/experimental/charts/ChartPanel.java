package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.objects.ChartObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by TiM on 22.12.2016.
 */
public class ChartPanel extends JFXPanel {

    public static final String CURRENT_POSITION_CHANGE="CURRENT_POSITION_CHANGE";
    public static final String NUMBER_OF_POINTS_CHANGE="NUMBER_OF_POINTS_CHANGE";
    private ActionListener actionListener;

    private final Chart chart;
    private final List<ChartObject> chartObjects = new ArrayList<>();
    private final List<LocalDateTime> xValues = new ArrayList<>();
    private int currentPosition = 0;
    private int numberOfPoints = 15;

    public ChartPanel() {
        super();
        chart = new Chart(new NumberAxis(), new NumberAxis());
        this.setScene(new Scene(chart));
        chart.setOnMouseClicked(event->{

        });
        chart.setOnScroll(event -> {
            if (event.isControlDown()) {
                setNumberOfPoints(getNumberOfPoints() - (int) Math.signum(event.getDeltaY()));
            } else {
                setCurrentPosition(getCurrentPosition() - (int) Math.signum(event.getDeltaY()));
            }

        });

        this.getScene().widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> refresh());
        this.getScene().heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> refresh());

    }

    public List<LocalDateTime> getXValues(){
        return xValues;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        updateXValues();
        if(xValues.size()==0){
            return;
        }
        if (currentPosition < 0) {
            setCurrentPosition(0);
        } else if (currentPosition > 0 && currentPosition > xValues.size() - numberOfPoints) {
            setCurrentPosition(xValues.size() - numberOfPoints);
        } else {
            this.currentPosition = currentPosition;
            refresh();
            if(actionListener!=null){
                actionListener.actionPerformed(new ActionEvent(this, 1, CURRENT_POSITION_CHANGE));
            }
        }
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
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
        setCurrentPosition(currentPosition);
    }

    public void refresh(){
        Platform.runLater(()->{
            setAxisValues();
            chart.updatePlotChildren(paintChartObjects());
        });
    }

    public void addChartObject(ChartObject object){
        object.setChart(chart);
        chartObjects.add(object);
    }

    public void removeChartObject(ChartObject object){
        object.setChart(chart);
        chartObjects.add(object);
    }

    public List<ChartObject> getChartObjects() {
        return chartObjects;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setTimeAxisSettings(TimeAxisSettings timeAxisSettings) {
        chart.setTimeAxisSettings(timeAxisSettings);
    }

    public boolean isTimeDisplayed(LocalDateTime time){
        return chart.isTimeDisplayed(time);
    }

    public boolean isTimeDisplayed(Instant time){
        return isTimeDisplayed(Utils.toLocalDateTime(time));
    }

    private void updateXValues(){
        Set<LocalDateTime> set = new TreeSet<>();

        xValues.clear();
        for(ChartObject obj: chartObjects){
            set.addAll(obj.getXValues());
        }
        xValues.addAll(set);
    }

    private void setAxisValues(){
        List<LocalDateTime> xValuesToDisplay = new ArrayList<>();
        int cnt = Math.min(currentPosition + numberOfPoints, xValues.size());
        for(int i=currentPosition; i<cnt; i++){
            xValuesToDisplay.add(xValues.get(i));
        }

        double maxY = 0;
        double minY = 1e6;

        for(ChartObject obj: chartObjects){
            Pair<Double, Double> interval = obj.getYInterval(xValuesToDisplay);
            if(interval.getRight() > maxY){
                maxY = interval.getRight();
            }
            if(interval.getLeft() < minY){
                minY = interval.getLeft();
            }
        }

        chart.setAxisValues(xValuesToDisplay, minY, maxY);
    }

    private List<Node> paintChartObjects(){
        List<Node> result = new ArrayList<>();
        for(ChartObject obj: chartObjects){
            result.addAll(obj.paint());
        }
        return result;
    }

    public Chart getChart() {
        return chart;
    }
}

