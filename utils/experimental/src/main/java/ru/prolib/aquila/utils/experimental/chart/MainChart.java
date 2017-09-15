package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.tseries.*;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.CandleBarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.HistogramBarChartLayerInv;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.AbsNumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.NumberLabelFormatter;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.Random;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.BAR_COLOR;

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
        chart.addHistogram(bidVolumes).setColor(Color.GREEN);

        HistogramBarChartLayerInv layer = new HistogramBarChartLayerInv(askVolumes);
        chart.addLayer(layer).setColor(Color.RED);

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

        chartPanel.setVisibleArea(0, 40);

//        chartPanel.addObservableSeries(candlesObs);

        addMouseClickListener();
    }

    private static void addMouseClickListener() {
//        chartPanel.getChart("CANDLES").getRootPanel().addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if(e.getButton()==1){
//                    final Instant time = Instant.parse("2017-06-13T06:11:00Z");
//                    final Candle candle = candles.get(time);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for(int i=0; i<100; i++){
////                                System.out.println(LocalDateTime.now());
//                                ((EditableTSeries)candlesObs).set(time, randomCandle(time, candle.getOpen()));
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//                        }
//                    }).start();
//                }
//                else {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Candle candle = candles.get();
//                                for (int i = 0; i < 100; i++) {
//                                    Instant time = candle.getStartTime().plusSeconds(60 * (i + 1));
//                                    candle = randomCandle(time, candle.getClose());
//                                    ((EditableTSeries)candlesObs).set(time, candle);
//                                    try {
//                                        Thread.sleep(100);
//                                    } catch (InterruptedException e1) {
//                                        e1.printStackTrace();
//                                    }
//                                }
//                            } catch (ValueException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//            }
//        });
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

        Instant time = Instant.parse("2017-06-13T06:00:00Z");
        double prevClose = 120d;
        for(int j=0; j<40; j++){
            Candle candle = randomCandle(time, prevClose);
            if(j%7!=3){
                candles.set(time, candle);
            }
            bidVolumes.set(time, j*10);
            askVolumes.set(time, Math.random()*1000);
            time = time.plusSeconds(60);
            prevClose = candle.getClose();
        }


    }
}