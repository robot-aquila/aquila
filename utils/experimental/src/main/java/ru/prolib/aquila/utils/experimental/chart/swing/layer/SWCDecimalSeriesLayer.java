package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;

abstract public class SWCDecimalSeriesLayer extends SWAbstractLayer {
	protected Series<CDecimal> series;

	public SWCDecimalSeriesLayer(Series<CDecimal> series) {
		super(series.getId());
		this.series = series;
	}
	
	public void setSeries(Series<CDecimal> series) {
		this.series = series;
		this.id = series.getId();
	}

	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		if ( ! isVisible() || series == null ) {
			return null;
		}
		series.lock();
		try {
			CDecimal min = null, max = null;
			for ( int i = 0; i < number; i ++ ) {
				try {
					CDecimal x = series.get(first + i);
					if ( x != null ) {
						min = x.min(min);
						max = x.max(max);
					}
				} catch ( ValueException e ) { }
			}
			if ( min != null ) {
				return new Range<CDecimal>(min, max);
			}
		} finally {
			series.unlock();
		}
		return null;
	}

}
