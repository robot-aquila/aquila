package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.scene.Group;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.formatters.DefaultTimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 22.12.2016.
 */
public class CandleChart extends ScatterChart {

    public static final String CURRENT_POSITION_CHANGE="CURRENT_POSITION_CHANGE";
    public static final String NUMBER_OF_POINTS_CHANGE="NUMBER_OF_POINTS_CHANGE";
    private final double MIN_WIDTH = 5;
    private List<Candle> data = new ArrayList<>();
    private int numberOfPoints = 15;
    private int currentPosition = 0;
    private Series candleSeries = new Series();
    private Series highSeries = new Series();
    private Series lowSeries = new Series();
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private final List<LocalDateTime> xValues = new ArrayList<>();
    private ActionListener actionListener;
    private TimeAxisSettings timeAxisSettings = new DefaultTimeAxisSettings();

    public CandleChart(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        setAnimated(false);
        getStylesheets().add(CandleChart.class.getResource("/charts.css").toExternalForm());

        setLegendVisible(false);
        getData().add(candleSeries);
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

    public void setCandleData(List<Candle> data) {
        this.data.clear();
        this.data.addAll(data);
        xValues.clear();
        for(int i=0; i<data.size(); i++){
            xValues.add(data.get(i).getStartTime().atOffset(ZoneOffset.UTC).toLocalDateTime());
        }
        setCurrentPosition(0);
    }

    public void addCandle(Candle candle){
        int idx = data.size()-1;
        boolean lastCandleDisplayed = currentPosition >= data.size() - numberOfPoints;
        if(idx<0 || candle.getStartTime().isAfter(data.get(idx).getStartTime())){
            data.add(candle);
            xValues.add(candle.getStartTime().atOffset(ZoneOffset.UTC).toLocalDateTime());
        } else {
            throw new IllegalArgumentException("We can add candle only to the end.");
        }
        if(lastCandleDisplayed){
            setCurrentPosition(getCurrentPosition()+1);
        }
    }

    public void setLastClose(double close){
        int idx = data.size()-1;
        boolean lastCandleDisplayed = currentPosition >= data.size() - numberOfPoints;
        if(idx < 0){
            throw new IllegalStateException("Empty candle list");
        }
        Candle candle = data.get(idx);
        double high = close>candle.getHigh()?close:candle.getHigh();
        double low = close<candle.getLow()?close:candle.getLow();
        data.set(idx, new Candle(candle.getInterval(), candle.getOpen(), high, low, close, candle.getVolume()));
        if(lastCandleDisplayed){
            setCurrentPosition(getCurrentPosition());
        }
    }

    public int getCandleDataCount(){
        return data.size();
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
        if (numberOfPoints > data.size()) {
            numberOfPoints = data.size();
        }
        this.numberOfPoints = numberOfPoints;
        if(actionListener!=null){
            actionListener.actionPerformed(new ActionEvent(this, 1, NUMBER_OF_POINTS_CHANGE));
        }
        setCurrentPosition(getCurrentPosition());
    }

    public void setCurrentPosition(Integer currentPosition) {
        if (data.size() < numberOfPoints || currentPosition < 0) {
            setCurrentPosition(0);
        } else if (currentPosition > 0 && currentPosition > data.size() - numberOfPoints) {
            setCurrentPosition(data.size() - numberOfPoints);
        } else {
            this.currentPosition = currentPosition;
            candleSeries.getData().clear();
            highSeries.getData().clear();
            lowSeries.getData().clear();
            for (int i = 0; i < numberOfPoints; i++) {
                Candle candle = data.get(i + currentPosition);
                int xVal = currentPosition + i;
                candleSeries.getData().add(new Data<>(xVal, candle.getBodyMiddle(), candle));
                highSeries.getData().add(new Data<>(xVal, candle.getHigh()));
                lowSeries.getData().add(new Data<>(xVal, candle.getLow()));
            }
            xAxis.setLowerBound(currentPosition - 1);
            xAxis.setUpperBound(currentPosition+numberOfPoints);
            if(actionListener!=null){
                actionListener.actionPerformed(new ActionEvent(this, 1, CURRENT_POSITION_CHANGE));
            }
        }
    }

    @Override
    protected void dataItemAdded(Series series, int itemIndex, Data item) {
    }

    private double getY(double chartY) {
        return yAxis.getDisplayPosition(chartY);
    }

    @Override
    protected void layoutPlotChildren() {
        getPlotChildren().clear();
        for(int i=0; i<candleSeries.getData().size(); i++){
            drawCandle(i);
        }
        updateStyles();
    }

    private void drawCandle(int i){
        Data item = (Data)candleSeries.getData().get(i);
        removeDataItemFromDisplay(candleSeries, item);
        Candle candle = (Candle) item.getExtraValue();
        double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
        Line line = new Line(x, getY(candle.getHigh()), x, getY(candle.getLow()));
        line.getStyleClass().add("candle-line");

        double height = Math.abs(getY(candle.getOpen()) - getY(candle.getClose()));
        if(height==0){
            height = 1;
        }
        double width = getWidth()/candleSeries.getData().size()/4;
        if(width< MIN_WIDTH){
            width = MIN_WIDTH;
        }
        Rectangle body = new Rectangle(x, getY(candle.getBodyMiddle()), width, height);
        body.setLayoutX(-width/2);
        body.setLayoutY(-height/2);
        if(candle.getOpen()<candle.getClose()){
            body.getStyleClass().add("candle-body-bull");
        } else {
            body.getStyleClass().add("candle-body-bear");
        }

        Group group = new Group(line, body);
        item.setNode(group);
        getPlotChildren().add(group);
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
}

