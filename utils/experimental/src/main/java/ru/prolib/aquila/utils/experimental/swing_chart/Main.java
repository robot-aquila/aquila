package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DoubleLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.BidAskVolumeChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.VolumeChartLayer;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.Random;

/**
 * Created by TiM on 10.06.2017.
 */
public class Main {

    private static Random random = new Random();

    public static void main(String[] args){
        JFrame main = new JFrame("test");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChartPanel<Instant> chartPanel = new ChartPanel<>();
        chartPanel.setPreferredSize(new Dimension(1230, 900));
        chartPanel.addChart("CANDLES");
        DoubleLabelFormatter dlf = new DoubleLabelFormatter();
        dlf.setPrecision(0);
        chartPanel.addChart("VOLUMES", 200).setValuesLabelFormatter(dlf);
//        chartPanel.addChart("CANDLES3", 200);
        chartPanel.addChart("BID_ASK_VOLUMES", 200).setValuesLabelFormatter(dlf);

        main.getContentPane().add(chartPanel);
        main.pack();
        main.setVisible(true);

        SeriesImpl<Candle> series = new SeriesImpl<>();
        Instant time = Instant.parse("2017-06-13T06:00:00Z");
        double prevClose = 120d;
        for(int j=0; j<40; j++){
            try {
                Candle candle = randomCandle(time, prevClose);
                series.add(candle);
                time = time.plusSeconds(60);
                prevClose = candle.getClose();
            } catch (ValueException e) {
                e.printStackTrace();
            }
        }

        CandleChartLayer layer = new CandleChartLayer("CANDLES");
        layer.setData(series);
        chartPanel.getChart("CANDLES").addLayer(layer);
//        chartPanel.getChart("CANDLES3").addLayer(layer);

        IndicatorChartLayer highLayer = new IndicatorChartLayer("High");
        highLayer.setColor(Color.BLUE);
        highLayer.setCategories(new CandleStartTimeSeries(series));
        highLayer.setData(new CandleHighSeries(series));
        chartPanel.getChart("CANDLES").addLayer(highLayer);

        IndicatorChartLayer lowLayer = new IndicatorChartLayer("High");
        lowLayer.setColor(Color.BLUE);
        lowLayer.setCategories(new CandleStartTimeSeries(series));
        lowLayer.setData(new CandleLowSeries(series));
        lowLayer.setLineRenderer(new SmoothLineRenderer());
//        chartPanel.getChart("CANDLES").addLayer(lowLayer);

        VolumeChartLayer volumes = new VolumeChartLayer("VOLUMES");
        volumes.setCategories(new CandleStartTimeSeries(series));
        volumes.setData(new CandleVolumeSeries(series));
        chartPanel.getChart("VOLUMES").addLayer(volumes);

        BidAskVolumeChartLayer bidVolumes = new BidAskVolumeChartLayer("BID_VOLUMES", BidAskVolumeChartLayer.TYPE_BID);
        bidVolumes.setCategories(new CandleStartTimeSeries(series));
        bidVolumes.setData(new CandleVolumeSeries(series));
        chartPanel.getChart("BID_ASK_VOLUMES").addLayer(bidVolumes);

        BidAskVolumeChartLayer askVolumes = new BidAskVolumeChartLayer("BID_VOLUMES", BidAskVolumeChartLayer.TYPE_ASK);
        askVolumes.setCategories(new CandleStartTimeSeries(series));
        askVolumes.setData(new CandleVolumeSeries(series));
        chartPanel.getChart("BID_ASK_VOLUMES").addLayer(askVolumes);


        chartPanel.getChart("CANDLES").getTopAxis().setLabelFormatter(new InstantLabelFormatter());
        chartPanel.getChart("CANDLES").getTopAxis().setShowLabels(true);
//        chartPanel.getChart("VOLUMES").getBottomAxis().setShowLabels(false);
//        chartPanel.getChart("VOLUMES").getTopAxis().setLabelOrientation(SwingConstants.HORIZONTAL);

//        chartPanel.getChart("CANDLES3").getTopAxis().setLabelFormatter(new InstantLabelFormatter());
//        chartPanel.getChart("CANDLES3").getTopAxis().setShowLabels(true);
//        chartPanel.getChart("CANDLES3").getBottomAxis().setShowLabels(true);
//        chartPanel.getChart("CANDLES3").getBottomAxis().setLabelOrientation(SwingConstants.HORIZONTAL);


        chartPanel.getChart("CANDLES").getLeftAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES").getRightAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES").setValuesLabelFormatter(new DoubleLabelFormatter());
        chartPanel.getChart("VOLUMES").getLeftAxis().setShowLabels(true);
        chartPanel.getChart("VOLUMES").getRightAxis().setShowLabels(true);
        chartPanel.getChart("BID_ASK_VOLUMES").getLeftAxis().setShowLabels(true);
        chartPanel.getChart("BID_ASK_VOLUMES").getRightAxis().setShowLabels(true);
//        chartPanel.getChart("CANDLES3").getLeftAxis().setShowLabels(true);
//        chartPanel.getChart("CANDLES3").getRightAxis().setShowLabels(true);

        chartPanel.setCurrentPosition(0);
    }

    private static Candle randomCandle(Instant time, double prevClose){
        double var = 1;
        double close = prevClose + (random.nextBoolean()?1:-1) * random.nextDouble()*var;
        double high = Math.max(prevClose, close) + random.nextDouble()*var;
        double low = Math.min(prevClose, close) - random.nextDouble()*var;
        return new Candle(TimeFrame.M1.getInterval(time), prevClose, high, low, close, random.nextInt(5000));
    }
}
