package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeriesEvent;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DoubleLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.*;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorageTSeries;

import javax.swing.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.BID_ASK_VOLUME_CHARTS_HEIGHT;

/**
 * Created by TiM on 01.09.2017.
 */
public class SDP2ChartPanel extends ChartPanel<Instant> implements EventListener {

    private ObservableTSeries<Instant> categoriesSeries;
    private SDP2DataSlice dataSlice;
    private CandleChartLayer candleLayer;
    private LabelFormatter labelFormatter = new InstantLabelFormatter();
    private LabelFormatter doubleLabelFormatter = new DoubleLabelFormatter();
    private final Set<String> observableSeriesDataId = new HashSet<>();
    private final HashMap<String, ChartLayer> layerByDataId = new HashMap<>();

    public SDP2ChartPanel(String candleDataId) {
        super();
        candleLayer = new CandleChartLayer("CANDLES", new ChartLayerDataStorageTSeries<>());
        addLayer("CANDLES", candleDataId, candleLayer, false);
    }

    @Override
    public Chart addChart(String id, Integer height) throws IllegalArgumentException {
        Chart chart = super.addChart(id, height);
        updateLabelsConfig(labelFormatter, doubleLabelFormatter);
        return chart;
    }

    public IndicatorChartLayer addSmoothLine(String chartId, String dataId){
        return addLine(chartId, dataId, new SmoothLineRenderer());
    }

    public IndicatorChartLayer addPolyLine(String chartId, String dataId){
        return addLine(chartId, dataId, new PolyLineRenderer());
    }

    public ChartLayer addVolumes(String dataId){
        if(getChart("VOLUMES") != null){
            throw new IllegalStateException("Volumes chart is already added");
        }
        Chart chart = addChart("VOLUMES");
        DoubleLabelFormatter formatter = new DoubleLabelFormatter();
        formatter.setPrecision(0);
        chart.setValuesLabelFormatter(formatter);
        ChartLayer layer = new VolumeChartLayer("VOLUMES", new ChartLayerDataStorageTSeries<>());
        return addLayer("VOLUMES", dataId, layer, false);
    }

    public void addBidAskVolumes(String bidVolumeDataId, String askVolumeDataId){
        if(getChart("BID_ASK_VOLUMES") != null){
            throw new IllegalStateException("Bid/Ask Volumes chart is already added");
        }
        Chart chart = addChart("BID_ASK_VOLUMES", BID_ASK_VOLUME_CHARTS_HEIGHT);
        DoubleLabelFormatter formatter = new DoubleLabelFormatter();
        formatter.setPrecision(0);
        chart.setValuesLabelFormatter(formatter);
        ChartLayer layer = new BidAskVolumeChartLayer("BID_VOLUMES", new ChartLayerDataStorageTSeries<>(), BidAskVolumeChartLayer.TYPE_BID);
        addLayer("BID_ASK_VOLUMES", bidVolumeDataId, layer, false);
        layer = new BidAskVolumeChartLayer("ASK_VOLUMES", new ChartLayerDataStorageTSeries<>(), BidAskVolumeChartLayer.TYPE_ASK);
        addLayer("BID_ASK_VOLUMES", askVolumeDataId, layer, false);
    }

    private IndicatorChartLayer addLine(String chartId, String dataId, LineRenderer lineRenderer){
        IndicatorChartLayer indicator = new IndicatorChartLayer(null, new ChartLayerDataStorageTSeries<>());
        indicator.setLineRenderer(lineRenderer);
        return (IndicatorChartLayer) addLayer(chartId, dataId, indicator, false);
    }

    private ChartLayer addLayer(String chartId, String dataId, ChartLayer<Instant, ?> layer, boolean isObservable){
        Chart<Instant> chart = getChart(chartId);
        if (chart == null) {
            chart = addChart(chartId);
        }
        if(dataSlice!=null){
            layer.setCategories(categoriesSeries);

            if(isObservable){
                ObservableTSeries series = dataSlice.getObservableSeries(dataId);
                series.onUpdate().addListener(this);
                layer.setData(series);
                observableSeriesDataId.add(dataId);
            } else {
                layer.setData(dataSlice.getSeries(dataId));
            }
        }
        chart.addLayer(layer);
        layerByDataId.put(dataId, layer);
        return layer;
    }

    @Override
    protected void updateCategories() {
        if(!SwingUtilities.isEventDispatchThread()){
            throw new IllegalStateException("It should be called from AWT Event queue thread");
        }
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

    public SDP2DataSlice getDataSlice() {
        return dataSlice;
    }

    public void setDataSlice(SDP2DataSlice dataSlice) {
        if(categoriesSeries!=null){
            categoriesSeries.onUpdate().removeListener(this);
        }
        for(Map.Entry<String, ChartLayer> entry: layerByDataId.entrySet()){
            if(observableSeriesDataId.contains(entry.getKey())){
                ObservableTSeries series = (ObservableTSeries) entry.getValue().getData();
                if(series!=null){
                    series.onUpdate().removeListener(this);
                }
            }
        }
        this.dataSlice = dataSlice;
        categoriesSeries = dataSlice.getIntervalStartSeries();
        categoriesSeries.onUpdate().addListener(this);
        for(Map.Entry<String, ChartLayer> entry: layerByDataId.entrySet()){
            entry.getValue().setCategories(categoriesSeries);
            if(observableSeriesDataId.contains(entry.getKey())){
                ObservableTSeries series = dataSlice.getObservableSeries(entry.getKey());
                series.onUpdate().addListener(this);
                entry.getValue().setData(series);
            } else {
                entry.getValue().setData(dataSlice.getSeries(entry.getKey()));
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

    @Override
    public void setCurrentPosition(int position, boolean newCategoryAdded) {
//        System.out.println(LocalDateTime.now());
        super.setCurrentPosition(position, newCategoryAdded);
    }
}
