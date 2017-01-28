package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.objects.ChartObject;

import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by TiM on 22.12.2016.
 */
public class ChartPanel extends JFXPanel {

    private final Chart chart;

    public ChartPanel() {
        super();
        chart = new Chart(new NumberAxis(), new NumberAxis());
        this.setScene(new Scene(chart));
    }

    public List<LocalDateTime> getXValues(){
        return chart.getXValues();
    }

    public Integer getCurrentPosition() {
        return chart.getCurrentPosition();
    }

    public void setCurrentPosition(Integer currentPosition) {
        Platform.runLater(()->{
            chart.setCurrentPosition(currentPosition);
        });
    }

    public void scrollNext(int countStep){
        setCurrentPosition(getCurrentPosition()+countStep);
    }

    public void scrollPrev(int countStep){
        setCurrentPosition(getCurrentPosition()-countStep);
    }

    public Integer getNumberOfPoints() {
        return chart.getNumberOfPoints();
    }

    public void setNumberOfPoints(Integer numberOfPoints) {
        Platform.runLater(()->{
            chart.setNumberOfPoints(numberOfPoints);
        });
    }

    public ChartObject getChartObject(int i) {
        return chart.getChartObject(i);
    }

    public void addChartObject(ChartObject object){
        Platform.runLater(()->{
            chart.addChartObject(object);
        });
    }

    public void removeChartObject(ChartObject object){
        Platform.runLater(()->{
            chart.removeChartObject(object);
        });
    }

    public void clearChartObjects(){
        Platform.runLater(()->{
            chart.clearChartObjects();
        });
    }

    public void setActionListener(ActionListener actionListener) {
        chart.setActionListener(actionListener);
    }

    public void setTimeAxisSettings(TimeAxisSettings timeAxisSettings) {
        chart.setTimeAxisSettings(timeAxisSettings);
    }
}

