package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.*;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.NumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.series.LongToNumberSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.series.StampedListSeries;
import ru.prolib.aquila.utils.experimental.swing_chart.series.StampedListTimeSeries;

import javax.swing.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 08.05.2017.
 */
public class CBSwingChartPanel extends ChartPanel<Instant> implements EventListener {

    private CandleChartLayer candles;
    private List<IndicatorChartLayer> indicators = new ArrayList<>();
    private ObservableSeries<Candle>  candleData;
    private BarChartLayer volumes;
    private BidAskVolumeChartLayer bidVolumes, askVolumes;
    private ObservableSeries<Long> bidData, askData;

    private TradeChartLayer trades;
    private StampedListSeries<TradeInfo> tradesData;


    public CBSwingChartPanel() {
        super();
        categoryLabelFormatter = new InstantLabelFormatter();
        valueLabelFormatter = new NumberLabelFormatter();
        Chart<Instant> chart = addChart("CANDLES", 600);
        candles = new CandleChartLayer("CANDLES");
        chart.addLayer(candles);
    }

    public CBSwingChartPanel(ObservableSeries<Candle> candleData) {
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
            volumes.setData(new LongToNumberSeries(new CandleVolumeSeries(candleData)));
            volumes.setCategories(new CandleStartTimeSeries(candleData));
        }
        setCurrentPosition(candleData.getLength());
    }

    public void setBidAskVolumeData(Series<Instant> categories, ObservableSeries<Long> bidData, ObservableSeries<Long> askData){
        if(this.bidData!=null){
            this.bidData.onAdd().removeListener(this);
            this.bidData.onSet().removeListener(this);
        }
        if(this.askData!=null){
            this.askData.onAdd().removeListener(this);
            this.askData.onSet().removeListener(this);
        }

        this.bidData = bidData;
        this.askData = askData;

        bidVolumes.setData(bidData);
        bidVolumes.setCategories(categories);
        askVolumes.setData(askData);
        askVolumes.setCategories(categories);

        if(this.bidData!=null){
            this.bidData.onAdd().addListener(this);
            this.bidData.onSet().addListener(this);
        }
        if(askData!=null){
            this.askData.onAdd().addListener(this);
            this.askData.onSet().addListener(this);
        }
        setCurrentPosition(candleData.getLength());
    }

    public void clearData(){
        if(candleData!=null){
            candleData.onAdd().removeListener(this);
            candleData.onSet().removeListener(this);
        }
        candleData = null;
        if(bidData!=null){
            bidData.onAdd().removeListener(this);
            bidData.onSet().removeListener(this);
        }
        if(askData!=null){
            askData.onAdd().removeListener(this);
            askData.onSet().removeListener(this);
        }
        bidData = null;
        askData = null;
        super.clearData();
        for(IndicatorChartLayer l: indicators){
            for(Chart<Instant> c: charts.values()){
                c.dropLayer(l);
            }
        }
        indicators.clear();
        setCurrentPosition(0);
    }

    public IndicatorChartLayer addSmoothLine(Series<Number> data){
        return addSmoothLine("CANDLES", data);
    }

    public IndicatorChartLayer addSmoothLine(String chartId, Series<Number> data){
        return addLine(chartId, data, new SmoothLineRenderer());
    }

    public IndicatorChartLayer addPolyLine(Series<Number> data){
        return addPolyLine("CANDLES", data);
    }

    public IndicatorChartLayer addPolyLine(String chartId, Series<Number> data){
        return addLine(chartId, data, new PolyLineRenderer());
    }

    private IndicatorChartLayer addLine(String chartId, Series<Number> data, LineRenderer lineRenderer){
        Chart<Instant> chart;
        if(candleData==null){
            throw new IllegalStateException("Candle data not set");
        }
        chart = getChart(chartId);
        if (chart == null) {
            chart = addChart(chartId);
        }
        IndicatorChartLayer indicator = new IndicatorChartLayer(null);
        indicator.setCategories(new CandleStartTimeSeries(candleData));
        indicator.setData(data);
        indicator.setLineRenderer(lineRenderer);
        indicators.add(indicator);
        chart.addLayer(indicator);
        return indicator;
    }

    @Override
    public Chart<Instant> addChart(String id) {
        return addChart(id, null);
    }

    public void addVolumes(){
        if(volumes!=null){
            throw new IllegalStateException("Volumes chart is already added");
        }
        Chart<Instant> chart = addChart("VOLUMES");
        NumberLabelFormatter formatter = new NumberLabelFormatter();
        formatter.withPrecision(0);
        chart.setValuesLabelFormatter(formatter);
        volumes = new BarChartLayer("VOLUMES");
        chart.addLayer(volumes);
        if(candleData!=null){
            volumes.setData(new LongToNumberSeries(new CandleVolumeSeries(candleData)));
            volumes.setCategories(new CandleStartTimeSeries(candleData));
        }
    }

    public void addBidAskVolumes(){
        if(bidVolumes!=null || askVolumes!=null){
            throw new IllegalStateException("Bid/Ask Volumes chart is already added");
        }
        Chart<Instant> chart = addChart("BID_ASK_VOLUMES");
        NumberLabelFormatter formatter = new NumberLabelFormatter();
        formatter.withPrecision(0);
        chart.setValuesLabelFormatter(formatter);

        bidVolumes = new BidAskVolumeChartLayer("BID_VOLUMES", BidAskVolumeChartLayer.TYPE_BID);
        chart.addLayer(bidVolumes);

        askVolumes = new BidAskVolumeChartLayer("ASK_VOLUMES", BidAskVolumeChartLayer.TYPE_ASK);
        chart.addLayer(askVolumes);
    }

    public void addTrades(){
        trades = new TradeChartLayer("TRADES");
        Chart<Instant> chart = getChart("CANDLES");
        if(chart!=null){
            chart.addLayer(trades);
        }
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
        if(candleData!=null && (event.isType(candleData.onAdd()) || event.isType(candleData.onSet()))){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int dummy = getCategories().size()-1;
                    Instant lastChartCategory = null;
                    if ( dummy >= 0 ) {
                        lastChartCategory = getCategories().get(dummy);
                    }
                    if(dummy < 0 || (lastChartCategory!=null && isCategoryDisplayed(lastChartCategory))){
                        setCurrentPosition(getCurrentPosition()+1, event.isType(candleData.onAdd()));
                        return;
                    }
                    if(event.isType(candleData.onAdd())){
                        updateScrollbar(getCategories().size()+1);
                    }
                }
            });
        }
        if(tradesData!=null && event.isType(tradesData.onAdd())){
            setCurrentPosition(getCurrentPosition());
        }
    }
}
