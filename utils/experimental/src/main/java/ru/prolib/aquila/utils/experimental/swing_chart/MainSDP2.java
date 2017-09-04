package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSliceImpl;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.util.Random;

/**
 * Created by TiM on 10.06.2017.
 */
public class MainSDP2 {

    private static Random random = new Random();

    public static void main(String[] args){
        JFrame main = new JFrame("test");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SDP2ChartPanel chartPanel = new SDP2ChartPanel("OHLC");
        chartPanel.getRootPanel().setPreferredSize(new Dimension(1230, 900));

        main.getContentPane().add(chartPanel.getRootPanel());
        main.pack();
        main.setVisible(true);

        SDP2DataSlice<SDP2Key> slice = new SDP2DataSliceImpl(new SDP2Key(TimeFrame.M1, new Symbol("AAPL")), new EventQueueImpl());
        final EditableTSeries<Candle> series = slice.createSeries("OHLC", true);
        EditableTSeries<Long> volumeSeries = slice.createSeries("VOLUME", false);
        EditableTSeries<Long> bidVolumeSeries = slice.createSeries("BID_VOLUME", false);
        EditableTSeries<Long> askVolumeSeries = slice.createSeries("ASK_VOLUME", false);
        EditableTSeries<Long> longLineSeries = slice.createSeries("LONG_LINE", false);
        EditableTSeries<Double> highSeries = slice.createSeries("HIGH", false);
        Instant time = Instant.parse("2017-06-13T06:00:00Z");
        double prevClose = 120d;
        for(int j=0; j<40; j++){
            Candle candle = randomCandle(time, prevClose);
            if(j%7!=3){
                series.set(time, candle);
                highSeries.set(time, candle.getHigh());
            }
            volumeSeries.set(time, new Double(Math.random()*1000).longValue());
            bidVolumeSeries.set(time, new Double(Math.random()*1000).longValue());
            askVolumeSeries.set(time, new Double(Math.random()*1000).longValue());
            longLineSeries.set(time, new Double(Math.random()*1000).longValue());
            time = time.plusSeconds(60);
            prevClose = candle.getClose();
        }


        chartPanel.addVolumes("VOLUME");
        chartPanel.addBidAskVolumes("BID_VOLUME", "ASK_VOLUME");
        chartPanel.addSmoothLine("CANDLES", "HIGH").withColor(Color.BLUE);
        chartPanel.addPolyLine("BID_ASK_VOLUMES", "LONG_LINE").withColor(Color.GREEN).withInvertValues(true);
        chartPanel.setDataSlice(slice);
        chartPanel.setCurrentPosition(0);
        chartPanel.getChart("CANDLES").getOverlays().add(new Overlay("Price", 0));
        chartPanel.getChart("VOLUMES").getOverlays().add(new Overlay("Volume", 0));

        Chart chart = chartPanel.getChart("BID_ASK_VOLUMES");
        chart.getOverlays().add(new Overlay("Bid volume", 0));
        chart.getOverlays().add(new Overlay("Ask volume", -1));

        chartPanel.getChart("CANDLES").getRootPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==1){
                    final Instant time = Instant.parse("2017-06-13T06:11:00Z");
                    final Candle candle = series.get(time);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<1; i++){
                                series.set(time, randomCandle(time, candle.getOpen()));
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
                                Candle candle = series.get();
                                for (int i = 0; i < 100; i++) {
                                    Instant time = candle.getStartTime().plusSeconds(60 * (i + 1));
                                    candle = randomCandle(time, candle.getClose());
                                    series.set(time, candle);
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
}
