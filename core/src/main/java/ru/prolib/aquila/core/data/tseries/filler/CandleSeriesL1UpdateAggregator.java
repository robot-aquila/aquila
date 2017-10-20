package ru.prolib.aquila.core.data.tseries.filler;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ValueException;

public class CandleSeriesL1UpdateAggregator implements CandleSeriesAggregator<L1Update> {
	private static final CandleSeriesAggregator<L1Update> instance;
	
	static {
		instance = new CandleSeriesL1UpdateAggregator();
	}
	
	public static CandleSeriesAggregator<L1Update> getInstance() {
		return instance;
	}
	
	private CandleSeriesL1UpdateAggregator() {
		
	}

	@Override
	public void aggregate(EditableTSeries<Candle> series, L1Update update) throws ValueException {
		Tick item = update.getTick();
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
