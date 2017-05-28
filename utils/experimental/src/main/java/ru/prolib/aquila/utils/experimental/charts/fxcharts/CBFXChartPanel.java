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
    private CandleChartLayer candles;
    private List<IndicatorChartLayer> indicators = new ArrayList<>();
    private ObservableSeries<Candle>  candleData;
    private VolumeChartLayer volumes;
    private TradeChartLayer trades;
    private StampedListSeries<TradeInfo> tradesData;

    public CBFXChartPanel(ObservableSeries<Candle> candleData) {
        setScrollbar(new JScrollBar(JScrollBar.HORIZONTAL));
        this.candleData = candleData;
        addChart("CANDLES");
        candles = new CandleChartLayer();
        addChartLayer("CANDLES", candles);
        candles.setData(candleData);
        setCategoriesLabelFormatter(new InstantLabelFormatter());
        candleData.onAdd().addListener(this);
        candleData.onSet().addListener(this);
        setCurrentPosition(candleData.getLength());
    }

    public IndicatorChartLayer addSmoothLine(Series<Double> data){
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
        addChartLayer("CANDLES", indicator);
        return indicator;
    }

    public void addVolumes(){
        if(volumes!=null){
            throw new IllegalStateException("Volumes chart is already added");
        }
        addChart("VOLUMES");
        volumes = new VolumeChartLayer();
        addChartLayer("VOLUMES", volumes);
        getChart("VOLUMES").setPrefHeight(200);
        getChart("CANDLES").setPrefHeight(1200);
        volumes.setData(new CandleVolumeSeries(candleData));
        volumes.setCategories(new CandleStartTimeSeries(candleData));
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
        if(event.isType(candleData.onAdd()) || event.isType(candleData.onSet())){
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
        if(event.isType(tradesData.onAdd())){
            refresh();
        }
    }
}
