package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import org.threeten.extra.Interval;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.ta.HIGH;
import ru.prolib.aquila.core.data.ta.LOW;
import ru.prolib.aquila.core.data.ta.QATR;
import ru.prolib.aquila.core.data.ta.QEMA;
import ru.prolib.aquila.utils.experimental.charts.fxcharts.CBFXChartPanel;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeInfo;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.CBSwingChartPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.COLOR_BEAR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.COLOR_BULL;

/**
 * Created by TiM on 25.12.2016.
 */
public class SimpleTestPanel extends JPanel {

    private final CBSwingChartPanel panel;
    private ObservableSeries<Candle> candleData;
    private StampedListSeries<TradeInfo> tradesData;
    private final Instant start = Instant.parse("2016-12-31T19:00:00.000Z");
    private final int step = 15;

    public SimpleTestPanel() {
        super();
        setLayout(new BorderLayout());

        EventQueue eventQueue = new EventQueueImpl();
        candleData = new ObservableSeriesImpl<Candle>(eventQueue, getRandomData());
        Series<Double> candleCloseData = new CandleCloseSeries(candleData);
        Series<Double> qema7 = new QEMA("QEMA_7", candleCloseData, 7);
        Series<Double> qema14 = new QEMA("QEMA_14", candleCloseData, 14);
        Series<Double> h = new CandleHighSeries(candleData);
        tradesData = new StampedListSeries<TradeInfo>("TRADES", TimeFrame.M15, eventQueue);

        /* три строчки */
        panel = new CBSwingChartPanel(candleData);
        panel.addSmoothLine(qema7).setColor(Color.BLUE);
        panel.addSmoothLine(qema14).setColor(Color.MAGENTA);
        panel.addVolumes();
        panel.addTrades();
        panel.setTradesData(tradesData);

        panel.addPolyLine(new HIGH("HIGH_20", candleData, 20)).setColor(COLOR_BULL);
        panel.addPolyLine(new LOW("LOW_20", candleData, 20)).setColor(COLOR_BEAR);
        panel.addPolyLine("QATR", new QATR("QATR_20", candleData, 20));

        add(panel, BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.setCurrentPosition(candleData.getLength());
            }
        });

    }

    /* генерация данных, изменение и добавление данных в candleData, меню для тестирования изменения данных */
    private SeriesImpl<Candle> getRandomData(){
        double previousClose = 1850;
        SeriesImpl<Candle> data = new SeriesImpl<>();
        for (int i = 0; i < 500; i++) {
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
    }

    private void addZeroCandle(){
            Candle candle = null;
            double lastClose;
            Interval interval;
            try {
                candle = candleData.get();
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(candle!=null) {
                lastClose = candle.getClose();
                interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
            } else {
                lastClose = getRandom();
                interval = Interval.of(start, start.plus(step, ChronoUnit.MINUTES));
            }
            double open = lastClose;
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

    public JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu main = new JMenu("Main");
        JMenuItem refresh = new JMenuItem("Refresh");
        refresh.addActionListener(e -> {
            this.panel.repaint();});
        main.add(refresh);
        JMenuItem change = new JMenuItem("Change last candle");
        change.addActionListener(e -> changeCandle());
        main.add(change);
        JMenuItem add = new JMenuItem("Add zero candle");
        add.addActionListener(e -> addZeroCandle());
        main.add(add);
        JMenuItem start = new JMenuItem("Start change");
        start.addActionListener(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Start change");
                    for(int i=0; i<1000; i++){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        changeCandle();
                    }
                    System.out.println("Stop change");
                }
            }).start();

        });
        main.add(start);

        final JMenuItem miAddTrade = new JMenuItem("Add trade");
        miAddTrade.addActionListener(e -> {
            try {
                tradesData.add(new TradeInfo(candleData.get(10).getStartTime().plus(12, ChronoUnit.MINUTES),
                        OrderAction.SELL,
                        candleData.get(10).getBodyMiddle(),
                        500L));
                tradesData.add(new TradeInfo(candleData.get(11).getStartTime().plus(12, ChronoUnit.MINUTES),
                        OrderAction.BUY,
                        candleData.get(11).getBodyMiddle(),
                        500L));
                tradesData.add(new TradeInfo(candleData.get(12).getStartTime().plus(12, ChronoUnit.MINUTES),
                        OrderAction.SELL,
                        candleData.get(12).getBodyMiddle(),
                        500L));
                tradesData.add(new TradeInfo(candleData.get(13).getStartTime().plus(12, ChronoUnit.MINUTES),
                        OrderAction.BUY,
                        candleData.get(13).getBodyMiddle(),
                        500L));
            } catch (ValueException e1) {
                e1.printStackTrace();
            }
        });
        main.add(miAddTrade);

        menuBar.add(main);
        return menuBar;
    }
}
