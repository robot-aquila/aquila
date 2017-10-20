package ru.prolib.aquila.core.data.tseries.filler;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ValueException;

public interface CandleSeriesAggregator<T> {
	
	void aggregate(EditableTSeries<Candle> series, T item) throws ValueException;

}
