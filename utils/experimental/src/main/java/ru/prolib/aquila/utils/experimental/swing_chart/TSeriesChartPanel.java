package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.AbsNumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.NumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.*;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorageTSeries;

import javax.swing.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.BOTTOM_BAR_COLOR;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TOP_BAR_COLOR;

/**
 * Created by TiM on 01.09.2017.
 */
public class TSeriesChartPanel extends ChartPanel<Instant> implements EventListener {

    protected TSeries<Instant> categoriesSeries;

    private final Set<ObservableTSeries> observableSeries = new HashSet<>();
    private final HashMap<String, ChartLayer> layerByDataId = new HashMap<>();

    public TSeriesChartPanel() {
        super();
        categoryLabelFormatter = new InstantLabelFormatter();
        valueLabelFormatter = new NumberLabelFormatter();
    }

    public TSeriesChartPanel(ObservableTSeries<Instant> categoriesSeries) {
        setCategoriesSeries(categoriesSeries);
    }

    public void setCategoriesSeries(TSeries<Instant> categoriesSeries){
        this.categoriesSeries = categoriesSeries;
        for(Chart<Instant> chart: charts.values()){
            for(ChartLayer<Instant, ?> layer: chart.getLayers()){
                layer.setCategories(this.categoriesSeries);
            }
        }
    }

    public void setData(String chartId, String layerId, TSeries series){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChartLayer<Instant, ?> layer = getLayer(chartId, layerId);
                if(layer == null) {
                    throw new IllegalArgumentException("Layer does not exist");
                }
                layer.setData(series);
            }
        });
    }

    @Override
    public ChartLayer<Instant, ?> addLayer(String chartId, ChartLayer<Instant, ?> layer) {
        ChartLayer<Instant, ?> l = super.addLayer(chartId, layer);
        l.setCategories(categoriesSeries);
        return l;
    }

    public ChartLayer<Instant, Candle> addCandles(String chartId, String layerId){
        ChartLayer<Instant, Candle> layer = new CandleChartLayer(layerId, new ChartLayerDataStorageTSeries<>());
        addLayer(chartId, layer);
        return layer;
    }

    public IndicatorChartLayer addSmoothLine(String chartId, String layerId){
        return addLine(chartId, layerId, new SmoothLineRenderer());
    }

    public IndicatorChartLayer addPolyLine(String chartId, String layerId){
        return addLine(chartId, layerId, new PolyLineRenderer());
    }

    public BarChartLayer addBars(String chartId, String layerId){
        BarChartLayer layer = new BarChartLayer(layerId, new ChartLayerDataStorageTSeries<>());
        addLayer(chartId, layer);
        return layer;
    }

    public void addDoubleHistogram(String chartId, String topLayerId, String bottomLayerId){
        Chart<Instant> chart = getChart(chartId);
        if(chart == null){
            addChart(chartId, 200);
        }
        addBars(chartId, topLayerId).withColor(TOP_BAR_COLOR);
        addBars(chartId, bottomLayerId).withInvertValues(true).withColor(BOTTOM_BAR_COLOR);
    }

    protected IndicatorChartLayer addLine(String chartId, String layerId, LineRenderer lineRenderer){
        IndicatorChartLayer indicator = new IndicatorChartLayer(layerId, new ChartLayerDataStorageTSeries<>());
        indicator.setLineRenderer(lineRenderer);
        return (IndicatorChartLayer) addLayer(chartId, indicator);
    }

    @Override
    protected void updateCategories() {
        if(!SwingUtilities.isEventDispatchThread()){
            throw new IllegalStateException("It should be called from AWT Event queue thread");
        }
        if (categoriesSeries!=null){
            categoriesSeries.lock();
            try {
                categories.clear();

                for(int i=0; i<categoriesSeries.getLength(); i++) {
                    try {
                        categories.add(categoriesSeries.get(i));
                    } catch (ValueException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                categoriesSeries.unlock();
            }
            displayedCategories.clear();
            for(int i=0; i<categories.size(); i++){
                if(i >= getCurrentPosition() && i < getCurrentPosition() + getNumberOfPoints()){
                    displayedCategories.add(categories.get(i));
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof TSeriesEvent){
            TSeriesEvent e = (TSeriesEvent) event;
            if(e.isNewInterval()){
                Instant time = null;
                if(categories.size()>0){
                    time = categories.get(categories.size()-1);
                }
                if(time==null || isCategoryDisplayed(time)){
                    setCurrentPosition(categoriesSeries.getLength()-getNumberOfPoints(), true);
                }
            } else {
                Instant time = e.getInterval().getStart();
                if(isCategoryDisplayed(time)){
                    setCurrentPosition(getCurrentPosition());
                }
            }
        }
    }

    public void addObservableSeries(ObservableTSeries series){
        observableSeries.add(series);
        series.onUpdate().addListener(this);
    }

    public void removeObservableSeries(ObservableTSeries series){
        series.onUpdate().removeListener(this);
        observableSeries.remove(series);
    }

    public void clearObservableSeries(){
        for(ObservableTSeries series: observableSeries){
            removeObservableSeries(series);
        }
    }

 }
