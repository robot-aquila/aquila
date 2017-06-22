package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.VolumeChartLayer;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

/**
 * Created by TiM on 10.06.2017.
 */
public class Main {

    public static void main(String[] args){
        JFrame main = new JFrame("test");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChartPanel<Instant> chartPanel = new ChartPanel<>();
        chartPanel.setPreferredSize(new Dimension(1230, 900));
        chartPanel.addChart("CANDLES");
        chartPanel.addChart("VOLUMES", 200);
        chartPanel.addChart("CANDLES3", 200);

        main.getContentPane().add(chartPanel);
        main.pack();
        main.setVisible(true);

        SeriesImpl<Candle> series = new SeriesImpl<>();
        Instant time = Instant.parse("2017-06-13T06:00:00Z");
        for(int j=0; j<10; j++){
            for(int i=0; i<5; i++){
                try {
                    series.add(new Candle(TimeFrame.M1.getInterval(time), 110d + i*10, 130d + i*10, 100d + i*10, 120d + i*10, Math.round(Math.random()*1e5+Math.random()*1e4)));
                    time = time.plusSeconds(60);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
            }

            for(int i=4; i>=0; i--){
                try {
                    series.add(new Candle(TimeFrame.M1.getInterval(time), 120d + i*10, 130d + i*10, 100d + i*10, 110d + i*10, Math.round(Math.random()*1e5+Math.random()*1e4)));
                    time = time.plusSeconds(60);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
            }
        }

        CandleChartLayer layer = new CandleChartLayer("CANDLES");
        layer.setData(series);
        chartPanel.getChart("CANDLES").addLayer(layer);
        chartPanel.getChart("CANDLES3").addLayer(layer);

        IndicatorChartLayer highLayer = new IndicatorChartLayer("High");
        highLayer.setColor(Color.BLUE);
        highLayer.setCategories(new CandleStartTimeSeries(series));
        highLayer.setData(new CandleHighSeries(series));
        chartPanel.getChart("CANDLES").addLayer(highLayer);

        IndicatorChartLayer lowLayer = new IndicatorChartLayer("High");
        lowLayer.setColor(Color.BLUE);
        lowLayer.setCategories(new CandleStartTimeSeries(series));
        lowLayer.setData(new CandleLowSeries(series));
        lowLayer.setLineRenderer(new PolyLineRenderer());
        chartPanel.getChart("CANDLES3").addLayer(lowLayer);

        VolumeChartLayer volumes = new VolumeChartLayer("VOLUMES");
        volumes.setColor(Color.cyan);
        volumes.setCategories(new CandleStartTimeSeries(series));
        volumes.setData(new CandleVolumeSeries(series));
        chartPanel.getChart("VOLUMES").addLayer(volumes);


        chartPanel.getChart("CANDLES").getTopAxis().setLabelFormatter(new InstantLabelFormatter());
        chartPanel.getChart("CANDLES").getTopAxis().setShowLabels(true);
        chartPanel.getChart("VOLUMES").getBottomAxis().setShowLabels(false);
        chartPanel.getChart("VOLUMES").getTopAxis().setLabelOrientation(SwingConstants.HORIZONTAL);

        chartPanel.getChart("CANDLES3").getTopAxis().setLabelFormatter(new InstantLabelFormatter());
        chartPanel.getChart("CANDLES3").getTopAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES3").getBottomAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES3").getBottomAxis().setLabelOrientation(SwingConstants.HORIZONTAL);


        chartPanel.getChart("CANDLES").getLeftAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES").getRightAxis().setShowLabels(true);
        chartPanel.getChart("VOLUMES").getLeftAxis().setShowLabels(true);
        chartPanel.getChart("VOLUMES").getRightAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES3").getLeftAxis().setShowLabels(true);
        chartPanel.getChart("CANDLES3").getRightAxis().setShowLabels(true);

        chartPanel.setCurrentPosition(0);
    }
}
