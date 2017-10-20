package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.tseries.*;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelHandler;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.*;
import ru.prolib.aquila.utils.experimental.chart.formatters.AbsNumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.chart.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.chart.formatters.NumberLabelFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.util.Random;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.BAR_COLOR;

/**
 * Created by TiM on 10.06.2017.
 */
public class MainChart {

    private static Random random = new Random();
    private static TSeriesNodeStorageKeys categoriesSeries;
    private static EditableTSeries<Candle> candles;
    private static ObservableTSeries<Candle> candlesObs;
    private static CandleVolumeTSeries volumes;
    private static EditableTSeries<Number> bidVolumes;
    private static EditableTSeries<Number> askVolumes;
    private static EditableTSeries<TradeInfoList> trades;
    private static CandleCloseTSeries closes;
    private static EventQueue eventQueue;

    private static BarChartPanel<Instant> chartPanel;


    public static void main(String[] args){

        createTestSeries();

        JFrame main = new JFrame("test");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BarChartPanelImpl<Instant> chartPanel = new BarChartPanelImpl<>(ChartOrientation.HORIZONTAL);
        chartPanel.getRootPanel().setPreferredSize(new Dimension(1230, 900));
        main.getContentPane().add(chartPanel.getRootPanel());
        main.pack();
        main.setVisible(true);

        BarChart<Instant> chart = chartPanel.addChart("CANDLES")
                .setHeight(600)
                .setValuesLabelFormatter(new NumberLabelFormatter())
                .addStaticOverlay("Price",0);
        chart.getTopAxis().setLabelFormatter(new InstantLabelFormatter());
        chart.getBottomAxis().setLabelFormatter(new InstantLabelFormatter());
        chart.addLayer(new CandleBarChartLayer<>(candles));
        chart.addLayer(new TradesBarChartLayer<Instant>(trades).setColor(TradesBarChartLayer.SELL_COLOR, Color.BLUE));
        chart.addLayer(new CurrentValueLayer<>(closes));

        chart.addSmoothLine(new CandleCloseTSeries("Close", candles)).setColor(Color.BLUE);

        chart = chartPanel.addChart("VOLUME");
        chartPanel.getChart("VOLUME").setValuesLabelFormatter(new NumberLabelFormatter().withPrecision(0));
        chart.addHistogram(volumes).setColor(BAR_COLOR);
        chart.getTopAxis().setLabelFormatter(new InstantLabelFormatter());
        chart.getBottomAxis().setLabelFormatter(new InstantLabelFormatter());

        chart = chartPanel.addChart("BID_ASK_VOLUME");
        chartPanel.getChart("BID_ASK_VOLUME").setValuesLabelFormatter(new AbsNumberLabelFormatter().withPrecision(0));
        chart.getTopAxis().setLabelFormatter(new InstantLabelFormatter());
        chart.getBottomAxis().setLabelFormatter(new InstantLabelFormatter());
        chart.addHistogram(bidVolumes)
                .setColor(Color.GREEN)
                .setParam(HistogramBarChartLayer.ZERO_LINE_ON_CENTER_PARAM, true);
        chart.addHistogram(askVolumes)
                .setColor(Color.RED)
                .setParam(HistogramBarChartLayer.INVERT_VALUES_PARAM, true)
                .setParam(HistogramBarChartLayer.ZERO_LINE_ON_CENTER_PARAM, true);

        chart.addSmoothLine(askVolumes)
                .setColor(Color.BLUE)
                .setParam(IndicatorBarChartLayer.INVERT_VALUES_PARAM, true);

        chartPanel.setCategories(categoriesSeries);

//        chartPanel.addCandles("CANDLES", "CANDLES").setData(candles);
//        chartPanel.addBars("VOLUME", "VOLUME").setData(volumes);
//        chartPanel.getChart("VOLUME").setValuesLabelFormatter(new NumberLabelFormatter().withPrecision(0));
//        chartPanel.addDoubleHistogram("BID_ASK_VOLUME", "BID_VOLUME", "ASK_VOLUME");
//        chartPanel.getChart("BID_ASK_VOLUME").setValuesLabelFormatter(new AbsNumberLabelFormatter().withPrecision(0));
//        chartPanel.setData("BID_ASK_VOLUME", "BID_VOLUME", bidVolumes);
//        chartPanel.setData("BID_ASK_VOLUME", "ASK_VOLUME", askVolumes);
//        chartPanel.addSmoothLine("CANDLES", closes.getId()).withColor(Color.BLUE).setData(closes);
//        chartPanel.addSmoothLine("BID_ASK_VOLUME", bidVolumes.getId()).withColor(Color.BLUE).setData(bidVolumes);
//        chartPanel.addPolyLine("BID_ASK_VOLUME", askVolumes.getId()).withColor(Color.MAGENTA).withInvertValues(true).setData(askVolumes);
//        chartPanel.getChart("CANDLES").getOverlays().add(new StaticOverlay("Price", 0));
//        chartPanel.getChart("VOLUME").getOverlays().add(new StaticOverlay("Volume", 0));
//        chartPanel.getChart("BID_ASK_VOLUME").getOverlays().add(new StaticOverlay("Max Bid Volume", 0));
//        chartPanel.getChart("BID_ASK_VOLUME").getOverlays().add(new StaticOverlay("Max Ask Volume", -1));

        chartPanel.setVisibleArea(0, chartPanel.getNumberOfVisibleCategories());
        BarChartPanelHandler handler = new BarChartPanelHandler(candlesObs, chartPanel.getViewport());
        handler.subscribe();

//        chartPanel.addObservableSeries(candlesObs);
        MainChart.chartPanel = chartPanel;

        addMouseClickListener();
    }

