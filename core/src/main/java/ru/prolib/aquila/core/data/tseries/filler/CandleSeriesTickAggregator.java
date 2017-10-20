package ru.prolib.aquila.core.data.tseries.filler;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ValueException;

public class CandleSeriesTickAggregator implements CandleSeriesAggregator<Tick> {
	private static final CandleSeriesAggregator<Tick> instance;
	
	static {
		instance = new CandleSeriesTickAggregator();
	}
	
	public static CandleSeriesAggregator<Tick> getInstance() {
		return instance;
	}
	
	private CandleSeriesTickAggregator() {
		
	}

	@Override
	public void aggregate(EditableTSeries<Candle> series, Tick item) throws ValueException {
		series.lock();
		try {
			Instant time = item.getTime();
			Candle candle = series.get(time);
			if ( candle == null ) {
				candle = new Candle(series.getTimeFrame().getInterval(time),
						item.getPrice(), item.getSize());
			} else {
				candle = candle.addTick(item);
			}
			series.set(time, candle);
		} finally {
			series.unlock();
		}
	}

}
