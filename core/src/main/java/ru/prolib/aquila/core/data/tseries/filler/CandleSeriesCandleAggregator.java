package ru.prolib.aquila.core.data.tseries.filler;

import java.time.Instant;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ValueException;

public class CandleSeriesCandleAggregator implements CandleSeriesAggregator<Candle> {
	private static final CandleSeriesCandleAggregator instance;
	
	static {
		instance = new CandleSeriesCandleAggregator();
	}
	
	public static CandleSeriesAggregator<Candle> getInstance() {
		return instance;
	}

	private CandleSeriesCandleAggregator() {
		
	}
	
	@Override
	public void aggregate(EditableTSeries<Candle> series, Candle item) throws ValueException {
		series.lock();
		try {
			Instant time = item.getStartTime();
			Candle candle = series.get(time);
			if ( candle == null ) {
				candle = new CandleBuilder()
						.withTimeFrame(series.getTimeFrame())
						.withTime(item.getStartTime())
						.withOpenPrice(item.getOpen())
						.withHighPrice(item.getHigh())
						.withLowPrice(item.getLow())
						.withClosePrice(item.getClose())
						.withVolume(item.getVolume())
						.buildCandle();
			} else {
				candle = candle.addCandle(item);
			}
			series.set(time, candle);
		} finally {
			series.unlock();
		}
	}

}