    private static void addMouseClickListener() {
        ((BarChartImpl)chartPanel.getChart("CANDLES")).getRootPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==1){
                    final Instant time = Instant.parse("2017-06-13T06:11:00Z");
                    final Candle candle = candles.get(time);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<100; i++){
//                                System.out.println(LocalDateTime.now());
                                ((EditableTSeries)candlesObs).set(time, randomCandle(time, candle.getOpen()));
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Candle candle = candles.get();
                                for (int i = 0; i < 100; i++) {
                                    Instant time = candle.getStartTime().plusSeconds(60 * (i + 1));
                                    candle = randomCandle(time, candle.getClose());
                                    ((EditableTSeries)candlesObs).set(time, candle);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            } catch (ValueException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    private static Candle randomCandle(Instant time, double prevClose){
        double var = 0.01;
        double close = prevClose + (random.nextBoolean()?1:-1) * random.nextDouble()*var;
        double high = Math.max(prevClose, close) + random.nextDouble()*var;
        double low = Math.min(prevClose, close) - random.nextDouble()*var;
        return new Candle(TimeFrame.M1.getInterval(time), prevClose, high, low, close, random.nextInt(5000));
    }

    private static void createTestSeries(){
        eventQueue = new EventQueueImpl();
        TSeriesNodeStorage storage = new TSeriesNodeStorageImpl(TimeFrame.M1);
        categoriesSeries = new TSeriesNodeStorageKeys(eventQueue, storage);
        candles = new TSeriesImpl<>("CANDLES", storage);
        candlesObs = new ObservableTSeriesImpl<>(eventQueue, candles);
        volumes = new CandleVolumeTSeries("Volume", candles);
        closes = new CandleCloseTSeries("Close", candles);
        bidVolumes = new TSeriesImpl<>("Bid volumes", storage);
        askVolumes = new TSeriesImpl<>("Ask volumes", storage);
        trades = new TSeriesImpl<>("Trades", storage);

        Instant time = Instant.parse("2017-06-13T06:00:00Z");
        double prevClose = 120d;
        for(int j=0; j<100; j++){
            Candle candle = randomCandle(time, prevClose);
            if(j%7!=3){
                candles.set(time, candle);
            }
            bidVolumes.set(time, j*10);
            double v = Math.random()*1000;
            askVolumes.set(time, v);
            time = time.plusSeconds(60);
            prevClose = candle.getClose();
        }

        TradeInfoList list = new TradeInfoList();
        Instant t = Instant.parse("2017-06-13T06:05:00Z");
        list.add(new TradeInfo(t.plusSeconds(10), OrderAction.BUY, candles.get(t).getHigh(), 1000L).withOrderId(1L));
        list.add(new TradeInfo(t.plusSeconds(20), OrderAction.BUY, candles.get(t).getLow(), 1000L).withOrderId(2L));
        list.add(new TradeInfo(t.plusSeconds(30), OrderAction.SELL, candles.get(t).getBodyMiddle(), 1000L).withOrderId(3L));
        trades.set(t, list);

        list = new TradeInfoList();
        t = Instant.parse("2017-06-13T06:20:00Z");
        list.add(new TradeInfo(t.plusSeconds(10), OrderAction.BUY, candles.get(t).getHigh(), 1000L).withOrderId(4L));
        list.add(new TradeInfo(t.plusSeconds(20), OrderAction.BUY, candles.get(t).getLow(), 1000L).withOrderId(5L));
        list.add(new TradeInfo(t.plusSeconds(30), OrderAction.SELL, candles.get(t).getBodyMiddle(), 1000L).withOrderId(6L));
        trades.set(t, list);

//        Candle[] arr = {
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-12-31T10:00:00Z")), 150.0, 152.0, 148.0, 151.0, 1000),
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-10-01T12:01:00Z")), 143.0, 145.0, 140.0, 142.0, 5000),
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-10-01T12:02:00Z")), 142.0, 149.0, 141.0, 145.0, 1500),
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-10-01T12:03:00Z")), 145.0, 152.0, 145.0, 150.0, 1700),
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-09-15T18:01:00Z")), 150.0, 156.0, 150.0, 155.0, 1700),
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-09-15T18:02:00Z")), 155.0, 155.0, 149.0, 151.0, 1800),
//                new Candle(TimeFrame.M1.getInterval(Instant.parse("2017-09-15T18:03:00Z")), 151.0, 152.0, 145.0, 148.0, 1900),
//        };
//
//        for(int i = 0; i<arr.length; i++){
//            candles.set(arr[i].getStartTime(), arr[i]);
//        }

        System.out.println(candles.getLength());
    }
}
