package ru.prolib.aquila.utils.experimental.charts.fxcharts;

import javafx.application.Platform;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.charts.ChartPanel;
import ru.prolib.aquila.utils.experimental.charts.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeInfo;
import ru.prolib.aquila.utils.experimental.charts.layers.VolumeChartLayer;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListSeries;
import ru.prolib.aquila.utils.experimental.charts.series.StampedListTimeSeries;

import javax.swing.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 08.05.2017.
 */
public class CBFXChartPanel extends ChartPanel<Instant> implements EventListener {
    private static final int FIRST_CHART_PREF_HEIGHT = 1200;
    private static final int OTHER_CHART_PREF_HEIGHT = 200;

    private CandleChartLayer candles;
    private List<IndicatorChartLayer> indicators = new ArrayList<>();
    private ObservableSeries<Candle>  candleData;
    private VolumeChartLayer volumes;
    private TradeChartLayer trades;
    private StampedListSeries<TradeInfo> tradesData;

    public CBFXChartPanel() {
        setScrollbar(new JScrollBar(JScrollBar.HORIZONTAL));
        addChart("CANDLES");
        candles = new CandleChartLayer();
        addChartLayer("CANDLES", candles);
        setCategoriesLabelFormatter(new InstantLabelFormatter());
    }

    public CBFXChartPanel(ObservableSeries<Candle> candleData) {
        this();
        setCandleData(candleData);
    }

    public void setCandleData(ObservableSeries<Candle> data){
        if(candleData!=null){
            candleData.onAdd().removeListener(this);
            candleData.onSet().removeListener(this);
        }
        indicators.clear();

        candleData = data;
        candles.setData(candleData);
        if(candleData!=null){
            candleData.onAdd().addListener(this);
            candleData.onSet().addListener(this);
        }
        if(volumes!=null){
            volumes.setData(new CandleVolumeSeries(candleData));
            volumes.setCategories(new CandleStartTimeSeries(candleData));
        }
        setFullRedraw(true);
        setCurrentPosition(candleData.getLength());
    }

    @Override
    public void clearData(){
        if(candleData!=null){
            candleData.onAdd().removeListener(this);
            candleData.onSet().removeListener(this);
        }
        candleData = null;
        removeIndicators("CANDLES");
        super.clearData();
        indicators.clear();
        setCurrentPosition(0);
    }

    public IndicatorChartLayer addSmoothLine(Series<Double> data){
        return addSmoothLine("CANDLES", data);
    }

    public IndicatorChartLayer addSmoothLine(String chartId, Series<Double> data){
        if(candleData==null){
            throw new IllegalStateException("Candle data not set");
        }
        try {
            getChart(chartId);
        } catch (IllegalArgumentException e){
            addChart(chartId);
            getChart(chartId).setPrefHeight(OTHER_CHART_PREF_HEIGHT);
        }

        IndicatorSettings indicatorSettings = new IndicatorSettings(new Calculator() {
            @Override
            public String getId() {
                return data.getId();
            }

            @Override
            public String getName() {
                return data.getId();
            }

            @Override
            public Series calculate(Series data) {
                return data;
            }
        }, "");
        IndicatorChartLayer indicator = new IndicatorChartLayer(indicatorSettings);
        indicator.setCategories(new CandleStartTimeSeries(candleData));
        indicator.setData(data);
        indicators.add(indicator);
        addChartLayer(chartId, indicator);
        return indicator;
    }

    public void addVolumes(){
        if(volumes!=null){
            throw new IllegalStateException("Volumes chart is already added");
        }
        addChart("VOLUMES");
        volumes = new VolumeChartLayer();
        addChartLayer("VOLUMES", volumes);
        getChart("VOLUMES").setPrefHeight(OTHER_CHART_PREF_HEIGHT);
        getChart("CANDLES").setPrefHeight(FIRST_CHART_PREF_HEIGHT);
        if(candleData!=null){
            volumes.setData(new CandleVolumeSeries(candleData));
            volumes.setCategories(new CandleStartTimeSeries(candleData));
        }
    }

    public void addTrades(){
        trades = new TradeChartLayer();
        addChartLayer("CANDLES", trades);
    }

    public void setTradesData(StampedListSeries<TradeInfo> data){
        if(tradesData!=null){
            tradesData.onAdd().removeListeners();
        }
        tradesData = data;
        trades.setData(tradesData);
        trades.setCategories(new StampedListTimeSeries(tradesData));
        tradesData.onAdd().addListener(this);
    }

    @Override
    public void onEvent(Event event) {
    	// А зачем тут onSet обрабатывается?
        if(candleData!=null && (event.isType(candleData.onAdd()) || event.isType(candleData.onSet()))){
            Platform.runLater(()->{
            	int dummy = getCategories().size()-1;
                Instant lastChartCategory = null;
            	if ( dummy >= 0 ) {
	                lastChartCategory = getCategories().get(dummy);
            	}
                if(dummy < 0 || (lastChartCategory!=null && isCategoryDisplayed(lastChartCategory))){
                    setCurrentPosition(getCurrentPosition()+1);
                }
            });
        }
        if(tradesData!=null && event.isType(tradesData.onAdd())){
            refresh();
        }
    }
}
