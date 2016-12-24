package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import ru.prolib.aquila.core.data.Candle;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by TiM on 22.12.2016.
 */
public class CandleChartPanel extends JFXPanel {

    private final CandleChart chart;

    public CandleChartPanel() {
        super();
        chart = new CandleChart(new NumberAxis(), new NumberAxis());
        this.setScene(new Scene(chart));
    }

    public void setCandleData(List<Candle> data){
        Platform.runLater(()-> {
            chart.setCandleData(data);
        });
    }

    public int getCandleDataCount(){
        return chart.getCandleDataCount();
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

    public void setActionListener(ActionListener actionListener) {
        chart.setActionListener(actionListener);
    }


}

