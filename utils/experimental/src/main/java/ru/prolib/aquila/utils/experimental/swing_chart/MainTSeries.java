package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.tseries.*;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSliceImpl;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.AbsNumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.NumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.BarChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.series.LongToNumberSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.series.ToNumberTSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Created by TiM on 10.06.2017.
 */
public class MainTSeries {

    private static Random random = new Random();
    private static TSeriesNodeStorageKeys categoriesSeries;
    private static EditableTSeries<Candle> candles;
    private static ObservableTSeries<Candle> candlesObs;
    private static TSeries<Number> volumes;
    private static EditableTSeries<Number> bidVolumes;
    private static EditableTSeries<Number> askVolumes;
    private static TSeries<Number> closes;
    private static EventQueue eventQueue;

    private static TSeriesChartPanel chartPanel;


    public static void main(String[] args){

        createTestSeries();

        JFrame main = new JFrame("test");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartPanel = new TSeriesChartPanel();
        chartPanel.getRootPanel().setPreferredSize(new Dimension(1230, 900));
        main.getContentPane().add(chartPanel.getRootPanel());
        main.pack();
        main.setVisible(true);


        chartPanel.setCategoriesSeries(categoriesSeries);
        chartPanel.addCandles("CANDLES", "CANDLES").setData(candles);
        chartPanel.addBars("VOLUME", "VOLUME").setData(volumes);
        chartPanel.getChart("VOLUME").setValuesLabelFormatter(new NumberLabelFormatter().withPrecision(0));
        chartPanel.addDoubleHistogram("BID_ASK_VOLUME", "BID_VOLUME", "ASK_VOLUME");
        chartPanel.getChart("BID_ASK_VOLUME").setValuesLabelFormatter(new AbsNumberLabelFormatter().withPrecision(0));
        chartPanel.setData("BID_ASK_VOLUME", "BID_VOLUME", bidVolumes);
        chartPanel.setData("BID_ASK_VOLUME", "ASK_VOLUME", askVolumes);
        chartPanel.addSmoothLine("CANDLES", closes.getId()).withColor(Color.BLUE).setData(closes);
        chartPanel.addSmoothLine("BID_ASK_VOLUME", bidVolumes.getId()).withColor(Color.BLUE).setData(bidVolumes);
        chartPanel.addPolyLine("BID_ASK_VOLUME", askVolumes.getId()).withColor(Color.MAGENTA).withInvertValues(true).setData(askVolumes);
        chartPanel.getChart("CANDLES").getOverlays().add(new StaticOverlay("Price", 0));
        chartPanel.getChart("VOLUME").getOverlays().add(new StaticOverlay("Volume", 0));
        chartPanel.getChart("BID_ASK_VOLUME").getOverlays().add(new StaticOverlay("Max Bid Volume", 0));
        chartPanel.getChart("BID_ASK_VOLUME").getOverlays().add(new StaticOverlay("Max Ask Volume", -1));

        chartPanel.setCurrentPosition(0);
        chartPanel.addObservableSeries(candlesObs);

        addMouseClickListener();
    }

    private static void addMouseClickListener() {
        chartPanel.getChart("CANDLES").getRootPanel().addMouseListener(new MouseAdapter() {
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
                }
                else {
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
        volumes = new ToNumberTSeries(new CandleVolumeTSeries("Volume", candles));
        closes = new ToNumberTSeries(new CandleCloseTSeries("Close", candles));
        bidVolumes = new TSeriesImpl<>("Bid volumes", storage);
        askVolumes = new TSeriesImpl<>("Ask volumes", storage);

        Instant time = Instant.parse("2017-06-13T06:00:00Z");
        double prevClose = 120d;
        for(int j=0; j<40; j++){
            Candle candle = randomCandle(time, prevClose);
            if(j%7!=3){
                candles.set(time, candle);
            }
            bidVolumes.set(time, Math.random()*1000);
            askVolumes.set(time, Math.random()*1000);
            time = time.plusSeconds(60);
            prevClose = candle.getClose();
        }


    }
}
