package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeriesEvent;

/**
 * Created by TiM on 15.09.2017.
 */
public class BarChartPanelHandler implements EventListener {
    private final ObservableTSeries<?> series;
    private final BarChartPanelViewport viewport;

    public BarChartPanelHandler(ObservableTSeries<?> series, BarChartPanelViewport viewport) {
        this.series = series;
        this.viewport = viewport;
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
            int from;
            if(viewport.getAutoScroll()){
                from = Integer.MAX_VALUE;
            } else {
                from = viewport.getFirstVisibleCategory();
            }
            viewport.setFirstVisibleCategory(from);
        }
    }
}
