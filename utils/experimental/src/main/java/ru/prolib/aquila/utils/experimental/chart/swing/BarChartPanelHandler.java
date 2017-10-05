package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeriesEvent;
import ru.prolib.aquila.utils.experimental.chart.BarChartPanel;

/**
 * Created by TiM on 15.09.2017.
 */
public class BarChartPanelHandler<TCategory> implements EventListener {
    private final BarChartPanel<TCategory> chartPanel;
    private final ObservableTSeries<?> series;

    public BarChartPanelHandler(BarChartPanel<TCategory> chartPanel, ObservableTSeries<?> series) {
        this.chartPanel = chartPanel;
        this.series = series;
    }

    public void subscribe(){
        series.onUpdate().addListener(this);
    }

    public void unsubscribe(){
        series.onUpdate().removeListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if(event.isType(series.onUpdate()) && event instanceof TSeriesEvent){
            TSeriesEvent e = (TSeriesEvent) event;
            int from = chartPanel.getFirstVisibleCategory();
            int to = from + chartPanel.getNumberOfVisibleCategories() -1;
            int changedIdx = e.getIndex();
            if(e.isNewInterval()){
                if(series.getLength()-2 == to) { // last item displayed
                    chartPanel.setVisibleArea(from+1, chartPanel.getNumberOfVisibleCategories());
                } else {
                    chartPanel.setVisibleArea(from, chartPanel.getNumberOfVisibleCategories()); // we need update Scrollbar
                }
            } else {
//                if(changedIdx >= from && changedIdx <= to){ // changed item displayed
                    chartPanel.setVisibleArea(from, chartPanel.getNumberOfVisibleCategories()); // we need update current value layer always
//                }
            }
        }

    }
}
