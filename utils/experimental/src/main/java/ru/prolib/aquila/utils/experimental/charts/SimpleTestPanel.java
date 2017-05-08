package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.scene.Node;
import org.threeten.extra.Interval;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.ta.QEMA;
import ru.prolib.aquila.utils.experimental.charts.ChartPanel;
import ru.prolib.aquila.utils.experimental.charts.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.charts.fxcharts.CBFXChartPanel;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.indicators.forms.IndicatorParams;
import ru.prolib.aquila.utils.experimental.charts.indicators.forms.QEMAIndicatorParams;
import ru.prolib.aquila.utils.experimental.charts.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.ChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.VolumeChartLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * Created by TiM on 25.12.2016.
 */
public class SimpleTestPanel extends JPanel {

    private final CBFXChartPanel panel;
    private ObservableSeries<Candle> candleData;

    public SimpleTestPanel() {
        super();
        setLayout(new BorderLayout());

        candleData = new ObservableSeriesImpl<Candle>(new EventQueueImpl(), getRandomData());
        Series<Double> candleCloseData = new CandleCloseSeries(candleData);
        Series<Double> qema7 = new QEMA("QEMA_7", candleCloseData, 7);
        Series<Double> qema14 = new QEMA("QEMA_14", candleCloseData, 14);

        /* три строчки */
        panel = new CBFXChartPanel(candleData);
        panel.addSmoothLine(qema7).setStyleClass("line-magenta");
        panel.addSmoothLine(qema14).setStyleClass("line-blue");

        add(panel, BorderLayout.CENTER);
    }

    /* генерация данных, изменение и добавление данных в candleData, меню для тестирования изменения данных */
    private SeriesImpl<Candle> getRandomData(){
        double previousClose = 1850;
        SeriesImpl<Candle> data = new SeriesImpl<>();
        Instant start = Instant.parse("2016-12-31T19:00:00.000Z");
        int step = 15;
        for (int i = 0; i < 150; i++) {
            Interval interval = Interval.of(start.plus(step*i, ChronoUnit.MINUTES), start.plus(step*(i+1), ChronoUnit.MINUTES));
            double open = previousClose;
            double close = getNewValue(open);
            double high = Math.max(open + getRandom(),close);
            double low = Math.min(open - getRandom(),close);
            long volume = Math.round(Math.random() * 5000);
            previousClose = close;
            try {
                data.add(new Candle(interval, open, high, low, close, volume));
            } catch (ValueException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    protected double getNewValue(double previousValue) {
        int sign;
        if( Math.random() < 0.5 ) {
            sign = -1;
        } else {
            sign = 1;
        }
        return getRandom() * sign + previousValue;
    }


    protected double getRandom() {
        double newValue = 0;
        newValue = Math.random() * 10;
        return newValue;
    }

    private void changeCandle(){
        Platform.runLater(()->{
            Candle candle = null;
            try {
                candle = candleData.get();
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(candle!=null){
                long vol = candle.getVolume() + Math.round(Math.random() * 500);
                double close = candle.getClose() + (Math.random()-0.5)*2;
                double high = close>candle.getHigh()?close:candle.getHigh();
                double low = close<candle.getLow()?close:candle.getLow();
                Candle newCandle = new Candle(candle.getInterval(), candle.getOpen(), high, low, close, vol);
                try {
                    ((EditableSeries)candleData).set(newCandle);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addZeroCandle(){
        Platform.runLater(()->{
            Candle candle = null;
            try {
                candle = candleData.get();
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(candle!=null){
                Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
                double open = candle.getClose();
                double close = open;
                double high = open;
                double low = open;
                Candle newCandle = new Candle(interval, open, high, low, close, 0L);
                try {
                    ((EditableSeries)candleData).add(newCandle);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu main = new JMenu("Main");
        JMenuItem change = new JMenuItem("Change last candle");
        change.addActionListener(e -> changeCandle());
        main.add(change);
        JMenuItem add = new JMenuItem("Add zero candle");
        add.addActionListener(e -> addZeroCandle());
        main.add(add);
        menuBar.add(main);
        return menuBar;
    }
}
