package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.AbsNumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.NumberLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.ChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorageTSeries;

import javax.swing.*;
import java.time.Instant;
import java.util.Map;

/**
 * Created by TiM on 01.09.2017.
 */
public class SDP2ChartPanel extends TSeriesChartPanel implements EventListener {

    private SDP2DataSlice dataSlice;

    public SDP2ChartPanel(String candleDataId) {
        super();
        CandleChartLayer layer = new CandleChartLayer(candleDataId, new ChartLayerDataStorageTSeries<>());
        addLayer("CANDLES", layer);
        Chart chart = getChart("CANDLES");
        chart.getOverlays().add(new StaticOverlay("Price", 0));
    }

    public ChartLayer addVolumes(String dataId){
        ChartLayer<Instant,?> layer = addBars("VOLUMES", dataId);
        Chart<Instant> chart = getChart("VOLUMES");
        chart.setValuesLabelFormatter(new NumberLabelFormatter().withPrecision(0));
        chart.getOverlays().add(new StaticOverlay("Volume", 0));
        return layer;
    }

    public void addBidAskVolumes(String bidVolumeDataId, String askVolumeDataId){
        addBidAskVolumes(bidVolumeDataId, askVolumeDataId, true);
    }

    public void addBidAskVolumes(String bidVolumeDataId, String askVolumeDataId, boolean fixCenter){
        addDoubleHistogram("BID_ASK_VOLUMES", bidVolumeDataId, askVolumeDataId);
        Chart<Instant> chart = getChart("BID_ASK_VOLUMES");
        chart.setValuesLabelFormatter(new AbsNumberLabelFormatter().withPrecision(0));
        chart.getOverlays().add(new StaticOverlay("Bid volume", 0));
        chart.getOverlays().add(new StaticOverlay("Ask volume", -1));
    }

    public SDP2DataSlice getDataSlice() {
        return dataSlice;
    }

    public void setDataSlice(SDP2DataSlice dataSlice) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearObservableSeries();
                SDP2ChartPanel.this.dataSlice = dataSlice;
                setCategoriesSeries(dataSlice.getIntervalStartSeries());
                for(Map.Entry<String, Chart<Instant>> entry: charts.entrySet()){
                    for(ChartLayer<Instant, ?> layer: entry.getValue().getLayers()){
                        setData(entry.getKey(), layer.getId(), dataSlice.getSeries(layer.getId()));
                    }
                }
                addObservableSeries((ObservableTSeries) categoriesSeries);
            }
        });
    }
}
