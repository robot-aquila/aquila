package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.NumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.BarChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.BidAskVolumeChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.series.DoubleToNumberSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.series.LongToNumberSeries;

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
        chartPanel.getRootPanel().setPreferredSize(new Dimension(1230, 900));
        Chart chart = chartPanel.addChart("CANDLES");
//        chart.setMinValueInterval(120d);
//        chart.setMaxValueInterval(115d);
        NumberLabelFormatter dlf = new NumberLabelFormatter();
        dlf.withPrecision(0);
        chartPanel.addChart("VOLUMES", 200).setValuesLabelFormatter(dlf);
//        chartPanel.addChart("CANDLES3", 200);
        chartPanel.addChart("BID_ASK_VOLUMES", 200).setValuesLabelFormatter(dlf);

        main.getContentPane().add(chartPanel.getRootPanel());
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
        highLayer.withColor(Color.BLUE);
        highLayer.setCategories(new CandleStartTimeSeries(series));
        highLayer.setData(new DoubleToNumberSeries(new CandleHighSeries(series){
            @Override
            public Double get(int index) throws ValueException {
                if(index%10==0){
                    return  null;
                }
                return super.get(index);
            }
        }));
        chartPanel.getChart("CANDLES").addLayer(highLayer);

        IndicatorChartLayer lowLayer = new IndicatorChartLayer("Low");
        lowLayer.withColor(Color.BLUE);
        lowLayer.setCategories(new CandleStartTimeSeries(series));
        lowLayer.setData(new DoubleToNumberSeries(new CandleLowSeries(series){
            @Override
            public Double get(int index) throws ValueException {
                if(index%5==0){
                    return  null;
                }
                return super.get(index);
            }
        }));
        lowLayer.setLineRenderer(new PolyLineRenderer());
        chartPanel.getChart("CANDLES").addLayer(lowLayer);

        BarChartLayer volumes = new BarChartLayer("VOLUMES");
        volumes.setCategories(new CandleStartTimeSeries(series));
        volumes.setData(new LongToNumberSeries(new CandleVolumeSeries(series)));
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
        chartPanel.getChart("CANDLES").setValuesLabelFormatter(new NumberLabelFormatter());
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
