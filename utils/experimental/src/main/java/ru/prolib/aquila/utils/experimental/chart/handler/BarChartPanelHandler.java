package ru.prolib.aquila.utils.experimental.chart.handler;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeriesEvent;
import ru.prolib.aquila.utils.experimental.chart.BarChartPanel;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;

/**
 * Created by TiM on 15.09.2017.
 */
public class BarChartPanelHandler implements EventListener {
    private final ObservableTSeries<?> series;
    private final BarChartPanel panel;

    public BarChartPanelHandler(ObservableTSeries<?> series, BarChartPanel panel) {
        this.series = series;
        this.panel = panel;
    }

    public void subscribe(){
        series.onUpdate().addListener(this);
    }

    public void unsubscribe(){
        series.onUpdate().removeListener(this);
    }

	@Override
	public void onEvent(Event event) {
		if ( event.isType(series.onUpdate()) && event instanceof TSeriesEvent ) {
			CategoryAxisViewport viewport = panel.getCategoryAxisViewport();
			int number = series.getLength();
			viewport.setCategoryRangeByFirstAndNumber(0, number);
			panel.paint();
			//int from;
			//if(viewport.getAutoScroll()){
			//    int cnt = series.getLength();
			//    from = cnt < viewport.getNumberOfVisibleCategories()?0:cnt - viewport.getNumberOfVisibleCategories();
			//} else {
			//    from = viewport.getFirstVisibleCategory();
			//}
			//viewport.setFirstVisibleCategory(from);
		}
	}
}
